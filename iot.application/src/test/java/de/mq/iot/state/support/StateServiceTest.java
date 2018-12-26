package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
	
	@SuppressWarnings("unchecked")
	private final StateConverter<Boolean> doubleStateConverter = Mockito.mock(StateConverter.class);
	private final Collection<StateConverter<?>> stateConverters = Arrays.asList(booleanStateConverter, doubleStateConverter);
	private StateService stateService;
	private final ResourceIdentifier resourceIdentifier = Mockito.mock(ResourceIdentifier.class);
	@SuppressWarnings("unchecked")
	private final Mono<ResourceIdentifier> mongoMono = Mockito.mock(Mono.class);

	private final Map<String, String> booleanStateMap = new HashMap<>();
	private final State<?> booleanState = Mockito.mock(State.class);

	private final Map<Long, String> rooms = new HashMap<>();

	
	private final Map<String,String> firstState = new HashMap<>();

	
	private final Map<String,String> secondState = new HashMap<>();


	private final Map<String,String>thirdState =new HashMap<>();

	
	private final Map<String,String> fourthState= new HashMap<>();
	
	
	private List<State<Double>> convertedStates = new ArrayList<>();

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
		convertedStates.clear();
		Mockito.doAnswer(answere -> {
			final Map<String,String> map = answere.getArgument(0);
			final State<Double> state = state(map);
			convertedStates.add(state);
			return state;
		}).when(doubleStateConverter).convert(Mockito.anyMap());
		Mockito.doReturn(Arrays.asList("4" , "LEVEL")).when(doubleStateConverter).keys();
		

		Mockito.doReturn(BooleanStateConverterImpl.BOOLEN_STATE_TYPES).when(booleanStateConverter).keys();
		Mockito.doReturn(Optional.of(resourceIdentifier)).when(mongoMono).blockOptional(Duration.ofMillis(TIMEOUT));
		Mockito.doReturn(mongoMono).when(resourceIdentifierRepository).findById(ResourceType.XmlApi);

		booleanStateMap.put(StateConverter.KEY_TYPE, BooleanStateConverterImpl.BOOLEN_STATE_TYPES.iterator().next());

		Mockito.doReturn(Arrays.asList(booleanStateMap)).when(stateRepository).findStates(resourceIdentifier);

		Mockito.doReturn(booleanState).when(booleanStateConverter).convert(booleanStateMap);
		stateService = new StateServiceImpl(resourceIdentifierRepository, stateRepository, stateConverters, TIMEOUT);

		firstState.put(AbstractStateConverter.KEY_ID, ""+ FIRST_CHANNEL);
		firstState.put(AbstractStateConverter.KEY_VALUE, ""+ 0.25);
		firstState.put(AbstractStateConverter.KEY_NAME, FIRST_CHANNEL + ":" + FIRST_ROOM);
		firstState.put(AbstractStateConverter.KEY_TYPE, DoubleStateConverterImpl.DOUBLE_STATE_TYPES.iterator().next());
		firstState.put(AbstractStateConverter.KEY_TIMESTAMP, ""+System.currentTimeMillis());
		
		secondState.put(AbstractStateConverter.KEY_ID, ""+ SECOND_CHANNEL);
		secondState.put(AbstractStateConverter.KEY_VALUE, ""+ 0.50);
		secondState.put(AbstractStateConverter.KEY_NAME, SECOND_CHANNEL + ":" + FIRST_ROOM);
		secondState.put(AbstractStateConverter.KEY_TYPE, DoubleStateConverterImpl.DOUBLE_STATE_TYPES.iterator().next());
		secondState.put(AbstractStateConverter.KEY_TIMESTAMP, ""+System.currentTimeMillis());

		thirdState.put(AbstractStateConverter.KEY_ID, ""+ THIRD_CHANNEL);
		thirdState.put(AbstractStateConverter.KEY_VALUE, ""+ 0.75);
		thirdState.put(AbstractStateConverter.KEY_NAME, THIRD_CHANNEL + ":" + SECOND_ROOM);
		thirdState.put(AbstractStateConverter.KEY_TYPE, DoubleStateConverterImpl.DOUBLE_STATE_TYPES.iterator().next());
		thirdState.put(AbstractStateConverter.KEY_TIMESTAMP, ""+System.currentTimeMillis());

		fourthState.put(AbstractStateConverter.KEY_ID, "4711");
		fourthState.put(AbstractStateConverter.KEY_VALUE, ""+ 1.00);
		fourthState.put(AbstractStateConverter.KEY_NAME, "xxx");
		fourthState.put(AbstractStateConverter.KEY_TYPE, DoubleStateConverterImpl.DOUBLE_STATE_TYPES.iterator().next());
		fourthState.put(AbstractStateConverter.KEY_TIMESTAMP, ""+System.currentTimeMillis());
		
		
		
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
		
		
		
		Mockito.doReturn(Arrays.asList(firstState,secondState,thirdState)).when(stateRepository).findDeviceStates(resourceIdentifier);
		
		final ArgumentCaptor<ResourceIdentifier> resourceIdentifierCaptor = ArgumentCaptor.forClass(ResourceIdentifier.class);
		
		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Collection<Entry<Long,String>>> entryListCaptor = ArgumentCaptor.forClass(Collection.class);
		
		
		Mockito.when(stateRepository.findChannelIds(resourceIdentifier, Arrays.asList(StateServiceImpl.FUNCTION))).thenReturn(Arrays.asList(FIRST_CHANNEL, SECOND_CHANNEL, THIRD_CHANNEL));
		Mockito.when(stateRepository.findCannelsRooms(resourceIdentifier)).thenReturn(rooms);
		Mockito.when(stateRepository.findDeviceStates(resourceIdentifier)).thenReturn(Arrays.asList(firstState, secondState, thirdState, fourthState));
		
		
		final State<Double> first = state(firstState);
		final State<Double> second = state(secondState);
		final State<Double> third = state(thirdState);
		
		final List<Room> results = new ArrayList<>(stateService.update(Arrays.asList(first, second, third)));
		
		assertEquals(2, results.size());
		
		Mockito.verify(stateRepository).changeState(resourceIdentifierCaptor.capture(), entryListCaptor.capture());
		
		assertEquals(resourceIdentifier, resourceIdentifierCaptor.getValue());
		
		final Map<Long,String> stateValues = entryListCaptor.getValue().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		assertEquals(3, stateValues.size());
		assertEquals(""+first.value(), stateValues.get(FIRST_CHANNEL));
		assertEquals(""+second.value(), stateValues.get(SECOND_CHANNEL));
		assertEquals(""+third.value(), stateValues.get(THIRD_CHANNEL));
		
		
		
		
		assertEquals(2, results.size());
		assertEquals(SECOND_ROOM, results.get(0).name());
		assertEquals(1, results.get(0).states().size());
		assertEquals(THIRD_CHANNEL, results.get(0).states().stream().findFirst().get().id());
		assertEquals(THIRD_CHANNEL + ":" + SECOND_ROOM, results.get(0).states().stream().findFirst().get().name());
	

		assertEquals(FIRST_ROOM, results.get(1).name());
		assertEquals(2, results.get(1).states().size());
		
		
		final List<State<?>> secondResult = results.get(1).states().stream().collect(Collectors.toList());
		assertEquals(FIRST_CHANNEL, secondResult.get(0).id());
		assertEquals(FIRST_CHANNEL + ":" + FIRST_ROOM, secondResult.get(0).name());
		
		
		
		assertEquals(SECOND_CHANNEL, secondResult.get(1).id());
		assertEquals(SECOND_CHANNEL + ":" + FIRST_ROOM, secondResult.get(1).name());
			
		
		
		assertEquals(4, convertedStates.size());
		
		
		Mockito.verify(convertedStates.get(0),Mockito.times(1)).assign(0.25d);
		Mockito.verify(convertedStates.get(1),Mockito.times(1)).assign(0.5d);
		Mockito.verify(convertedStates.get(2),Mockito.times(1)).assign(0.75d);
		Mockito.verify(convertedStates.get(3),Mockito.never()).assign(Mockito.any());
	}
	
	@SuppressWarnings("unchecked")
	private State<Double> state(final Map<String,String> map) {
		final State<Double>  state = Mockito.mock(State.class);
		Mockito.doReturn(Long.valueOf(map.get(AbstractStateConverter.KEY_ID))).when(state).id();
		Mockito.doReturn(map.get(AbstractStateConverter.KEY_NAME)).when(state).name();
		Mockito.doReturn(Double.valueOf(map.get(AbstractStateConverter.KEY_VALUE))).when(state).value();
		return state;
	}

	@Test
	void deviceStates() {

		Mockito.when(stateRepository.findChannelIds(resourceIdentifier,  Arrays.asList(StateServiceImpl.FUNCTION))).thenReturn(Arrays.asList(FIRST_CHANNEL, SECOND_CHANNEL, THIRD_CHANNEL));
		Mockito.when(stateRepository.findCannelsRooms(resourceIdentifier)).thenReturn(rooms);
		Mockito.when(stateRepository.findDeviceStates(resourceIdentifier)).thenReturn(Arrays.asList(firstState, secondState, thirdState, fourthState));

		final List<Room> results = new ArrayList<>(stateService.deviceStates());

		assertEquals(2, results.size());
		assertEquals(SECOND_ROOM, results.get(0).name());
		assertEquals(1, results.get(0).states().size());
		assertEquals(THIRD_CHANNEL, results.get(0).states().stream().findFirst().get().id());
		assertEquals(THIRD_CHANNEL + ":" + SECOND_ROOM, results.get(0).states().stream().findFirst().get().name());
		assertEquals(0.75d, results.get(0).states().stream().findFirst().get().value());

		assertEquals(FIRST_ROOM, results.get(1).name());
		assertEquals(2, results.get(1).states().size());
		
		
		final List<State<?>> second = results.get(1).states().stream().collect(Collectors.toList());
		assertEquals(FIRST_CHANNEL, second.get(0).id());
		assertEquals(FIRST_CHANNEL + ":" + FIRST_ROOM, second.get(0).name());
		assertEquals(0.25d, second.get(0).value());	
		
		
		assertEquals(SECOND_CHANNEL, second.get(1).id());
		assertEquals(SECOND_CHANNEL + ":" + FIRST_ROOM, second.get(1).name());
		assertEquals(0.5d, second.get(1).value());	
	
	}

	@Test
	void deviceStatesNoRoom() {
		Mockito.when(stateRepository.findChannelIds(resourceIdentifier,  Arrays.asList(StateServiceImpl.FUNCTION))).thenReturn(Arrays.asList(FIRST_CHANNEL, SECOND_CHANNEL, THIRD_CHANNEL));
		Mockito.when(stateRepository.findDeviceStates(resourceIdentifier)).thenReturn(Arrays.asList(firstState, secondState, thirdState, fourthState));

		final List<Room> results = new ArrayList<>(stateService.deviceStates());

		assertEquals(1, results.size());
		assertEquals(StateServiceImpl.MISSING_ROOM_NAME, results.stream().findFirst().get().name());

		assertEquals(3, results.get(0).states().size());

		final List<State<?>>  states = results.get(0).states().stream().collect(Collectors.toList());
		
		assertEquals(FIRST_CHANNEL, states.get(0).id());
		assertEquals(0.25d, states.get(0).value());
		assertEquals(FIRST_CHANNEL +":" +FIRST_ROOM, states.get(0).name());
		
		
		assertEquals(SECOND_CHANNEL, states.get(1).id());
		assertEquals(0.5d, states.get(1).value());
		assertEquals(SECOND_CHANNEL +":" +FIRST_ROOM, states.get(1).name());
		
		
		assertEquals(THIRD_CHANNEL, states.get(2).id());
		assertEquals(0.75d, states.get(2).value());
		assertEquals(THIRD_CHANNEL +":" +SECOND_ROOM, states.get(2).name());
		
	}

}
