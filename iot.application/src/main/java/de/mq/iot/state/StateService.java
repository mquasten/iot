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
	 * A list of deviceStates
	 * @return deviceStates list
	 */
	Collection<State<Double>> deviceStates();

}