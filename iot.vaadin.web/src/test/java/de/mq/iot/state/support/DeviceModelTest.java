package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;
import de.mq.iot.state.Room;
import de.mq.iot.state.State;
import de.mq.iot.state.StateService.DeviceType;
import de.mq.iot.state.support.DeviceModel.Events;

class DeviceModelTest {

	

	private static final double STATE_VALUE = 0.5d;

	@SuppressWarnings("unchecked")
	private Subject<Events, DeviceModel> subject = Mockito.mock(Subject.class);

	private final DeviceModel deviceModel = new DeviceModelImpl(subject);

	private final Observer observer = Mockito.mock(Observer.class);

	final Room firstRoom = Mockito.mock(Room.class);
	final Room secondRoom = Mockito.mock(Room.class);
	@SuppressWarnings("unchecked")
	final State<Object> firstState = Mockito.mock(State.class);
	@SuppressWarnings("unchecked")
	final State<Object> secondState = Mockito.mock(State.class);

	@BeforeEach
	void setup() {
		Mockito.when(firstRoom.name()).thenReturn("first");

		Mockito.when(secondRoom.name()).thenReturn("second");

		Mockito.when(firstState.value()).thenReturn(STATE_VALUE);
		Mockito.when(secondState.value()).thenReturn(STATE_VALUE);

	}

	@Test
	void register() {
		deviceModel.register(DeviceModel.Events.ChangeLocale, observer);

		Mockito.verify(subject).register(DeviceModel.Events.ChangeLocale, observer);
	}

	@Test
	void notifyObservers() {
		deviceModel.notifyObservers(DeviceModel.Events.ChangeLocale);

		Mockito.verify(subject).notifyObservers(DeviceModel.Events.ChangeLocale);
	}

	@Test
	void locale() {
		assertEquals(Locale.GERMAN, deviceModel.locale());
	}

	@Test
	void selectedDevices() {
		assertTrue(deviceModel.selectedDevices().isEmpty());

		deviceModel.assign(firstRoom, Arrays.asList(firstState));
		deviceModel.assign(secondRoom, Arrays.asList(secondState));

		assertEquals(2, deviceModel.selectedDevices().size());

		assertTrue(deviceModel.selectedDevices().contains(firstState));
		assertTrue(deviceModel.selectedDevices().contains(secondState));

		deviceModel.assign(firstRoom, Collections.emptySet());
		deviceModel.assign(secondRoom, Collections.emptySet());

		assertEquals(0, deviceModel.selectedDevices().size());

		Mockito.verify(subject, Mockito.times(4)).notifyObservers(DeviceModel.Events.SeclectionChanged);
	}

	@Test
	void selectedDistinctSinglePercentValue() {
		assertFalse(deviceModel.selectedDistinctSinglePercentValue().isPresent());

		deviceModel.assign(firstRoom, Arrays.asList(firstState, secondState));
		deviceModel.assign(secondRoom, Arrays.asList(secondState));

		assertEquals(Optional.of(STATE_VALUE ), deviceModel.selectedDistinctSinglePercentValue());
	}

	@Test
	void selectedDistinctSinglePercentValueDifferent() {
		Mockito.when(firstState.value()).thenReturn(1d);

		deviceModel.assign(firstRoom, Arrays.asList(firstState, secondState));
		deviceModel.assign(secondRoom, Arrays.asList(secondState));

		assertEquals(Optional.empty(), deviceModel.selectedDistinctSinglePercentValue());
	}

	@Test
	void isSelected() {
		assertFalse(deviceModel.isSelected());

		deviceModel.assign(secondRoom, Arrays.asList(secondState));

		assertTrue(deviceModel.isSelected());
	}

	@Test
	void assign() {
		final int value = 50;
		deviceModel.assign("" +value);
		
		assertEquals(Optional.of(value/100d), deviceModel.value());
		Mockito.verify(subject).notifyObservers(DeviceModel.Events.ValueChanged);

	}
	
	@Test
	void assignEmpty() {
		deviceModel.assign("");
		
		assertEquals(Optional.empty(), deviceModel.value());
		Mockito.verify(subject).notifyObservers(DeviceModel.Events.ValueChanged);
	}
	
	@Test
	void assignInvalid() {
		deviceModel.assign("x");
		
		assertEquals(Optional.empty(), deviceModel.value());
		Mockito.verify(subject).notifyObservers(DeviceModel.Events.ValueChanged);
	}
	
	@Test
	void assignOutOfRange() {
		deviceModel.assign("101");
		
		assertEquals(Optional.empty(), deviceModel.value());
		Mockito.verify(subject).notifyObservers(DeviceModel.Events.ValueChanged);
	}
	
	
	@Test
	void assignType() {
		ReflectionTestUtils.setField(deviceModel, "value" , STATE_VALUE);
		final Map<String,Collection<State<?>>> selected = new HashMap<>();
		selected.put(firstRoom.name(), Arrays.asList(firstState));
		ReflectionTestUtils.setField(deviceModel, "selectedDevices" , selected);
		assertTrue(deviceModel.value().isPresent());
		assertTrue(deviceModel.isSelected());
		assertEquals(Optional.empty(), deviceModel.type());
		
		deviceModel.assignType(DeviceType.Level);
		
		assertEquals(Optional.of(DeviceType.Level), deviceModel.type());
		assertFalse(deviceModel.value().isPresent());
		assertFalse(deviceModel.isSelected());
		Mockito.verify(subject).notifyObservers(DeviceModel.Events.SeclectionChanged);
		Mockito.verify(subject).notifyObservers(DeviceModel.Events.TypeChanged);
		
		
	}
	
	@Test
	void  type() {
		assertFalse(deviceModel.isSelected());
		ReflectionTestUtils.setField(deviceModel, "type" , DeviceType.State);
		assertEquals(Optional.of(DeviceType.State), deviceModel.type());
		
	}
	
	@Test
	void clearSelection() {
		final Map<String,Collection<State<?>>> selected = new HashMap<>();
		selected.put(firstRoom.name(), Arrays.asList(firstState));
		ReflectionTestUtils.setField(deviceModel, "selectedDevices" , selected);
		assertTrue(deviceModel.isSelected());
		
		deviceModel.clearSelection();
		
		assertFalse(deviceModel.isSelected());
		Mockito.verify(subject).notifyObservers(DeviceModel.Events.SeclectionChanged);
	}
	

}
