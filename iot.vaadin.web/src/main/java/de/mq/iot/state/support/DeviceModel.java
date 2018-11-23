package de.mq.iot.state.support;

import java.util.Collection;

import de.mq.iot.calendar.support.CalendarModel;
import de.mq.iot.model.LocaleAware;
import de.mq.iot.model.Subject;
import de.mq.iot.state.Room;
import de.mq.iot.state.State;

public interface DeviceModel  extends Subject<DeviceModel.Events, CalendarModel> , LocaleAware  {

	public enum Events {

		ChangeLocale;

	}

	/**
	 * Add all selected devices
	 * @param room the room where the devices are placed 
	 * @param selectedDevices selected devices
	 */
	void assign(final Room room, final Collection<State<Double>> selectedDevices);
	
	

	/**
	 * Collection of all devices in all rooms
	 * @return all devices
	 */
	 Collection<State<Double>>  selectedDevices(); 
}
