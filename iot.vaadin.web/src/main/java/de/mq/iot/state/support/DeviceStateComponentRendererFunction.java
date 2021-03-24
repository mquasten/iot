package de.mq.iot.state.support;

import java.util.Collection;

import org.springframework.util.CollectionUtils;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.ValueProvider;

import de.mq.iot.state.Room;
import de.mq.iot.state.State;

class DeviceStateComponentRendererFunction  implements SerializableFunction<Room,Grid<State<Object>>> {

	private static final long serialVersionUID = 1L;
	private final DeviceModel deviceModel;
	private final  Collection<Label> devicesValueColumns; 
	
	private final ValueProvider<State<Object>, String>stateNameProvider;
	private final ValueProvider<State<Object>, String> stateValueProvider;
	private final Label label;
	
	
	
	DeviceStateComponentRendererFunction(final DeviceModel deviceModel,  final Collection<Label> devicesValueColumns, final Label label ) {
		this.deviceModel = deviceModel;
		this.label=label;
		this.devicesValueColumns=devicesValueColumns;
		stateValueProvider = (ValueProvider<State<Object>, String>) state -> deviceModel.convert(state);
		stateNameProvider = (ValueProvider<State<Object>, String>) state -> deviceModel.synonym(state);
	}

	

	
	
	
	@Override
	public Grid<State<Object>> apply(final Room room) {
		final Grid<State<Object>> devices = new Grid<State<Object>>();

		devices.setSelectionMode(SelectionMode.MULTI);
		
		
		devices.addColumn(stateNameProvider).setFlexGrow(80).setResizable(true).setHeader(room.name());

		
		final Column<State<Object>> column = devices.addColumn(stateValueProvider);
		final Label columnLabel = new Label();
		column.setHeader(columnLabel);
		
		devicesValueColumns.add(columnLabel);
	
		
		column.addAttachListener(event -> columnLabel.setText(label.getText()));

		devices.setItems((Collection<State<Object>>) room.states());

		devices.setHeightByRows(true);

		devices.addSelectionListener(event -> {

			deviceModel.assign(room, event.getAllSelectedItems());

			if (CollectionUtils.isEmpty(event.getAllSelectedItems())) devices.setItems(room.states());

		});

		return devices;
	}

	

	
	

	
	

}
