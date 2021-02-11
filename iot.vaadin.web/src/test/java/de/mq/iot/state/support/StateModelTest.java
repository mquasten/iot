package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.Authority;
import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;
import de.mq.iot.state.State;
import de.mq.iot.state.support.StateModel.Events;

class StateModelTest {

	private static final long ID = 4711L;
	private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
	private static final String VARABLE_NAME = "varable";
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
		
		Mockito.doReturn(Locale.CHINESE).when(subject).locale();
		assertEquals(Locale.CHINESE, stateModel.locale());
	}

	@Test
	void validateBooleanState() {
		final State<Boolean> state = new BooleanStateImpl(ID, VARABLE_NAME, LAST_UPDATE);
		stateModel.assign(state);
		assertEquals(StateModel.ValidationErrors.Ok, stateModel.validate(Boolean.TRUE));
		assertEquals(StateModel.ValidationErrors.Mandatory, stateModel.validate(null));
		assertEquals(StateModel.ValidationErrors.Invalid, stateModel.validate("x"));

		state.assign(true);
		assertEquals(StateModel.ValidationErrors.NotChanged, stateModel.validate("" + Boolean.TRUE));
	}

	@Test
	void validateDoubleState() {
		final double value = 47.11;
		final State<Double> state = new DoubleStateImpl(4711L, VARABLE_NAME, LAST_UPDATE);
		stateModel.assign(state);
		assertEquals(StateModel.ValidationErrors.Ok, stateModel.validate("" + value));
		assertEquals(StateModel.ValidationErrors.Mandatory, stateModel.validate(null));
		assertEquals(StateModel.ValidationErrors.Invalid, stateModel.validate("x"));

		state.assign(value);
		assertEquals(StateModel.ValidationErrors.NotChanged, stateModel.validate("" + value));
	}

	@Test
	void validateStringState() {
		final String value = "value";
		final State<String> state = new StringStateImpl(4711L, VARABLE_NAME, LAST_UPDATE);
		stateModel.assign(state);
		assertEquals(StateModel.ValidationErrors.Ok, stateModel.validate(value));
		assertEquals(StateModel.ValidationErrors.Mandatory, stateModel.validate(null));
		assertEquals(StateModel.ValidationErrors.Invalid, stateModel.validate("x y"));

		state.assign(value);
		assertEquals(StateModel.ValidationErrors.NotChanged, stateModel.validate(value));

	}

	@Test
	void validateItemState() {
		final int value = 1;
		final State<Integer> state = new ItemsStateImpl(4711L, VARABLE_NAME, LAST_UPDATE);
		assignItems(state);

		stateModel.assign(state);
		assertEquals(StateModel.ValidationErrors.Ok, stateModel.validate(value));
		assertEquals(StateModel.ValidationErrors.Mandatory, stateModel.validate(null));
		assertEquals(StateModel.ValidationErrors.Invalid, stateModel.validate("x"));

		state.assign(value);
		assertEquals(StateModel.ValidationErrors.NotChanged, stateModel.validate(value));

	}

	private void assignItems(final State<Integer> state) {
		final Map<Integer, String> items = new HashMap<>();
		items.put(0, "SummerTime");
		items.put(1, "WinterTime");
		Arrays.asList(state.getClass().getDeclaredFields()).stream().filter(field -> field.getType().equals(Map.class)).forEach(field -> ReflectionTestUtils.setField(state, field.getName(), items));
	}

	@Test
	void validateNotSelected() {
		assertThrows(IllegalStateException.class, () -> stateModel.validate("xy"));
	}

	@Test
	void convertDoubleState() {
		final Double value = 47.11;
		final State<Double> state = new DoubleStateImpl(ID, VARABLE_NAME, LAST_UPDATE);
		stateModel.assign(state);

		final State<Double> result = stateModel.convert("" + value);
		assertEquals(state.id(), result.id());
		assertEquals(state.name(), result.name());
		assertEquals(state.lastupdate(), result.lastupdate());
		assertEquals(value, result.value());

	}

	@Test
	void convertBooleanState() {

		final State<Boolean> state = new BooleanStateImpl(ID, VARABLE_NAME, LAST_UPDATE);
		stateModel.assign(state);

		final State<Double> result = stateModel.convert("" + true);
		assertEquals(state.id(), result.id());
		assertEquals(state.name(), result.name());
		assertEquals(state.lastupdate(), result.lastupdate());
		assertEquals(Boolean.TRUE, result.value());

	}

	@Test
	void convertStringState() {

		final State<String> state = new StringStateImpl(ID, VARABLE_NAME, LAST_UPDATE);
		stateModel.assign(state);

		String value = "value";
		final State<Double> result = stateModel.convert(value);
		assertEquals(state.id(), result.id());
		assertEquals(state.name(), result.name());
		assertEquals(state.lastupdate(), result.lastupdate());
		assertEquals(value, result.value());

	}

	@Test
	void convertItemsState() {
		final Integer value = 1;
		final State<Integer> state = new ItemsStateImpl(ID, VARABLE_NAME, LAST_UPDATE);
		assignItems(state);
		stateModel.assign(state);

		final State<Double> result = stateModel.convert(value);
		assertEquals(state.id(), result.id());
		assertEquals(state.name(), result.name());
		assertEquals(state.lastupdate(), result.lastupdate());
		assertEquals(value, result.value());

	}

	@Test
	void convertStateConstructorMissing() {
		stateModel.assign(state);
		Mockito.doReturn(1d).when(state).value();
		assertThrows(IllegalStateException.class, () -> stateModel.convert(47.11));
	}

	@Test
	void stateInfoParametersState() {
		final State<Boolean> state = new BooleanStateImpl(4711L, VARABLE_NAME, LAST_UPDATE);
		stateModel.assign(state);

		final String[] parameters = stateModel.stateInfoParameters();

		assertEquals(3, parameters.length);

		assertEquals(Boolean.class.getSimpleName(), parameters[0]);
		assertEquals("" + ID, parameters[1]);
		assertTrue(parameters[2].isEmpty());
	}

	@Test
	void stateInfoParametersNothingSelected() {
		assertThrows(IllegalStateException.class, () -> stateModel.stateInfoParameters());
	}

	@Test
	void stateInfoParametersMinMaxRangeInfinity() {
		final State<Double> state = new DoubleStateImpl(4711L, VARABLE_NAME, LAST_UPDATE);
		stateModel.assign(state);

		final String[] parameters = stateModel.stateInfoParameters();

		assertEquals(3, parameters.length);

		assertEquals(Double.class.getSimpleName(), parameters[0]);
		assertEquals("" + ID, parameters[1]);
		assertEquals("[-∞,+∞]", parameters[2]);

	}

	@Test
	void stateInfoParametersMinMaxRange() {
		final State<Double> state = new DoubleStateImpl(4711L, VARABLE_NAME, LAST_UPDATE);
		ReflectionTestUtils.setField(state, "min", -1d);
		ReflectionTestUtils.setField(state, "max", 1d);
		stateModel.assign(state);

		final String[] parameters = stateModel.stateInfoParameters();
		assertEquals(Double.class.getSimpleName(), parameters[0]);
		assertEquals("" + ID, parameters[1]);
		assertEquals("[-1.0,1.0]", parameters[2]);
	}
	@Test
	void isChangeVariableAllowed() {
		
		assertFalse(stateModel.isChangeVariableAllowed());
		
		final Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(authentication.hasRole(Authority.Systemvariables)).thenReturn(true);
		Mockito.when(subject.currentUser()).thenReturn(Optional.of(authentication));
		
		assertTrue(stateModel.isChangeVariableAllowed());
		
		Mockito.when(authentication.hasRole(Authority.Systemvariables)).thenReturn(false);
		
		assertFalse(stateModel.isChangeVariableAllowed());
		
	}
	@Test
	void assignLocale() {
		stateModel.assign(Locale.GERMAN);
		Mockito.verify(subject).assign(Locale.GERMAN);
	}

}
