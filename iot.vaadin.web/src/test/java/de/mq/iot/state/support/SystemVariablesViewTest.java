package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;

import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;
import de.mq.iot.state.State;
import de.mq.iot.state.StateService;
import de.mq.iot.state.support.StateModel.Events;
import de.mq.iot.state.support.StateModel.ValidationErrors;
import de.mq.iot.support.ButtonBox;

class SystemVariablesViewTest {

	private static final String STATE_VALUE = Double.valueOf(0).toString();
	private static final String STATE_NAME = "name";
	private static final String I18N_INFO_LABEL_VALUE_BOOLEAN = "Boolean-Variable id=4711 ändern";
	private static final String I18N_INFO_LABEL_VALUE_ITEM = "List-Variable id=4711 ändern";
	private static final String I18N_INFO_LABEL_VALUE_DOUBLE = "Double-Variable id=4711 ändern";
	private static final String I18N_VALUE_COLUMN = "systemvariables_value_column";
	private static final String I18N_NAME_COLUMN = "systemvariables_name_column";
	private static final String I18N_NAME = "systemvariables_name";
	private static final String I18N_LASTUPDATE = "systemvariables_date";
	private static final String I18N_VALUE_LABEL = "systemvariables_value";
	private static final String I18N_RESET_BUTTON = "systemvariables_reset";
	private static final String I18N_SAVE_BUTTON = "systemvariables_save";
	private final StateService stateService = Mockito.mock(StateService.class);
	private final StateModel stateModel = Mockito.mock(StateModel.class);
	@SuppressWarnings("unchecked")
	private final Converter<State<?>, String> converter = Mockito.mock(Converter.class);
	
	private final ResourceBundleMessageSource messageSource = Mockito.mock(ResourceBundleMessageSource.class);

	private SystemVariablesView systemVariablesView;

	private State<Boolean> workingDayState = new BooleanStateImpl(4711, "WorkingDay", LocalDateTime.now());

	private State<Integer> itemState = new ItemsStateImpl(4711, "TimeZones", LocalDateTime.now());
	private State<Double> doubleState = new DoubleStateImpl(4711, "TimeZones", LocalDateTime.now());

	private final Map<String, Object> fields = new HashMap<>();

	private final Map<StateModel.Events, Observer> observers = new HashMap<>();

	private final SimpleNotificationDialog notificationDialog = Mockito.mock(SimpleNotificationDialog.class);
	
	private final Subject<?, ?> subject = Mockito.mock(Subject.class);

	
	
	
	@BeforeEach
	void setup() {
	
		Mockito.when(converter.convert(Mockito.any())).thenReturn(STATE_VALUE);
		Mockito.when(stateModel.selectedState()).thenReturn(Optional.of(workingDayState));
	
		final Map<Integer, String> values = new HashMap<>();
		values.put(Integer.valueOf(0), "Winter");
		values.put(Integer.valueOf(1), "Summer");
		ReflectionTestUtils.setField(itemState, "items", values);

		Mockito.doReturn(Locale.GERMAN).when(stateModel).locale();
		Arrays.asList(I18N_SAVE_BUTTON, I18N_RESET_BUTTON, I18N_NAME, I18N_LASTUPDATE, I18N_VALUE_LABEL, I18N_NAME_COLUMN, I18N_VALUE_COLUMN).forEach(key -> {
			Mockito.doReturn(key).when(messageSource).getMessage(key, null, "???", Locale.GERMAN);
		});

		Mockito.doReturn(Arrays.asList(workingDayState, itemState, doubleState)).when(stateService).states();
		Mockito.doAnswer(answer -> {

			final StateModel.Events event = (Events) answer.getArguments()[0];
			final Observer observer = (Observer) answer.getArguments()[1];
			observers.put(event, observer);
			return null;

		}).when(stateModel).register(Mockito.any(), Mockito.any());

		
		
		systemVariablesView = new SystemVariablesView(stateService, stateModel, converter, messageSource, notificationDialog,  new ButtonBox(subject));;

		Arrays.asList(SystemVariablesView.class.getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).forEach(field -> fields.put(field.getName(), ReflectionTestUtils.getField(systemVariablesView, field.getName())));

		observers.get(Events.ChangeLocale).process();
	}

	@Test
	void init() {
		assertEquals(24, fields.size());
		assertEquals(2, observers.size());

		Mockito.verify(stateModel).notifyObservers(Events.ChangeLocale);

		final Button saveButton = (Button) fields.get("saveButton");
		assertEquals(I18N_SAVE_BUTTON, saveButton.getText());
		assertFalse(saveButton.isEnabled());
		
		assertFalse(saveButton.isVisible());

		final Button resetButton = (Button) fields.get("resetButton");
		assertEquals(I18N_RESET_BUTTON, resetButton.getText());
		assertFalse(resetButton.isEnabled());

		final FormItem itemTextField = (FormItem) fields.get("textFieldFormItem");

		assertTrue(itemTextField.isVisible());
		final TextField valueTextField = (TextField) fields.get("valueTextField");
		assertTrue(valueTextField.isReadOnly());
		assertTrue(valueTextField.getValue().isEmpty());
		assertEquals(I18N_VALUE_LABEL, itemTextField.getElement().getChild(1).getText());

		final TextField lastUpdateTextField = (TextField) fields.get("lastUpdateTextField");
		assertTrue(lastUpdateTextField.isReadOnly());
		final FormLayout formLayout = (FormLayout) fields.get("formLayout");
		assertEquals(I18N_LASTUPDATE, formLayout.getElement().getChild(1).getChild(1).getText());
		assertTrue(lastUpdateTextField.isEmpty());

		final TextField nameTextField = (TextField) fields.get("nameTextField");
		assertTrue(nameTextField.isReadOnly());
		assertTrue(nameTextField.getValue().isEmpty());
		assertEquals(I18N_NAME, formLayout.getElement().getChild(0).getChild(1).getText());

		@SuppressWarnings("unchecked")
		final Grid<State<Boolean>> grid = (Grid<State<Boolean>>) fields.get("grid");

		assertEquals(2, grid.getColumns().size());

		final ListDataProvider<?> data = (ListDataProvider<?>) grid.getDataProvider();
		assertEquals(3, data.getItems().size());
		assertEquals(workingDayState, ((List<?>) data.getItems()).get(0));
		assertEquals(itemState, ((List<?>) data.getItems()).get(1));
		assertEquals(doubleState, ((List<?>) data.getItems()).get(2));

		final Label nameColumn = (Label) fields.get("nameColumnLabel");
		assertEquals(I18N_NAME_COLUMN, nameColumn.getText());

		final Label valueColumn = (Label) fields.get("valueColumnLabel");
		assertEquals(I18N_VALUE_COLUMN, valueColumn.getText());

		final Label stateInfoLabel = (Label) fields.get("stateInfoLabel");
		assertTrue(stateInfoLabel.getText().isEmpty());

	}

	@Test
	void selectRow() {
		
		@SuppressWarnings("unchecked")
		final Grid<State<Boolean>> grid = (Grid<State<Boolean>>) fields.get("grid");

		grid.select(workingDayState);

		Mockito.verify(stateModel).assign(workingDayState);

		Mockito.when(stateModel.selectedState()).thenReturn(Optional.of(workingDayState));
		final String[] parameters = new String[] { workingDayState.getClass().getSimpleName(), "4711", "" };
		Mockito.doReturn(parameters).when(stateModel).stateInfoParameters();
		Mockito.doReturn(I18N_INFO_LABEL_VALUE_BOOLEAN).when(messageSource).getMessage(SystemVariablesView.I18N_INFO_LABEL_PATTERN, parameters, "???", Locale.GERMAN);
		Mockito.when(stateModel.isChangeVariableAllowed()).thenReturn(true);
		
		workingDayState.assign(true);
		observers.get(Events.AssignState).process();

		final Button saveButton = (Button) fields.get("saveButton");
		assertEquals(I18N_SAVE_BUTTON, saveButton.getText());
		assertTrue(saveButton.isEnabled());

		final Button resetButton = (Button) fields.get("resetButton");
		assertEquals(I18N_RESET_BUTTON, resetButton.getText());
		assertTrue(resetButton.isEnabled());

		final TextField nameTextField = (TextField) fields.get("nameTextField");
		assertTrue(nameTextField.isReadOnly());

		assertEquals(workingDayState.name(), nameTextField.getValue());

		final TextField lastUpdateTextField = (TextField) fields.get("lastUpdateTextField");
		assertTrue(lastUpdateTextField.isReadOnly());

		assertEquals(workingDayState.lastupdate().toString(), lastUpdateTextField.getValue());

		final FormItem itemTextField = (FormItem) fields.get("textFieldFormItem");

		assertFalse(itemTextField.isVisible());
		final TextField valueTextField = (TextField) fields.get("valueTextField");
		assertTrue(valueTextField.isReadOnly());
		assertTrue(valueTextField.getValue().isEmpty());

		final FormItem itemComboBox = (FormItem) fields.get("comboBoxFormItem");
		assertTrue(itemComboBox.isVisible());

		final ComboBox<?> valueComboBox = (ComboBox<?>) fields.get("valueComboBox");
		assertFalse(valueComboBox.isReadOnly());
		assertEquals(Boolean.TRUE, valueComboBox.getValue());

		final Label stateInfoLabel = (Label) fields.get("stateInfoLabel");

		assertEquals(I18N_INFO_LABEL_VALUE_BOOLEAN, stateInfoLabel.getText());
		
		
		assertTrue(saveButton.isVisible());

	}
	
	@Test
	void isChangeVariableAllowed() {
		Mockito.when(stateModel.selectedState()).thenReturn(Optional.of(workingDayState));
		@SuppressWarnings("unchecked")
		final Grid<State<Boolean>> grid = (Grid<State<Boolean>>) fields.get("grid");
		assertNotNull(grid);
		final Button saveButton = (Button) fields.get("saveButton");
		assertNotNull(saveButton);
		assertEquals(I18N_SAVE_BUTTON, saveButton.getText());
		grid.select(workingDayState);
		
		observers.get(Events.AssignState).process();
		assertFalse(saveButton.isVisible());
		
		Mockito.when(stateModel.isChangeVariableAllowed()).thenReturn(true);
		observers.get(Events.AssignState).process();
		assertTrue(saveButton.isVisible());
	}

	@Test
	void selectListValueRow() {

		@SuppressWarnings("unchecked")
		final Grid<State<?>> grid = (Grid<State<?>>) fields.get("grid");

		grid.select(itemState);

		Mockito.verify(stateModel).assign(itemState);

		Mockito.when(stateModel.selectedState()).thenReturn(Optional.of(itemState));
		final String[] parameters = new String[] { itemState.getClass().getSimpleName(), "4711", "" };
		Mockito.doReturn(parameters).when(stateModel).stateInfoParameters();
		Mockito.doReturn(I18N_INFO_LABEL_VALUE_ITEM).when(messageSource).getMessage(SystemVariablesView.I18N_INFO_LABEL_PATTERN, parameters, "???", Locale.GERMAN);
		workingDayState.assign(true);
		observers.get(Events.AssignState).process();

		final TextField lastUpdateTextField = (TextField) fields.get("lastUpdateTextField");
		assertTrue(lastUpdateTextField.isReadOnly());

		assertEquals(workingDayState.lastupdate().toString(), lastUpdateTextField.getValue());

		final FormItem itemTextField = (FormItem) fields.get("textFieldFormItem");

		assertFalse(itemTextField.isVisible());
		final TextField valueTextField = (TextField) fields.get("valueTextField");
		assertTrue(valueTextField.isReadOnly());
		assertTrue(valueTextField.getValue().isEmpty());

		final FormItem itemComboBox = (FormItem) fields.get("comboBoxFormItem");
		assertTrue(itemComboBox.isVisible());

		final ComboBox<?> valueComboBox = (ComboBox<?>) fields.get("valueComboBox");
		assertFalse(valueComboBox.isReadOnly());
		assertEquals(0, valueComboBox.getValue());

		final Label stateInfoLabel = (Label) fields.get("stateInfoLabel");

		assertEquals(I18N_INFO_LABEL_VALUE_ITEM, stateInfoLabel.getText());

	}

	@Test
	void selectDoubleRow() {

		@SuppressWarnings("unchecked")
		final Grid<State<?>> grid = (Grid<State<?>>) fields.get("grid");

		grid.select(doubleState);

		Mockito.verify(stateModel).assign(doubleState);

		
		
		Mockito.when(stateModel.selectedState()).thenReturn(Optional.of(doubleState));
		final String[] parameters = new String[] { doubleState.getClass().getSimpleName(), "4711", "" };
		Mockito.doReturn(parameters).when(stateModel).stateInfoParameters();
		Mockito.doReturn(I18N_INFO_LABEL_VALUE_DOUBLE).when(messageSource).getMessage(SystemVariablesView.I18N_INFO_LABEL_PATTERN, parameters, "???", Locale.GERMAN);
		workingDayState.assign(true);
		observers.get(Events.AssignState).process();

		final TextField lastUpdateTextField = (TextField) fields.get("lastUpdateTextField");
		assertTrue(lastUpdateTextField.isReadOnly());

		assertEquals(workingDayState.lastupdate().toString(), lastUpdateTextField.getValue());

		final FormItem itemTextField = (FormItem) fields.get("textFieldFormItem");

		assertTrue(itemTextField.isVisible());
		final TextField valueTextField = (TextField) fields.get("valueTextField");
		assertFalse(valueTextField.isReadOnly());
		assertEquals("0.0", valueTextField.getValue());

		final FormItem itemComboBox = (FormItem) fields.get("comboBoxFormItem");
		assertFalse(itemComboBox.isVisible());

		final ComboBox<?> valueComboBox = (ComboBox<?>) fields.get("valueComboBox");
		assertFalse(valueComboBox.isReadOnly());
		assertTrue(valueComboBox.isEmpty());

		final Label stateInfoLabel = (Label) fields.get("stateInfoLabel");

		assertEquals(I18N_INFO_LABEL_VALUE_DOUBLE, stateInfoLabel.getText());
	}

	@Test
	void saveButtonClickBooleanState() {
		@SuppressWarnings("unchecked")
		final Grid<State<?>> grid = (Grid<State<?>>) fields.get("grid");
		grid.select(workingDayState);

		observers.get(Events.AssignState).process();

		@SuppressWarnings("unchecked")
		final ComboBox<Boolean> valueComboBox = (ComboBox<Boolean>) fields.get("valueComboBox");

		assertEquals(Boolean.FALSE, valueComboBox.getValue());

		valueComboBox.setValue(Boolean.TRUE);

		final Button saveButton = (Button) fields.get("saveButton");
		Mockito.doReturn(ValidationErrors.Ok).when(stateModel).validate(Boolean.TRUE);
		Mockito.doReturn(workingDayState).when(stateModel).convert(Boolean.TRUE);

		assertTrue(grid.getSelectionModel().getFirstSelectedItem().isPresent());
		listener(saveButton).onComponentEvent(null);

		Mockito.verify(stateModel).validate(Boolean.TRUE);
		Mockito.verify(stateModel).convert(Boolean.TRUE);
		Mockito.verify(notificationDialog, Mockito.times(0)).showError(Mockito.any());
		Mockito.verify(stateService).update(workingDayState);
		assertFalse(grid.getSelectionModel().getFirstSelectedItem().isPresent());

	}

	@SuppressWarnings("unchecked")
	private ComponentEventListener<?> listener(final Button saveButton) {
		final ComponentEventBus eventBus = (ComponentEventBus) ReflectionTestUtils.getField(saveButton, "eventBus");
		final Map<Class<?>, ?> map = (Map<Class<?>, ?>) ReflectionTestUtils.getField(eventBus, "componentEventData");
		return DataAccessUtils.requiredSingleResult((Collection<ComponentEventListener<?>>) ReflectionTestUtils.getField(map.values().iterator().next(), "listeners"));
	}

	@Test
	void saveButtonClickItemState() {

		Mockito.doReturn(Optional.of(itemState)).when(stateModel).selectedState();
		@SuppressWarnings("unchecked")
		final Grid<State<?>> grid = (Grid<State<?>>) fields.get("grid");
		grid.select(itemState);

		observers.get(Events.AssignState).process();

		@SuppressWarnings("unchecked")
		final ComboBox<Integer> valueComboBox = (ComboBox<Integer>) fields.get("valueComboBox");

		assertEquals(Integer.valueOf(0), valueComboBox.getValue());

		final Button saveButton = (Button) fields.get("saveButton");
		Mockito.doReturn(ValidationErrors.Ok).when(stateModel).validate(0);
		Mockito.doReturn(itemState).when(stateModel).convert(0);

		assertTrue(grid.getSelectionModel().getFirstSelectedItem().isPresent());
		listener(saveButton).onComponentEvent(null);

		Mockito.verify(stateModel).validate(0);
		Mockito.verify(stateModel).convert(0);
		Mockito.verify(notificationDialog, Mockito.times(0)).showError(Mockito.any());
		Mockito.verify(stateService).update(itemState);
		assertFalse(grid.getSelectionModel().getFirstSelectedItem().isPresent());

	}

	@Test
	void saveButtonClickDouble() {

		Mockito.doReturn(Optional.of(doubleState)).when(stateModel).selectedState();
		@SuppressWarnings("unchecked")
		final Grid<State<?>> grid = (Grid<State<?>>) fields.get("grid");
		grid.select(doubleState);

		observers.get(Events.AssignState).process();

		final TextField valueTextField = (TextField) fields.get("valueTextField");

		assertEquals("0.0", valueTextField.getValue());
		valueTextField.setValue("47.11");

		final Button saveButton = (Button) fields.get("saveButton");
		Mockito.doReturn(ValidationErrors.Ok).when(stateModel).validate("47.11");
		Mockito.doReturn(doubleState).when(stateModel).convert("47.11");

		assertTrue(grid.getSelectionModel().getFirstSelectedItem().isPresent());
		listener(saveButton).onComponentEvent(null);

		Mockito.verify(stateModel).validate("47.11");
		Mockito.verify(stateModel).convert("47.11");
		Mockito.verify(notificationDialog, Mockito.times(0)).showError(Mockito.any());
		Mockito.verify(stateService).update(doubleState);
		assertFalse(grid.getSelectionModel().getFirstSelectedItem().isPresent());

	}

	@Test
	void saveButtonClickDoubleEmpty() {
		Mockito.doReturn(Locale.GERMAN).when(stateModel).locale();
		final String errorMessage = "Message NullValue";
		Mockito.doReturn(errorMessage).when(messageSource).getMessage(SystemVariablesView.I18N_VALUE_INVALID, null, "???", Locale.GERMAN);
		Mockito.doReturn(Optional.of(doubleState)).when(stateModel).selectedState();
		@SuppressWarnings("unchecked")
		final Grid<State<?>> grid = (Grid<State<?>>) fields.get("grid");
		grid.select(doubleState);

		observers.get(Events.AssignState).process();

		final TextField valueTextField = (TextField) fields.get("valueTextField");

		assertEquals("0.0", valueTextField.getValue());
		String value = " ";
		valueTextField.setValue(value);

		final Button saveButton = (Button) fields.get("saveButton");
		Mockito.doReturn(ValidationErrors.Invalid).when(stateModel).validate(null);
		Mockito.doReturn(doubleState).when(stateModel).convert(value);

		assertTrue(grid.getSelectionModel().getFirstSelectedItem().isPresent());
		listener(saveButton).onComponentEvent(null);

		Mockito.verify(stateModel).validate(null);

		Mockito.verify(notificationDialog).showError(errorMessage);
		Mockito.verify(stateService, Mockito.never()).update(doubleState);

	}

	@Test
	void saveButtonServiceError() {
		final String exceptionMessage = "ErrorMessage";
		Mockito.doReturn(Locale.GERMAN).when(stateModel).locale();
		final String errorMessage = "Message NullValue";

		Mockito.doReturn(errorMessage).when(messageSource).getMessage(SystemVariablesView.I18N_ERROR, new String[] { exceptionMessage }, "???", Locale.GERMAN);
		// Mockito.doReturn(errorMessage).when(messageSource).getMessage(SystemVariablesView.I18N_ERROR,
		// null,"???", Locale.GERMAN);

		@SuppressWarnings("unchecked")
		final Grid<State<?>> grid = (Grid<State<?>>) fields.get("grid");
		grid.select(workingDayState);

		observers.get(Events.AssignState).process();

		@SuppressWarnings("unchecked")
		final ComboBox<Boolean> valueComboBox = (ComboBox<Boolean>) fields.get("valueComboBox");

		assertEquals(Boolean.FALSE, valueComboBox.getValue());

		valueComboBox.setValue(Boolean.TRUE);

		final Button saveButton = (Button) fields.get("saveButton");
		Mockito.doReturn(ValidationErrors.Ok).when(stateModel).validate(Boolean.TRUE);
		Mockito.doReturn(workingDayState).when(stateModel).convert(Boolean.TRUE);

		Mockito.doThrow(new IllegalStateException(exceptionMessage)).when(stateService).update(workingDayState);

		assertTrue(grid.getSelectionModel().getFirstSelectedItem().isPresent());
		listener(saveButton).onComponentEvent(null);

		Mockito.verify(stateModel).validate(Boolean.TRUE);
		Mockito.verify(stateModel).convert(Boolean.TRUE);
		Mockito.verify(notificationDialog, Mockito.times(1)).showError(errorMessage);
		Mockito.verify(stateService).update(workingDayState);

	}

	@Test
	void unselect() {

		Mockito.doReturn(Optional.empty()).when(stateModel).selectedState();

		observers.get(Events.AssignState).process();

		final Button saveButton = (Button) fields.get("saveButton");
		assertFalse(saveButton.isEnabled());

		final Button resetButton = (Button) fields.get("resetButton");
		assertFalse(resetButton.isEnabled());

		final TextField lastUpdateTextField = (TextField) fields.get("lastUpdateTextField");
		assertTrue(lastUpdateTextField.isReadOnly());
		assertTrue(lastUpdateTextField.isEmpty());

		final TextField nameTextField = (TextField) fields.get("nameTextField");
		assertTrue(nameTextField.isReadOnly());
		assertTrue(nameTextField.getValue().isEmpty());

		final FormItem itemTextField = (FormItem) fields.get("textFieldFormItem");
		assertTrue(itemTextField.isVisible());

		final TextField valueTextField = (TextField) fields.get("valueTextField");
		assertTrue(valueTextField.isReadOnly());
		assertTrue(valueTextField.isVisible());
		assertTrue(valueTextField.getValue().isEmpty());

		final FormItem itemCombobox = (FormItem) fields.get("comboBoxFormItem");
		assertFalse(itemCombobox.isVisible());

	}

	@Test
	void loacalizeStateInfoLabelNothingSelected() {
		Mockito.doReturn(Optional.empty()).when(stateModel).selectedState();

		observers.get(Events.ChangeLocale).process();

		final Label stateInfoLabel = (Label) fields.get("stateInfoLabel");
		assertTrue(stateInfoLabel.getText().isEmpty());

	}

	@Test
	void resetButtonClick() {
		final Button resetButton = (Button) fields.get("resetButton");
		listener(resetButton).onComponentEvent(null);

		Mockito.verify(stateModel).reset();
	}
	
	@Test
	void stateNameValueProvider() {
		final State<?> state = Mockito.mock(State.class);
		Mockito.doReturn(STATE_NAME).when(state).name();
		
		assertEquals(STATE_NAME, systemVariablesView.stateNameValueProvider().apply(state));
	}
	
	@Test
	void statealueProvider() {
		final State<?> state = Mockito.mock(State.class);
		
		assertEquals(STATE_VALUE, systemVariablesView.stateValueProvider().apply(state));
	}

}
