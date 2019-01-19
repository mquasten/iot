package de.mq.iot.state.support;

import java.util.Collection;

import org.springframework.util.CollectionUtils;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.ValueProvider;

import de.mq.iot.state.Room;
import de.mq.iot.state.State;

class DeviceStateComponentRendererFunction  implements SerializableFunction<Room,Grid<State<Object>>> {

	private static final long serialVersionUID = 1L;
	private final DeviceModel deviceModel;
	
	DeviceStateComponentRendererFunction(DeviceModel deviceModel, Collection<Column<?>> devicesValueColumn) {
		this.deviceModel = deviceModel;
		this.devicesValueColumn = devicesValueColumn;
	}

	private final  Collection<Column<?>> devicesValueColumn;

	@Override
	public Grid<State<Object>> apply(Room room) {
		final Grid<State<Object>> devices = new Grid<State<Object>>();

		devices.setSelectionMode(SelectionMode.MULTI);
		devices.addColumn((ValueProvider<State<Object>, String>) state -> state.name()).setFlexGrow(80).setResizable(true).setHeader(room.name());

		final Column<State<Object>> column = devices.addColumn((ValueProvider<State<Object>, String>) state -> deviceModel.convert(state));
		devicesValueColumn.add(column);
		column.addAttachListener(event -> deviceModel.notifyObservers(DeviceModel.Events.ChangeLocale));

		devices.setItems((Collection<State<Object>>) room.states());

		devices.setHeightByRows(true);

		devices.addSelectionListener(event -> {

			deviceModel.assign(room, event.getAllSelectedItems());

			if (CollectionUtils.isEmpty(event.getAllSelectedItems())) devices.setItems(room.states());

		});

		return devices;
	}

	

	
	

}
