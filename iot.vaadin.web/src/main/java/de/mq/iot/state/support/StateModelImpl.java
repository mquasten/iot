package de.mq.iot.state.support;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
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
	public void reset() {
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
	private <T> State<T> state() {
		return (State<T>) selectedState.orElseThrow(() -> new IllegalStateException("State must be selected."));
	}

	@Override
	public final <T>  State<T> convert(final Object value) {
		final State<T> state = state();

		@SuppressWarnings("unchecked")
		final Class<T> valueType = targetTypes.containsKey(state.getClass()) ? (Class<T>) targetTypes.get(state.getClass()) : (Class<T>) value.getClass();
		
		final T newValue =  (T) conversionService.convert(value, valueType);
		
		
		final Constructor<State<T>> 	constructor =  constructor(state);
		final State<T> newState = BeanUtils.instantiateClass(constructor, state.id(), state.name(), state.lastupdate());
		newState.assign(newValue);
		
		return newState;
	}

	@SuppressWarnings("unchecked")
	private <T> Constructor<State<T>> constructor(final State<T> state)  {
		try {
			return  (Constructor<State<T>>) state.getClass().getDeclaredConstructor(long.class, String.class, LocalDateTime.class);
		} catch (final Exception ex) {
			throw new IllegalStateException(ex);
		} 
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
