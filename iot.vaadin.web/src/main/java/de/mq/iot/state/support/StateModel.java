package de.mq.iot.state.support;

import java.util.Optional;

import de.mq.iot.model.Subject;



interface StateModel extends Subject<StateModel.Events, StateModel> {
	
	enum Events {
		AssignState
	}
	
	void assign(final State<?> selectedState);

	Optional<State<?>> selectedState();
	

}
