package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;

import de.mq.iot.model.Observer;
import de.mq.iot.state.Room;
import de.mq.iot.state.State;
import de.mq.iot.state.StateService;
import de.mq.iot.state.StateService.DeviceType;
import de.mq.iot.support.ButtonBox;

class DeviceViewTest {

	private static final String I18N_DEVICES_INVALID_VALUE_LEVEL = "devices_invalid_value_level";

	private final StateService stateService = Mockito.mock(StateService.class);

	private final DeviceModel deviceModel = Mockito.mock(DeviceModel.class);

	private final MessageSource messageSource = Mockito.mock(MessageSource.class);

	private DeviceView deviceView;

	private Map<String, Object> fields = new HashMap<>();

	private final State<Double> doubleState01 = new DoubleStateImpl(4711L, "Rolladen Fenster rechts", LocalDateTime.now());
	private final State<Double> doubleState02 = new DoubleStateImpl(4712L, "Rolladen Fenster links", LocalDateTime.now());
	private final State<Boolean> booleanState = new BooleanStateImpl(4713L, "Lampe", LocalDateTime.now());

	private final Room room = new RoomImpl("Schlafzimmer");

	private final Map<DeviceModel.Events, Observer> observers = new HashMap<>();

	@BeforeEach
	final void setup() {
		((RoomImpl) room).assign(doubleState01);
		((RoomImpl) room).assign(doubleState02);
		doubleState01.assign(0.5d);
		doubleState02.assign(05.d);
		booleanState.assign(true);

		Mockito.doReturn(Arrays.asList(DeviceType.Level, DeviceType.State)).when(stateService).deviceTypes();

		Mockito.doReturn(Arrays.asList(room)).when(stateService).deviceStates(Arrays.asList(DeviceType.Level));

		Mockito.doAnswer(answer -> {

			final DeviceModel.Events event = (DeviceModel.Events) answer.getArguments()[0];
			final Observer observer = (Observer) answer.getArguments()[1];
			observers.put(event, observer);
			return null;

		}).when(deviceModel).register(Mockito.any(), Mockito.any());

		deviceView = new DeviceView(stateService, deviceModel, messageSource, new ButtonBox());
		fields.putAll(Arrays.asList(deviceView.getClass().getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).collect(Collectors.toMap(Field::getName, field -> ReflectionTestUtils.getField(deviceView, field.getName()))));

		Arrays.asList("devices_change", "devices_info", "devices_devices_value", "devices_devices", "devices_value", I18N_DEVICES_INVALID_VALUE_LEVEL, "devices_invalid_value_state", "devices_type_state", "devices_type_level").forEach(key -> {
			Mockito.doReturn(key).when(messageSource).getMessage(key, null, "???", Locale.GERMAN);
		});

	}

	@Test
	final void init() {
		assertEquals(16, fields.size());

		assertEquals(4, observers.size());

		Mockito.doReturn(Optional.of(DeviceType.Level)).when(deviceModel).type();
		observers.get(DeviceModel.Events.TypeChanged).process();

		final Grid<?> grid = grid();
		assertEquals(1, grid.getColumns().size());

		Collection<?> rooms = (Collection<?>) grid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList());

		assertEquals(1, rooms.size());
		assertEquals(room, rooms.stream().findFirst().get());

		final DeviceStateValueField stateValueField = stateValueField();
		final FormItem textItem = textItem(stateValueField);
		assertTrue(textItem.isVisible());

		final ComboBox<?> comboBox = comboBox();
		final Collection<?> items = comboBox.getDataProvider().fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(Arrays.asList(DeviceType.Level, DeviceType.State), items);

	}

	private ComboBox<?> comboBox() {
		return (ComboBox<?>) fields.get("comboBox");
	}

	private Button saveButton() {
		return (Button) fields.get("saveButton");
	}

	private FormItem textItem(final DeviceStateValueField stateValueField) {
		return (FormItem) ReflectionTestUtils.getField(stateValueField, "textItem");
	}

	private TextField textField(final DeviceStateValueField stateValueField) {
		return (TextField) ReflectionTestUtils.getField(stateValueField, "textField");
	}

	private DeviceStateValueField stateValueField() {
		return (DeviceStateValueField) fields.get("stateValueField");
	}

	private Label invalidLevelValueLabel() {
		return (Label) fields.get("invalidLevelValueLabel");
	}

	private Grid<?> grid() {
		return (Grid<?>) fields.get("grid");
	}

	@Test
	final void seclectionChanged() {
		final Observer observer = observers.get(DeviceModel.Events.SeclectionChanged);
		assertNotNull(observer);
		Mockito.doReturn(true).when(deviceModel).isSelected();
		Mockito.doReturn(Optional.of(50)).when(deviceModel).selectedDistinctSingleViewValue();
		final DeviceStateValueField stateValueField = stateValueField();
		stateValueField.setDeviceType(DeviceType.Level);
		final TextField textField = textField(stateValueField);
		assertFalse(textField.isEnabled());
		assertEquals(0, textField.getValue().length());

		observer.process();

		assertTrue(textField.isEnabled());
		assertEquals("50", textField.getValue());

		final Button saveButton = saveButton();

		saveButton.setEnabled(true);
		textField.setInvalid(true);

		Mockito.doReturn(false).when(deviceModel).isSelected();
		observer.process();

		assertFalse(saveButton.isEnabled());
		assertFalse(textField.isInvalid());
	}

	@Test
	final void valueChanged() {
		final Observer observer = observers.get(DeviceModel.Events.ValueChanged);
		assertNotNull(observer);

		final DeviceStateValueField stateValueField = stateValueField();
		stateValueField.setDeviceType(DeviceType.Level);

		final TextField textField = textField(stateValueField);
		final Button saveButton = saveButton();

		final Label message = invalidLevelValueLabel();
		message.setText("ErrorMessage");

		saveButton.setEnabled(true);

		Mockito.doReturn(Optional.of(DeviceType.Level)).when(deviceModel).type();
		Mockito.doReturn(true).when(deviceModel).isSelected();

		observer.process();

		assertFalse(saveButton.isEnabled());
		assertEquals(message.getText(), textField.getErrorMessage());
		assertTrue(textField.isInvalid());

		Mockito.doReturn(Optional.of(0.5d)).when(deviceModel).value();
		Mockito.doReturn(false).when(deviceModel).isSelected();
		observer.process();

		assertTrue(saveButton.isEnabled());
		assertFalse(textField.isInvalid());
	}

	@Test
	final void save() {
		final Button saveButton = saveButton();
		@SuppressWarnings("unchecked")
		final Collection<State<Object>> states = Arrays.asList(Mockito.mock(State.class));
		Mockito.doReturn(states).when(deviceModel).changedValues();

		Mockito.doReturn(Optional.of(0.5d)).when(deviceModel).value();
		listener(saveButton).onComponentEvent(null);

		Mockito.verify(deviceModel).clearSelection();

		Mockito.verify(stateService).update(states);
	}

	@SuppressWarnings("unchecked")
	private ComponentEventListener<?> listener(final Button saveButton) {
		final ComponentEventBus eventBus = (ComponentEventBus) ReflectionTestUtils.getField(saveButton, "eventBus");
		final Map<Class<?>, ?> map = (Map<Class<?>, ?>) ReflectionTestUtils.getField(eventBus, "componentEventData");
		return DataAccessUtils.requiredSingleResult((Collection<ComponentEventListener<?>>) ReflectionTestUtils.getField(map.values().iterator().next(), "listeners"));
	}

	@Test
	final void i18n() {
		final Observer observer = observers.get(DeviceModel.Events.ChangeLocale);

		assertNotNull(observer);

		Mockito.doReturn(Locale.GERMAN).when(deviceModel).locale();

		observer.process();

		assertEquals(I18N_DEVICES_INVALID_VALUE_LEVEL, invalidLevelValueLabel().getText());

	}

}
