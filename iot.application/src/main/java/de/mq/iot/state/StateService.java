package de.mq.iot.state;

import java.util.Collection;

import de.mq.iot.state.support.State;

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

}