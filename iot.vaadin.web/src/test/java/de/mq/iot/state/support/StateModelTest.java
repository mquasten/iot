package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;
import de.mq.iot.state.support.StateModel.Events;

class StateModelTest {

	@SuppressWarnings("unchecked")
	private final Subject<StateModel.Events, StateModel> subject = Mockito.mock(Subject.class);
	private final ConversionService conversionService = new DefaultConversionService();
	private final StateModel stateModel = new StateModelImpl(subject, conversionService);
	private final State<?> state = Mockito.mock(State.class);
	final Observer observer = Mockito.mock(Observer.class);

	@Test
	void register() {
		stateModel.register(Events.AssignState, observer);

		Mockito.verify(subject).register(Events.AssignState, observer);
	}

	@Test
	void notifyObservers() {
		stateModel.notifyObservers(Events.AssignState);
		Mockito.verify(subject).notifyObservers(Events.AssignState);
	}

	@Test
	void assign() {
		assertEquals(Optional.empty(), stateModel.selectedState());
		stateModel.assign(state);
		assertEquals(Optional.of(state), stateModel.selectedState());
		
		Mockito.verify(subject).notifyObservers(Events.AssignState);
	}
	
	@Test
	void reset() {
		stateModel.reset();
		
		Mockito.verify(subject).notifyObservers(Events.AssignState);
	}
	
	@Test
	void locale() {
		assertEquals(Locale.GERMAN, stateModel.locale());
	}

}
