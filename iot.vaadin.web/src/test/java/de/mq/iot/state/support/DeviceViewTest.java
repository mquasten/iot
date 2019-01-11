package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.Query;

import de.mq.iot.model.Observer;
import de.mq.iot.state.Room;
import de.mq.iot.state.State;
import de.mq.iot.state.StateService;
import de.mq.iot.state.StateService.DeviceType;
import de.mq.iot.support.ButtonBox;

class DeviceViewTest {
	
	private final StateService stateService = Mockito.mock(StateService.class);
	
	private final DeviceModel deviceModel = Mockito.mock(DeviceModel.class);
	
	private final MessageSource messageSource = Mockito.mock(MessageSource.class);
	
	private DeviceView deviceView;
	
	private Map<String,Object> fields = new HashMap<>();
	
	private final State<Double> doubleState01 = new  DoubleStateImpl(4711L, "Rolladen Fenster rechts" , LocalDateTime.now());
	private final State<Double> doubleState02 = new  DoubleStateImpl(4712L, "Rolladen Fenster links" , LocalDateTime.now());
	private final State<Boolean> booleanState = new BooleanStateImpl(4713L, "Lampe", LocalDateTime.now());
	
	private final Room room = new RoomImpl("Schlafzimmer");
	
	private final Map<DeviceModel.Events, Observer> observers = new HashMap<>();
	
	@BeforeEach
	final void setup() {
		((RoomImpl)room).assign(doubleState01);
		((RoomImpl)room).assign(doubleState02);
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
		fields.putAll(Arrays.asList(deviceView.getClass().getDeclaredFields()).stream().filter(field -> ! Modifier.isStatic(field.getModifiers())).collect(Collectors.toMap(Field::getName, field -> ReflectionTestUtils.getField(deviceView, field.getName()))));
	
		
	
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

	private FormItem textItem(final DeviceStateValueField stateValueField) {
		return (FormItem) ReflectionTestUtils.getField(stateValueField, "textItem");
	}

	private DeviceStateValueField stateValueField() {
		return (DeviceStateValueField) fields.get("stateValueField");
	}

	private Grid<?> grid() {
		return (Grid<?>) fields.get("grid");
	}

}
