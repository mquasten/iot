package de.mq.iot.state.support;

import java.util.Collection;
import java.util.Map;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.state.State;

public interface StateRepository {
	
	
	
	Collection<Map<String, String>> findStates(final ResourceIdentifier resourceIdentifier);


	void changeState(final ResourceIdentifier resourceIdentifier, State<?> state);


	double findVersion(ResourceIdentifier resourceIdentifier);


	/**
	 * List of channelIds for the given function
	 * @param resourceIdentifier the resourceIdentifier for the HomematicXmlApi
	 * @param function the device function 
	 * @return devices channelIds for the function 
	 */
	Collection<Long> findChannelIds(final ResourceIdentifier resourceIdentifier, final String function);


	/**
	 * Map of ChannelIds (key) and Rooms (Value)
	 * @param resourceIdentifier the resourceIdentifier for the HomematicXmlApi
	 * @return aMap of ChanelIds and roomNames
	 */
	Map<Long, String> findCannelsRooms(ResourceIdentifier resourceIdentifier);


	/**
	 * Get List of DeviceStates
	 * @param resourceIdentifier the resourceIdentifier for the HomematicXmlApi
	 * @return List of DeviceStates
	 */
	Collection<State<Double>> findDeviceStates(ResourceIdentifier resourceIdentifier);

}