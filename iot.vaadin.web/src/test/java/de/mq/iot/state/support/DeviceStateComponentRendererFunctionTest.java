package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.function.SerializableFunction;

import de.mq.iot.state.Room;
import de.mq.iot.state.State;

public class DeviceStateComponentRendererFunctionTest {
	
	private final DeviceModel deviceModel = Mockito.mock(DeviceModel.class);
	
	
	
	private final Collection<Column<?>> columns = new ArrayList<>();
	
	private final SerializableFunction<Room,Grid<State<Object>>>  function = new DeviceStateComponentRendererFunction(deviceModel,columns); 
	
	@Test
	final void apply() {
		
		final Room room = Mockito.mock(Room.class);
		final State<?> firstState = Mockito.mock(State.class);
		final State<?> secondState = Mockito.mock(State.class);
		Mockito.doReturn(Arrays.asList(firstState,secondState)).when(room).states();
		
		Mockito.doReturn("Raum").when(room).name();
		final Grid<State<Object>> grid = function.apply(room);
		
		grid.getDataProvider().refreshAll();
		
		assertEquals(1, columns.size());
		
		
		final Collection<State<?>> data = grid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(2, data.size());
		assertTrue(data.containsAll(Arrays.asList(firstState, secondState)));
		
		listener(columns.iterator().next()).onComponentEvent(null);;
		
	}
	
	@SuppressWarnings("unchecked")
	private ComponentEventListener<?> listener(final Component saveButton) {
		final ComponentEventBus eventBus = (ComponentEventBus) ReflectionTestUtils.getField(saveButton, "eventBus");
		final Map<Class<?>, ?> map = (Map<Class<?>, ?>) ReflectionTestUtils.getField(eventBus, "componentEventData");
		return DataAccessUtils.requiredSingleResult((Collection<ComponentEventListener<?>>) ReflectionTestUtils.getField(map.values().iterator().next(), "listeners"));
	}

}
