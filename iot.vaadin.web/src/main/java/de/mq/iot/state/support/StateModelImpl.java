package de.mq.iot.state.support;

import java.util.Optional;

import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;


class StateModelImpl implements StateModel {

	private final Subject<StateModel.Events, StateModel> subject ; 
	
	private Optional<State<?>> selectedState  = Optional.empty();
	
	
	StateModelImpl(final Subject<Events, StateModel> subject) {
		this.subject = subject;
	}

	@Override
	public final Observer register(final Events key, final Observer observer) {
		return subject.register(key, observer);
	}

	@Override
	public final void notifyObservers(final Events key) {
		subject.notifyObservers(key);
		
	}

	@Override
	public void assign(final State<?> selectedState) {
		this.selectedState=Optional.ofNullable(selectedState);
		notifyObservers(Events.AssignState);
		
	}
	
	@Override
	public Optional<State<?>> selectedState() {
		return selectedState;
	}
	
	

}
