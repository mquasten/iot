package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;

import de.mq.iot.model.Observer;
import de.mq.iot.state.StateService;
import de.mq.iot.state.support.StateModel.Events;

class SystemVariablesViewTest {

	private static final String I18N_VALUE_COLUMN = "systemvariables_value_column";
	private static final String I18N_NAME_COLUMN = "systemvariables_name_column";
	private static final String I18N_NAME = "systemvariables_name";
	private static final String I18N_LASTUPDATE = "systemvariables_date";
	private static final String I18N_VALUE_LABEL = "systemvariables_value";
	private static final String I18N_RESET_BUTTON = "systemvariables_reset";
	private static final String I18N_SAVE_BUTTON = "systemvariables_save";
	private final StateService stateService = Mockito.mock(StateService.class);
	private final StateModel stateModel = Mockito.mock(StateModel.class);
	private final Converter<State<?>, String> converter = new StateValueConverterImpl(new DefaultConversionService());
	private final MessageSource messageSource = Mockito.mock(MessageSource.class);

	private SystemVariablesView systemVariablesView;

	private State<Boolean> workingDayState = new BooleanStateImpl(4711, "WorkingDay", LocalDateTime.now());

	private final Map<String, Object> fields = new HashMap<>();

	private final Map<StateModel.Events, Observer> observers = new HashMap<>();

	@BeforeEach
	void setup() {

		Mockito.doReturn(Locale.GERMAN).when(stateModel).locale();
		Arrays.asList(I18N_SAVE_BUTTON, I18N_RESET_BUTTON, I18N_NAME, I18N_LASTUPDATE, I18N_VALUE_LABEL, I18N_NAME_COLUMN, I18N_VALUE_COLUMN).forEach(key -> {
			Mockito.doReturn(key).when(messageSource).getMessage(key, null, "???", Locale.GERMAN);
		});

		Mockito.doReturn(Arrays.asList(workingDayState)).when(stateService).states();
		Mockito.doAnswer(answer -> {

			final StateModel.Events event = (Events) answer.getArguments()[0];
			final Observer observer = (Observer) answer.getArguments()[1];
			observers.put(event, observer);
			return null;

		}).when(stateModel).register(Mockito.any(), Mockito.any());

		systemVariablesView = new SystemVariablesView(stateService, stateModel, converter, messageSource);

		Arrays.asList(SystemVariablesView.class.getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).forEach(field -> fields.put(field.getName(), ReflectionTestUtils.getField(systemVariablesView, field.getName())));

		observers.get(Events.ChangeLocale).process();
	}

	@Test
	void init() {
		assertEquals(23, fields.size());
		assertEquals(2, observers.size());

		
		Mockito.verify(stateModel).notifyObservers(Events.ChangeLocale);

		final Button saveButton = (Button) fields.get("saveButton");
		assertEquals(I18N_SAVE_BUTTON, saveButton.getText());
		assertFalse(saveButton.isEnabled());

		final Button resetButton = (Button) fields.get("resetButton");
		assertEquals(I18N_RESET_BUTTON, resetButton.getText());
		assertFalse(resetButton.isEnabled());

		final FormItem itemTextField = (FormItem) fields.get("textFieldFormItem");

		assertTrue(itemTextField.isVisible());
		final TextField valueTextField = (TextField) fields.get("valueTextField");
		assertTrue(valueTextField.isReadOnly());
		assertEquals(I18N_VALUE_LABEL, itemTextField.getElement().getChild(1).getText());

		final TextField lastUpdateTextField = (TextField) fields.get("lastUpdateTextField");
		assertTrue(lastUpdateTextField.isReadOnly());
		final FormLayout formLayout = (FormLayout) fields.get("formLayout");
		assertEquals(I18N_LASTUPDATE, formLayout.getElement().getChild(1).getChild(1).getText());

		final TextField nameTextField = (TextField) fields.get("nameTextField");
		assertTrue(nameTextField.isReadOnly());
		assertEquals(I18N_NAME, formLayout.getElement().getChild(0).getChild(1).getText());

		@SuppressWarnings("unchecked")
		final Grid<State<Boolean>> grid = (Grid<State<Boolean>>) fields.get("grid");

		assertEquals(2, grid.getColumns().size());

		final ListDataProvider<?> data = (ListDataProvider<?>) grid.getDataProvider();
		assertEquals(1, data.getItems().size());
		assertEquals(workingDayState, data.getItems().iterator().next());

		final Label nameColumn = (Label) fields.get("nameColumnLabel");
		assertEquals(I18N_NAME_COLUMN, nameColumn.getText());

		final Label valueColumn = (Label) fields.get("valueColumnLabel");
		assertEquals(I18N_VALUE_COLUMN, valueColumn.getText());
		
		

	}

}
