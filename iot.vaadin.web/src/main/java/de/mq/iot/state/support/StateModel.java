package de.mq.iot.state.support;

import java.util.Optional;

import de.mq.iot.model.LocaleAware;
import de.mq.iot.model.Subject;



interface StateModel extends Subject<StateModel.Events, StateModel> , LocaleAware {
	
	enum Events {
		AssignState,
		ChangeLocale;
	}
	enum ValidationErrors {
		Ok,
		Mandatory,
		Invalid,
		NotChanged;
	}
	
	void assign(final State<?> selectedState);
	void reset();

	Optional<State<? extends Object>> selectedState();

	ValidationErrors validate(Object value);
	
	<T>  State<T> convert(Object value);
	
	String[] stateInfoParameters(); 
}
