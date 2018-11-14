package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.resource.ResourceIdentifier.ResourceType;
import de.mq.iot.resource.support.ResourceIdentifierRepository;
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
	
	private final Map<String,String> booleanStateMap = new HashMap<>();
	private final State<?>  booleanState = Mockito.mock(State.class);
	
	private final Map<Long, String> rooms = new HashMap<>();
	
	@BeforeEach
	void setup() {
	
		rooms.put(1952L, FIRST_ROOM);
		rooms.put(1427L, FIRST_ROOM);
		rooms.put(FIRST_CHANNEL, SECOND_ROOM);
		rooms.put(1431L, FIRST_ROOM);
		rooms.put(1944L, FIRST_ROOM);
		rooms.put(SECOND_CHANNEL, SECOND_ROOM);
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
	}
	@Test
	void states() {
		final Collection<State<?>> states = stateService.states();
		
		assertEquals(1, states.size());
		assertEquals(booleanState, states.stream().findAny().orElseThrow(() -> new IllegalArgumentException(("Single result expected.") )));
		
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
	void deviceStates() {
		

		Mockito.when(stateRepository.findChannelIds(resourceIdentifier, StateServiceImpl.FUNCTION)).thenReturn(Arrays.asList(FIRST_CHANNEL,SECOND_CHANNEL,THIRD_CHANNEL));
		
		Mockito.when(stateRepository.findCannelsRooms(resourceIdentifier)).thenReturn(rooms);
		stateService.deviceStates();
	}

}
