package de.mq.iot.state;

import java.util.Collection;
/**
 * Service Statehandling
 * @author Admin
 *
 */
public interface StateService {
	
	/**
	 * Read all  state form CCU2
	 * @return Collection of State from CCU2
	 */
	Collection<State<?>> states();

	void update(State<?> state);

	/**
	 * List of devices states
	 * @param types list of types to query
	 * @return list of devices
	 */
	Collection<Room> deviceStates(final Collection<String> types);

	/**
	 * Update states
	 * Every state will be set to its value. 
	 * @param states list of states 
	 * @return list of Rooms, with changed State Values
	 */
	Collection<Room> update(Collection<State<Object>> states);

	/**
	 * Supported device types
	 * @return possible types of devices
	 */
	Collection<String> deviceTypes();

}