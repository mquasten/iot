package de.mq.iot.state.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.convert.ConversionService;

import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;

class StateModelImpl implements StateModel {

	private final Subject<StateModel.Events, StateModel> subject;

	private Optional<State<?>> selectedState = Optional.empty();
	private final ConversionService conversionService;

	private Map<Class<? extends State<?>>, Class<?>> targetTypes = new HashMap<>();

	StateModelImpl(final Subject<Events, StateModel> subject, final ConversionService conversionService) {
		this.subject = subject;
		this.conversionService = conversionService;

		targetTypes.put(DoubleStateImpl.class, Double.class);
		targetTypes.put(StringStateImpl.class, String.class);
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
		this.selectedState = Optional.ofNullable(selectedState);
		notifyObservers(Events.AssignState);

	}

	@Override
	public Optional<State<?>> selectedState() {
		return selectedState;
	}

	@Override
	public ValidationErrors validate(final Object value) {

		if (value == null) {
			return ValidationErrors.Mandatory;
		}

		final State<Object> state = state();
		
		
		
		final Class<?> valueType = targetTypes.containsKey(state.getClass()) ? targetTypes.get(state.getClass()) : value.getClass();

		if (!canConvert(value, valueType)) {
			return ValidationErrors.Invalid;
		}
		
		
		if( state.value().equals(conversionService.convert(value, valueType))) {
			return ValidationErrors.NotChanged;
		}
		


		return state.validate(conversionService.convert(value, valueType)) ? ValidationErrors.Ok : ValidationErrors.Invalid;

	}

	@SuppressWarnings("unchecked")
	private State<Object> state() {
		return (State<Object>) selectedState.orElseThrow(() -> new IllegalStateException("State must be selected."));
	}

	@Override
	public final Object convert(final Object value) {
		final State<Object> state = state();

		final Class<?> valueType = targetTypes.containsKey(state.getClass()) ? targetTypes.get(state.getClass()) : value.getClass();
		return conversionService.convert(value, valueType);
	}

	private boolean canConvert(final Object value, final Class<?> valueType) {
		try {
			conversionService.convert(value, valueType);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
