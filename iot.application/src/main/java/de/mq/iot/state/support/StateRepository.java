package de.mq.iot.state.support;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.state.State;

public interface StateRepository {

	Collection<Map<String, String>> findStates(final ResourceIdentifier resourceIdentifier);

	void changeState(final ResourceIdentifier resourceIdentifier, State<?> state);

	double findVersion(ResourceIdentifier resourceIdentifier);

	/**
	 * List of channelIds for the given functions
	 * 
	 * @param resourceIdentifier
	 *            the resourceIdentifier for the HomematicXmlApi
	 * @param functions
	 *            the devices functions
	 * @return devices channelIds and funtion 
	 */
	 Collection<Entry<Long,String>> findChannelIds(final ResourceIdentifier resourceIdentifier, final Collection<String> functions);

	/**
	 * Map of ChannelIds (key) and Rooms (Value)
	 * 
	 * @param resourceIdentifier
	 *            the resourceIdentifier for the HomematicXmlApi
	 * @return aMap of ChanelIds and roomNames
	 */
	Map<Long, String> findCannelsRooms(ResourceIdentifier resourceIdentifier);

	/**
	 * Get List of DeviceStates
	 * 
	 * @param resourceIdentifier
	 *            the resourceIdentifier for the HomematicXmlApi
	 * @param types
	 *            the types that will be selected , LEVEL or STATE
	 * @return List of DeviceStates as a map
	 */
	 Collection<Map<String,String>> findDeviceStates(ResourceIdentifier resourceIdentifier, Collection<String> types);

	/**
	 * Change values for the given devices
	 * 
	 * @param resourceIdentifier
	 *            the resourceIdentifier for the HomematicXmlApi
	 * @param states
	 *            Collection of Entries with device id and value to that the
	 *            devices should be set
	 */
	void changeState(final ResourceIdentifier resourceIdentifier, final Collection<Entry<Long, String>> states);

}