package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.Authority;
import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;
import de.mq.iot.state.Room;
import de.mq.iot.state.State;
import de.mq.iot.state.StateService.DeviceType;
import de.mq.iot.state.support.DeviceModel.Events;
import de.mq.iot.synonym.Synonym;

class DeviceModelTest {

	

	private static final String FUNCTION = "Licht";

	private static final String SYNONYM = "Stehlampe";

	private static final String DEVIVE_NAME = "HM-LC-Sw1-Pl-DN-R1 PEQ0088080:1";

	private static final double STATE_VALUE = 0.5d;

	@SuppressWarnings("unchecked")
	private Subject<Events, DeviceModel> subject = Mockito.mock(Subject.class);

	private final DeviceModel deviceModel = new DeviceModelImpl(subject, new DefaultConversionService());

	private final Observer observer = Mockito.mock(Observer.class);

	private final Room firstRoom = Mockito.mock(Room.class);
	private final Room secondRoom = Mockito.mock(Room.class);
	@SuppressWarnings("unchecked")
	private final State<Object> firstState = Mockito.mock(State.class);
	@SuppressWarnings("unchecked")
	private final State<Object> secondState = Mockito.mock(State.class);
	
	private final Authentication authentication = Mockito.mock(Authentication.class);

	@BeforeEach
	void setup() {
		Mockito.when(firstRoom.name()).thenReturn("first");

		Mockito.when(secondRoom.name()).thenReturn("second");

		Mockito.when(firstState.value()).thenReturn(STATE_VALUE);
		Mockito.when(secondState.value()).thenReturn(STATE_VALUE);
		
		Mockito.when(subject.currentUser()).thenReturn(Optional.of(authentication));

	}

	@Test
	void register() {
		deviceModel.register(DeviceModel.Events.ChangeLocale, observer);

		Mockito.verify(subject).register(DeviceModel.Events.ChangeLocale, observer);
	}

	@Test
	void notifyObservers() {
		deviceModel.notifyObservers(DeviceModel.Events.ChangeLocale);

		Mockito.verify(subject).notifyObservers(DeviceModel.Events.ChangeLocale);
	}

	@Test
	void locale() {
		Mockito.when(deviceModel.locale()).thenReturn(Locale.GERMAN);
		assertEquals(Locale.GERMAN, deviceModel.locale());
	}

	@Test
	void selectedDevices() {
		assertTrue(deviceModel.selectedDevices().isEmpty());

		deviceModel.assign(firstRoom, Arrays.asList(firstState));
		deviceModel.assign(secondRoom, Arrays.asList(secondState));

		assertEquals(2, deviceModel.selectedDevices().size());

		assertTrue(deviceModel.selectedDevices().contains(firstState));
		assertTrue(deviceModel.selectedDevices().contains(secondState));

		deviceModel.assign(firstRoom, Collections.emptySet());
		deviceModel.assign(secondRoom, Collections.emptySet());

		assertEquals(0, deviceModel.selectedDevices().size());

		Mockito.verify(subject, Mockito.times(4)).notifyObservers(DeviceModel.Events.SeclectionChanged);
	}

	@Test
	void selectedDistinctSinglePercentValue() {
		deviceModel.assignType(DeviceType.Level);
		assertFalse(deviceModel.selectedDistinctSingleViewValue().isPresent());

		deviceModel.assign(firstRoom, Arrays.asList(firstState, secondState));
		deviceModel.assign(secondRoom, Arrays.asList(secondState));

		assertEquals(Optional.of("" + (int) (STATE_VALUE *100d)), deviceModel.selectedDistinctSingleViewValue());
	}

	@Test
	void selectedDistinctSinglePercentValueDifferent() {
		Mockito.when(firstState.value()).thenReturn(1d);

		deviceModel.assign(firstRoom, Arrays.asList(firstState, secondState));
		deviceModel.assign(secondRoom, Arrays.asList(secondState));

		assertEquals(Optional.empty(), deviceModel.selectedDistinctSingleViewValue());
	}

	@Test
	void isSelected() {
		assertFalse(deviceModel.isSelected());

		deviceModel.assign(secondRoom, Arrays.asList(secondState));

		assertTrue(deviceModel.isSelected());
	}

	@Test
	void assign() {
		deviceModel.assignType(DeviceType.Level);
		final int value = 50;
		deviceModel.assign("" +value);
		
		assertEquals(Optional.of(value/100d), deviceModel.value());
		Mockito.verify(subject).notifyObservers(DeviceModel.Events.ValueChanged);

	}
	
	
	@Test
	void assignBoolean() {
		deviceModel.assignType(DeviceType.State);
		
		deviceModel.assign(Boolean.TRUE);
		
		assertEquals(Optional.of(Boolean.TRUE), deviceModel.value());
		Mockito.verify(subject).notifyObservers(DeviceModel.Events.ValueChanged);

	}
	
	
	@Test
	void assignEmpty() {
		deviceModel.assignType(DeviceType.Level);
		
		deviceModel.assign("");
		
		assertEquals(Optional.empty(), deviceModel.value());
		Mockito.verify(subject).notifyObservers(DeviceModel.Events.ValueChanged);
	}
	
	@Test
	void assignInvalid() {
		deviceModel.assignType(DeviceType.Level);
		deviceModel.assign("x");
		
		assertEquals(Optional.empty(), deviceModel.value());
		Mockito.verify(subject).notifyObservers(DeviceModel.Events.ValueChanged);
	}
	
	@Test
	void assignOutOfRange() {
		deviceModel.assignType(DeviceType.Level);
		deviceModel.assign("101");
		
		assertEquals(Optional.empty(), deviceModel.value());
		Mockito.verify(subject).notifyObservers(DeviceModel.Events.ValueChanged);
	}
	
	
	@Test
	void assignType() {
		ReflectionTestUtils.setField(deviceModel, "value" , STATE_VALUE);
		final Map<String,Collection<State<?>>> selected = new HashMap<>();
		selected.put(firstRoom.name(), Arrays.asList(firstState));
		ReflectionTestUtils.setField(deviceModel, "selectedDevices" , selected);
		assertTrue(deviceModel.value().isPresent());
		assertTrue(deviceModel.isSelected());
		assertEquals(Optional.empty(), deviceModel.type());
		
		deviceModel.assignType(DeviceType.Level);
		
		assertEquals(Optional.of(DeviceType.Level), deviceModel.type());
		assertFalse(deviceModel.value().isPresent());
		assertFalse(deviceModel.isSelected());
		Mockito.verify(subject).notifyObservers(DeviceModel.Events.SeclectionChanged);
		Mockito.verify(subject).notifyObservers(DeviceModel.Events.TypeChanged);
		
		
	}
	
	@Test
	void  type() {
		assertFalse(deviceModel.isSelected());
		ReflectionTestUtils.setField(deviceModel, "type" , DeviceType.State);
		assertEquals(Optional.of(DeviceType.State), deviceModel.type());
		
	}
	
	@Test
	void clearSelection() {
		final Map<String,Collection<State<?>>> selected = new HashMap<>();
		selected.put(firstRoom.name(), Arrays.asList(firstState));
		ReflectionTestUtils.setField(deviceModel, "selectedDevices" , selected);
		assertTrue(deviceModel.isSelected());
		
		deviceModel.clearSelection();
		
		assertFalse(deviceModel.isSelected());
		Mockito.verify(subject).notifyObservers(DeviceModel.Events.SeclectionChanged);
	}
	
	@Test
	 final void convertBoolean() {
		
		@SuppressWarnings("unchecked")
		final State<Boolean> state = Mockito.mock(State.class);
		Mockito.when(state.value()).thenReturn(Boolean.TRUE);
		deviceModel.assignType(DeviceType.State);
		assertEquals(String.valueOf(Boolean.TRUE), deviceModel.convert(state));
	}
	
	@Test
	 final void convertDouble() {
		
		@SuppressWarnings("unchecked")
		final State<Double> state = Mockito.mock(State.class);
		Mockito.when(state.value()).thenReturn(STATE_VALUE);
		deviceModel.assignType(DeviceType.Level);
		assertEquals(String.valueOf(Integer.valueOf((int) (100*STATE_VALUE))), deviceModel.convert(state));
	}
	
	@Test
	 final void convertMissingType() {
		assertThrows(IllegalArgumentException.class, () -> deviceModel.convert((State<?>) Mockito.mock(State.class)));
	}
	
	@Test
	final void changedValues() {
		deviceModel.assignType(DeviceType.Level);
		deviceModel.assign("47");
		
		deviceModel.assign(firstRoom, Arrays.asList(firstState));
		deviceModel.assign(secondRoom, Arrays.asList(secondState));
	
		
		
		
		final Collection<State<Object>> results = deviceModel.changedValues();
		assertEquals(2, results.size());
		assertTrue(results.containsAll(Arrays.asList(firstState, secondState)));
		
		Mockito.verify(firstState).assign(0.47d);
		Mockito.verify(secondState).assign(0.47d);
		
	}
	
	@Test
	final void assignSynonyms() {
		
		final Map<?,?> synonyms = synonymsField();
		assertTrue(synonyms.isEmpty());
		
		deviceModel.assign(synonymList());
		
		assertEquals(1, synonyms.size());
		assertTrue(synonyms.containsKey(DEVIVE_NAME));
		assertEquals(SYNONYM, synonyms.get(DEVIVE_NAME));
		
	}

	private Collection<Synonym> synonymList() {
		final Synonym synonym = Mockito.mock(Synonym.class);
		Mockito.when(synonym.key()).thenReturn(DEVIVE_NAME);
		Mockito.when(synonym.value()).thenReturn(SYNONYM);
		return Arrays.asList(synonym);
	}

	private Map<?, ?> synonymsField() {
		return (Map<?, ?>) ReflectionTestUtils.getField(deviceModel, "deviceSynonyms");
	}
	
	@Test
	final void synonyms() {
		final State<?> state = Mockito.mock(State.class);
		Mockito.when(state.name()).thenReturn(DEVIVE_NAME);
		Mockito.when(state.function()).thenReturn(Optional.of(FUNCTION));
		
		deviceModel.assign(synonymList());
		
		assertEquals(FUNCTION+": " +SYNONYM, deviceModel.synonym(state));
	}
	
	@Test
	final void synonymsFunctionMissing() {
		final State<?> state = Mockito.mock(State.class);
		Mockito.when(state.name()).thenReturn(DEVIVE_NAME);
		
		deviceModel.assign(synonymList());
		
		assertEquals(SYNONYM, deviceModel.synonym(state));
	}
	
	@Test
	final void synonymsSynonymMissing() {
		final State<?> state = Mockito.mock(State.class);
		Mockito.when(state.name()).thenReturn(DEVIVE_NAME);
		Mockito.when(state.function()).thenReturn(Optional.of(FUNCTION));
		
		
		assertEquals(FUNCTION+": " +DEVIVE_NAME, deviceModel.synonym(state));
	}


	@Test
	final void isChangeDeviceAllowed() {
		Mockito.when(authentication.hasRole(Authority.Devices)).thenReturn(true);
	
		assertTrue(deviceModel.isChangeDeviceAllowed());
		
	}
	@Test
	final void isChangeDeviceAlloweRoleNotGranted() {
		assertFalse(deviceModel.isChangeDeviceAllowed());
	}
	
	@Test
	final void isChangeDeviceUserNotAware() {
		Mockito.when(subject.currentUser()).thenReturn(Optional.empty());
		
		assertFalse(deviceModel.isChangeDeviceAllowed());
	}
	
	@Test
	final void assignLocale() {
		deviceModel.assign(Locale.GERMAN);
		Mockito.verify(subject).assign(Locale.GERMAN);
	}
}
