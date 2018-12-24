package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.resource.ResourceIdentifier.ResourceType;
import de.mq.iot.resource.support.ResourceIdentifierRepository;
import de.mq.iot.state.Room;
import de.mq.iot.state.State;
import de.mq.iot.state.StateService;
import reactor.core.publisher.Mono;

class StateServiceTest {

	private static final long THIRD_CHANNEL = 4669L;

	private static final long SECOND_CHANNEL = 4665L;

	private static final long FIRST_CHANNEL = 4661L;

	private static final String SECOND_ROOM = "Eßzimmer (unten)";

	private static final String FIRST_ROOM = "Schlafzimmer (oben)";

	private static final int TIMEOUT = 500;

	private final ResourceIdentifierRepository resourceIdentifierRepository = Mockito.mock(ResourceIdentifierRepository.class);

	private final StateRepository stateRepository = Mockito.mock(StateRepository.class);
	@SuppressWarnings("unchecked")
	private final StateConverter<Boolean> booleanStateConverter = Mockito.mock(StateConverter.class);
	private final Collection<StateConverter<?>> stateConverters = Arrays.asList(booleanStateConverter);
	private StateService stateService;
	private final ResourceIdentifier resourceIdentifier = Mockito.mock(ResourceIdentifier.class);
	@SuppressWarnings("unchecked")
	private final Mono<ResourceIdentifier> mongoMono = Mockito.mock(Mono.class);

	private final Map<String, String> booleanStateMap = new HashMap<>();
	private final State<?> booleanState = Mockito.mock(State.class);

	private final Map<Long, String> rooms = new HashMap<>();

	@SuppressWarnings("unchecked")
	private final State<Double> firstState = Mockito.mock(State.class);

	@SuppressWarnings("unchecked")
	private final State<Double> secondState = Mockito.mock(State.class);

	@SuppressWarnings("unchecked")
	private final State<Double> thirdState = Mockito.mock(State.class);

	@SuppressWarnings("unchecked")
	private final State<Double> fourthState = Mockito.mock(State.class);

	@BeforeEach
	void setup() {

		rooms.put(1952L, FIRST_ROOM);
		rooms.put(1427L, FIRST_ROOM);
		rooms.put(FIRST_CHANNEL, FIRST_ROOM);
		rooms.put(1431L, FIRST_ROOM);
		rooms.put(1944L, FIRST_ROOM);
		rooms.put(SECOND_CHANNEL, FIRST_ROOM);
		rooms.put(1948L, FIRST_ROOM);
		rooms.put(THIRD_CHANNEL, SECOND_ROOM);
		rooms.put(1423L, FIRST_ROOM);

		Mockito.doReturn(BooleanStateConverterImpl.BOOLEN_STATE_TYPE).when(booleanStateConverter).key();
		Mockito.doReturn(Optional.of(resourceIdentifier)).when(mongoMono).blockOptional(Duration.ofMillis(TIMEOUT));
		Mockito.doReturn(mongoMono).when(resourceIdentifierRepository).findById(ResourceType.XmlApi);

		booleanStateMap.put(StateConverter.KEY_TYPE, BooleanStateConverterImpl.BOOLEN_STATE_TYPE);

		Mockito.doReturn(Arrays.asList(booleanStateMap)).when(stateRepository).findStates(resourceIdentifier);

		Mockito.doReturn(booleanState).when(booleanStateConverter).convert(booleanStateMap);
		stateService = new StateServiceImpl(resourceIdentifierRepository, stateRepository, stateConverters, TIMEOUT);

		Mockito.when(firstState.id()).thenReturn(FIRST_CHANNEL);
		Mockito.when(firstState.value()).thenReturn(25d);
		Mockito.when(firstState.name()).thenReturn(FIRST_CHANNEL + ":" + FIRST_ROOM);

		Mockito.when(secondState.id()).thenReturn(SECOND_CHANNEL);
		Mockito.when(secondState.value()).thenReturn(50d);
		Mockito.when(secondState.name()).thenReturn(SECOND_CHANNEL + ":" + SECOND_ROOM);

		Mockito.when(thirdState.id()).thenReturn(THIRD_CHANNEL);
		Mockito.when(thirdState.value()).thenReturn(75d);
		Mockito.when(thirdState.name()).thenReturn(THIRD_CHANNEL + ":" + SECOND_ROOM);

		Mockito.when(fourthState.id()).thenReturn(4711L);
		Mockito.when(fourthState.value()).thenReturn(100d);
		Mockito.when(fourthState.name()).thenReturn("xxx");
	}

	@Test
	void states() {
		final Collection<State<?>> states = stateService.states();

		assertEquals(1, states.size());
		assertEquals(booleanState, states.stream().findAny().orElseThrow(() -> new IllegalArgumentException(("Single result expected."))));

		Mockito.verify(resourceIdentifierRepository).findById(ResourceType.XmlApi);
		Mockito.verify(stateRepository).findStates(resourceIdentifier);
		Mockito.verify(booleanStateConverter).convert(booleanStateMap);
	}

	@Test
	void statesResourceIdentifierNotFound() {
		Mockito.doReturn(Optional.empty()).when(mongoMono).blockOptional(Duration.ofMillis(TIMEOUT));

		assertThrows(EmptyResultDataAccessException.class, () -> stateService.states());
	}

	@Test
	void update() {
		final State<?> state = Mockito.mock(State.class);
		stateService.update(state);

		Mockito.verify(stateRepository).changeState(resourceIdentifier, state);
	}
	
	@Test
	void updateAll() {
		@SuppressWarnings("unchecked")
		final State<Double> firstState = Mockito.mock(State.class);
		Mockito.doReturn(FIRST_CHANNEL).when(firstState).id();
		double firstValue = 0.25d;
		Mockito.doReturn(firstValue).when(firstState).value();
		@SuppressWarnings("unchecked")
		final State<Double> secondState = Mockito.mock(State.class);
		Mockito.doReturn(SECOND_CHANNEL).when(secondState).id();
		double secondValue = 0.5d;
		Mockito.doReturn(secondValue).when(secondState).value();
		
		@SuppressWarnings("unchecked")
		final State<Double> thirdState = Mockito.mock(State.class);
		Mockito.doReturn(THIRD_CHANNEL).when(thirdState).id();
		double thirdValue = 0.75d;
		Mockito.doReturn(thirdValue).when(thirdState).value();
		
		
		final ArgumentCaptor<ResourceIdentifier> resourceIdentifierCaptor = ArgumentCaptor.forClass(ResourceIdentifier.class);
		
		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Collection<Entry<Long,String>>> entryListCaptor = ArgumentCaptor.forClass(Collection.class);
		
		
		
		stateService.update(Arrays.asList(firstState, secondState, thirdState));
		Mockito.verify(stateRepository).changeState(resourceIdentifierCaptor.capture(), entryListCaptor.capture());
		
		assertEquals(resourceIdentifier, resourceIdentifierCaptor.getValue());
		
		final Map<Long,String> stateValues = entryListCaptor.getValue().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		assertEquals(3, stateValues.size());
		assertEquals(""+firstValue, stateValues.get(FIRST_CHANNEL));
		assertEquals(""+secondValue, stateValues.get(SECOND_CHANNEL));
		assertEquals(""+thirdValue, stateValues.get(THIRD_CHANNEL));
	}

	@Test
	void deviceStates() {

		Mockito.when(stateRepository.findChannelIds(resourceIdentifier, StateServiceImpl.FUNCTION)).thenReturn(Arrays.asList(FIRST_CHANNEL, SECOND_CHANNEL, THIRD_CHANNEL));
		Mockito.when(stateRepository.findCannelsRooms(resourceIdentifier)).thenReturn(rooms);
		Mockito.when(stateRepository.findDeviceStates(resourceIdentifier)).thenReturn(Arrays.asList(firstState, secondState, thirdState, fourthState));

		final List<Room> results = new ArrayList<>(stateService.deviceStates());

		assertEquals(2, results.size());
		assertEquals(SECOND_ROOM, results.get(0).name());
		assertEquals(1, results.get(0).states().size());
		assertEquals(Optional.of(thirdState), results.get(0).states().stream().findFirst());

		assertEquals(FIRST_ROOM, results.get(1).name());
		assertEquals(2, results.get(1).states().size());
		assertTrue(results.get(1).states().contains(firstState));
		assertTrue(results.get(1).states().contains(secondState));
	}

	@Test
	void deviceStatesNoRoom() {
		Mockito.when(stateRepository.findChannelIds(resourceIdentifier, StateServiceImpl.FUNCTION)).thenReturn(Arrays.asList(FIRST_CHANNEL, SECOND_CHANNEL, THIRD_CHANNEL));
		Mockito.when(stateRepository.findDeviceStates(resourceIdentifier)).thenReturn(Arrays.asList(firstState, secondState, thirdState, fourthState));

		final List<Room> results = new ArrayList<>(stateService.deviceStates());

		assertEquals(1, results.size());
		assertEquals(StateServiceImpl.MISSING_ROOM_NAME, results.stream().findFirst().get().name());

		assertEquals(3, results.get(0).states().size());

		assertTrue(results.get(0).states().contains(firstState));
		assertTrue(results.get(0).states().contains(secondState));
		assertTrue(results.get(0).states().contains(thirdState));
	}

}
