package de.mq.iot.state;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

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
	 * @param functions list of functions to query
	 * @param types list of types to query
	 * @return list of devices
	 */
	Collection<Room> deviceStates(final Collection<String> functions, final Collection<String> types);

	/**
	 * Update states
	 * Every state will be set to its value. 
	 * @param states list of states 
	 * @return list of Rooms, with changed State Values
	 */
	Collection<Room> update(Collection<State<Object>> states);

	/**
	 * List of entries with functions and supported types
	 * @return list functions  and supported types
	 */
	Collection<Entry<String, Set<String>>> functions();

}