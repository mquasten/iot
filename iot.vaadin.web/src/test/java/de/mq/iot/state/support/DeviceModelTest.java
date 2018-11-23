package de.mq.iot.state.support;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;
import de.mq.iot.state.Room;
import de.mq.iot.state.State;
import de.mq.iot.state.support.DeviceModel.Events;

class DeviceModelTest {
	
	@SuppressWarnings("unchecked")
	private Subject<Events, DeviceModel> subject = Mockito.mock( Subject.class);
	
	private final DeviceModel deviceModel = new DeviceModelImpl(subject);
	
	private final Observer  observer = Mockito.mock(Observer.class);
	
	final Room firstRoom = Mockito.mock(Room.class);
	final Room secondRoom = Mockito.mock(Room.class);
	@SuppressWarnings("unchecked")
	final State<Double> firstState = Mockito.mock(State.class);
	@SuppressWarnings("unchecked")
	final State<Double> secondState = Mockito.mock(State.class);
	
	@Test
	void register() {
		deviceModel.register(DeviceModel.Events.ChangeLocale, observer);
		
		Mockito.verify(subject).register(DeviceModel.Events.ChangeLocale, observer);
	}
	
	@Test
	void  notifyObservers() {
		deviceModel.notifyObservers(DeviceModel.Events.ChangeLocale);
		
		Mockito.verify(subject).notifyObservers(DeviceModel.Events.ChangeLocale);
	}
	
	@Test
	void  locale() {
		assertEquals(Locale.GERMAN, deviceModel.locale());
	}
	
	@Test
	void selectedDevices() {
		assertTrue(deviceModel.selectedDevices().isEmpty());
		
		
		Mockito.when(firstRoom.name()).thenReturn("first");
		
		Mockito.when(secondRoom.name()).thenReturn("second");
		
		
		
		deviceModel.assign(firstRoom, Arrays.asList(firstState));
		deviceModel.assign(secondRoom, Arrays.asList(secondState));
		
		assertEquals(2, deviceModel.selectedDevices().size());
		
		
		assertTrue(deviceModel.selectedDevices().contains(firstState));
		assertTrue(deviceModel.selectedDevices().contains(secondState));
		
		deviceModel.assign(firstRoom, Collections.emptySet());
		deviceModel.assign(secondRoom, Collections.emptySet());
		
		assertEquals(0, deviceModel.selectedDevices().size());
	}

}
