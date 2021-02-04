package de.mq.iot.state.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.ReflectionUtils;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.Authority;
import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;
import de.mq.iot.model.support.SubjectImpl;
import de.mq.iot.state.State;

class StateModelImpl implements StateModel {

	private final Subject<StateModel.Events, StateModel> subject;

	private Optional<State<?>> selectedState = Optional.empty();
	private final ConversionService conversionService;

	StateModelImpl(final Subject<Events, StateModel> subject, final ConversionService conversionService) {
		this.subject = subject;
		this.conversionService = conversionService;
		((SubjectImpl<?, ?>)subject).reset();
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

		final Class<?> valueType = state.value().getClass();

		if (!canConvert(value, valueType)) {
			return ValidationErrors.Invalid;
		}

		if (state.value().equals(conversionService.convert(value, valueType))) {
			return ValidationErrors.NotChanged;
		}

		return state.validate(conversionService.convert(value, valueType)) ? ValidationErrors.Ok : ValidationErrors.Invalid;

	}

	@SuppressWarnings("unchecked")
	private <T> State<T> state() {
		return (State<T>) selectedState.orElseThrow(() -> new IllegalStateException("State must be selected."));
	}

	@Override
	public final <T> State<T> convert(final Object value) {
		final State<T> state = state();

		@SuppressWarnings("unchecked")
		final Class<T> valueType = (Class<T>) state.value().getClass();

		final T newValue = (T) conversionService.convert(value, valueType);

		final Constructor<State<T>> constructor = constructor(state);
		final State<T> newState = BeanUtils.instantiateClass(constructor, state.id(), state.name(), state.lastupdate());

		if (newState instanceof ItemsStateImpl) {

			final Field field = ReflectionUtils.findField(ItemsStateImpl.class, "items");
			field.setAccessible(true);
			@SuppressWarnings("unchecked")
			final Map<Integer, String> items = (Map<Integer, String>) ReflectionUtils.getField(field, newState);
			items.putAll(((ItemsStateImpl) state).items().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue)));

		}

		newState.assign(newValue);

		return newState;
	}

	@SuppressWarnings("unchecked")
	private <T> Constructor<State<T>> constructor(final State<T> state) {
		try {
			return (Constructor<State<T>>) state.getClass().getDeclaredConstructor(long.class, String.class, LocalDateTime.class);
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

	@Override
	public String[] stateInfoParameters() {
		final State<?> state = selectedState.orElseThrow(() -> new IllegalStateException("State must be selected"));
		StringBuilder builder = new StringBuilder();
		if (state instanceof MinMaxRange) {

			builder.append("[");
			builder.append(((DoubleStateImpl) state).getMin().map(min -> "" + min).orElse("-" + "\u221E"));
			builder.append(",");
			builder.append(((DoubleStateImpl) state).getMax().map(max -> "" + max).orElse("+" + "\u221E"));
			builder.append("]");
		}
		return new String[] { state.value().getClass().getSimpleName(), "" + state.id(), builder.toString() };
	}

	
	@Override
	public boolean isChangeVariableAllowed() {
		
		final Optional<Authentication> authentication = subject.currentUser();
		if ( ! authentication.isPresent()) {
			return false;
		}
		
		return authentication.get().hasRole(Authority.Systemvariables);
	}

	@Override
	public void assign(final Locale locale) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Locale locale() {
		return Locale.GERMAN;
	}


}
