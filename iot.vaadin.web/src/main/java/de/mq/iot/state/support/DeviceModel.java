package de.mq.iot.state.support;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.core.convert.converter.Converter;

import de.mq.iot.calendar.support.CalendarModel;
import de.mq.iot.model.LocaleAware;
import de.mq.iot.model.Subject;
import de.mq.iot.state.Room;
import de.mq.iot.state.State;
import de.mq.iot.state.StateService.DeviceType;

public interface DeviceModel  extends Subject<DeviceModel.Events, CalendarModel> , LocaleAware  , Converter<State<?>,String> {

	public enum Events {

		ChangeLocale,
		SeclectionChanged,
		ValueChanged,
		TypeChanged

	}

	/**
	 * Add all selected devices
	 * @param room the room where the devices are placed 
	 * @param selectedDevices selected devices
	 */
	void assign(final Room room, final Collection<State<Object>> selectedDevices);
	
	

	/**
	 * Collection of all devices in all rooms
	 * @return all devices
	 */
	 Collection<State<Object>>  selectedDevices();



	 /**
	  * if all selected devices have the same value, that value will be converted to view representation andreturned. otherwise Optional.empty() is returned
	  * @return the value of all devicesStates, if all values are equals. Otherwise return Optional.empty
	  */
	Optional<Object> selectedDistinctSingleViewValue();


	/**
	 * Return true if at least one value is selected, otherwise false
	 * @return true if values are selected, otherwise false
	 */
	boolean isSelected();


	/**
	 * The value to that the devices values should be set
	 * @return devives new value
	 */
	Optional<Object> value();



	/**
	 * Assign value to that the selected Devices should be set
	 * @param value new value
	 */
	void assign(Object value);



	/**
	 * Change device type
	 * @param type the type of the devives
	 */
	void assignType(DeviceType type);


	/**
	 * Device type
	 * @return devives type
	 */
	Optional<DeviceType> type();



	/**
	 * Reset all selected values
	 */
	void clearSelection();


	/**
	 * Selected states as Map with new Value from model
	 * @return changed states;
	 */
	Collection<State<Object>> changedValues();


	/**
	 * Device Name or Translation
	 * @param state the device
	 * @return the name / description in the ui
	 */
	 String synonym(final State<?> state);

	 
	 /**
	  * Add Synonyms for device names
	  * Assign Synonyms for device names to be independent  from technical device names
	  * @param deviceSynonyms Entry: key deviceName , value deviceSynonym
	  */
	 void assign(Collection<Entry<String,String>> deviceSynonyms);

	
}
