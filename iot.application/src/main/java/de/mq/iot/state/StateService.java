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
	 * A list of rooms with  deviceStates
	 * @return rooms with deviceStates list
	 */
	Collection<Room> deviceStates();

	/**
	 * Update states
	 * Every state will be set to its value. 
	 * @param states list of states 
	 * @return list of Rooms, with changed State Values
	 */
	Collection<Room> update(Collection<State<?>> states);

}