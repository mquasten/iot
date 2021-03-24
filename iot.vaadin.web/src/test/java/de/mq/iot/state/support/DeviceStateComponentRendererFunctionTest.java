package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.ValueProvider;

import de.mq.iot.state.Room;
import de.mq.iot.state.State;

public class DeviceStateComponentRendererFunctionTest {
	
	private static final String DEVICE_VALUE = "50";



	private static final String DEVICE_NAME = "devicename";



	private final DeviceModel deviceModel = Mockito.mock(DeviceModel.class);
	
	private final Label label =  Mockito.mock(Label.class);
	
	private final Collection<Label> columns = new ArrayList<>();
	
	private final SerializableFunction<Room,Grid<State<Object>>>  function = new DeviceStateComponentRendererFunction(deviceModel,columns, label); 
	
	private final Room room = Mockito.mock(Room.class);
	private final State<?> firstState = Mockito.mock(State.class);
	private final State<?> secondState = Mockito.mock(State.class);
	
	
	@BeforeEach
	final void setup() {
		Mockito.doReturn(Arrays.asList(firstState,secondState)).when(room).states();
		
		Mockito.doReturn("Raum").when(room).name();
	}
	
	
	@Test
	final void apply() {
		
		
		
		final Grid<State<Object>> grid = function.apply(room);
		
		
		assertEquals(1, columns.size());
		
		
		final Collection<State<Object>> data = grid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(2, data.size());
		assertTrue(data.containsAll(Arrays.asList(firstState, secondState)));
		
		
		
	}

	@Test
	final void columnAttacheListener() {
		Mockito.when(label.getText()).thenReturn("Wert");

		final Grid<State<Object>> devices = function.apply(room);
		devices.getColumns();
		assertEquals(2, devices.getColumns().size());

		listener(devices.getColumns().get(1)).onComponentEvent(null);

		assertEquals(1, columns.size());
		assertEquals(label.getText(), columns.stream().findAny().get().getText());

	}
	
	@SuppressWarnings("unchecked")
	private ComponentEventListener<?> listener(final Component saveButton) {
		final ComponentEventBus eventBus = (ComponentEventBus) ReflectionTestUtils.getField(saveButton, "eventBus");
		final Map<Class<?>, ?> map = (Map<Class<?>, ?>) ReflectionTestUtils.getField(eventBus, "componentEventData");
		return DataAccessUtils.requiredSingleResult((Collection<ComponentEventListener<?>>) ReflectionTestUtils.getField(map.values().iterator().next(), "listeners"));
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void selectionListener() {
		final Component grid = function.apply(room);
	
		MultiSelectionEvent event =  Mockito.mock(MultiSelectionEvent.class);
	
		Mockito.doReturn(Sets.newSet(firstState)).when(event).getAllSelectedItems();
		
		final ComponentEventListener listener =  listener(grid);
		listener.onComponentEvent(event);
		
		
		Mockito.verify(deviceModel).assign(room,(Collection) Sets.newSet(firstState));
		
		Mockito.doReturn(Sets.newSet()).when(event).getAllSelectedItems();
		listener.onComponentEvent(event);
		
		Mockito.verify(deviceModel).assign(room,(Collection) Sets.newSet());
		
	}
	
	@Test
	void stateNameProvider() {
		 @SuppressWarnings("unchecked")
		 final ValueProvider<State<?>, String> stateNameProvider = (ValueProvider<State<?>, String>) ReflectionTestUtils.getField(function, "stateNameProvider");
	
		 Mockito.doReturn(DEVICE_NAME).when(deviceModel).synonym(firstState);
		 
		assertEquals(DEVICE_NAME, stateNameProvider.apply( firstState));
	}
	
	@Test
	void stateValueProvider() {
		 @SuppressWarnings("unchecked")
		 final ValueProvider<State<?>, String> stateValueProvider = (ValueProvider<State<?>, String>) ReflectionTestUtils.getField(function, "stateValueProvider");
	
		 Mockito.doReturn(DEVICE_VALUE).when(deviceModel).convert(firstState);
		 
		 
		 assertEquals(DEVICE_VALUE, stateValueProvider.apply(firstState));
		 Mockito.verify(deviceModel).convert(firstState);
	}

}
