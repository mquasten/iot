package de.mq.iot.state.support;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.resource.ResourceIdentifier.ResourceType;

import de.mq.iot.resource.support.ResourceIdentifierRepository;
import de.mq.iot.state.Room;
import de.mq.iot.state.State;
import de.mq.iot.state.StateService;

@Service
class StateServiceImpl implements StateService {

	static final String MISSING_ROOM_NAME = "?";
	static final String FUNCTION = "Rolladen";
	private final Duration timeout;
	private final ResourceIdentifierRepository resourceIdentifierRepository;
	private final StateRepository stateRepository;
	private final Map<String, StateConverter<State<?>>> stateConverters = new HashMap<>();
	private ConversionService conversionService = new DefaultConversionService();

	@SuppressWarnings("unchecked")
	@Autowired
	StateServiceImpl(final ResourceIdentifierRepository resourceIdentifierRepository, final StateRepository stateRepository, final Collection<StateConverter<?>> stateConverters, @Value("${mongo.timeout:500}") final Integer timeout) {
		this.resourceIdentifierRepository = resourceIdentifierRepository;
		this.stateRepository = stateRepository;
		//this.stateConverters.putAll((Map<? extends String, ? extends StateConverter<State<?>>>) (Map<?, ?>) stateConverters.stream().collect(Collectors.toMap(StateConverter::key, stateConverter -> stateConverter)));
		
		stateConverters.forEach(converter -> converter.keys().forEach(key -> this.stateConverters.put(key, (StateConverter<State<?>>) converter)));
		
		this.timeout = Duration.ofMillis(timeout);
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.state.StateService#states()
	 */
	public Collection<State<?>> states() {
		final ResourceIdentifier resourceIdentifier = resourceIdentifier();
		return stateRepository.findStates(resourceIdentifier).stream().map(this::mapToState).collect(Collectors.toList());
	}

	private ResourceIdentifier resourceIdentifier() {
		return resourceIdentifierRepository.findById(ResourceType.XmlApi).blockOptional(timeout).orElseThrow(() -> new EmptyResultDataAccessException(String.format("ResourceType: %s not found in Database.", ResourceType.XmlApi), 1));
	}

	private State<?> mapToState(Map<String, String> stateMap) {

		Assert.isTrue(stateMap.containsKey(StateConverter.KEY_TYPE), "Type must be specified.");
		final String type = stateMap.get(StateConverter.KEY_TYPE);
		
		Assert.isTrue(stateConverters.containsKey(type), "No Converter found vor type " + type + ".");
		final StateConverter<?> converter = stateConverters.get(type);
		return converter.convert(stateMap);
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.state.StateService#update(de.mq.iot.state.support.State)
	 */
	public void update(final State<?> state) {
		final ResourceIdentifier resourceIdentifier = resourceIdentifier();
		stateRepository.changeState(resourceIdentifier, state);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.state.StateService#deviceStates()
	 */
	@Override
	public Collection<Room> deviceStates() {
		final ResourceIdentifier resourceIdentifier = resourceIdentifier();

		final Map<Long,String> channelIds = stateRepository.findChannelIds(resourceIdentifier, Arrays.asList(FUNCTION)).stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		final Map<Long, String> rooms = stateRepository.findCannelsRooms(resourceIdentifier);

		final Collection<State<?>> states = stateRepository.findDeviceStates(resourceIdentifier).stream().map(map -> mapToState(map)).sorted((state1, state2) -> state1.name().compareToIgnoreCase(state2.name())).filter(state -> channelIds.containsKey(state.id())).collect(Collectors.toList());

		final Map<String, Room> results = new HashMap<>();

		states.forEach(state -> {

			final String roomName = rooms.containsKey(state.id()) ? rooms.get(state.id()) : MISSING_ROOM_NAME;

			if (!results.containsKey(roomName)) {
				results.put(roomName, new RoomImpl(roomName));
			}

			((RoomImpl) results.get(roomName)).assign(state);

		});

		return results.values().stream().sorted((r1, r2) -> r1.name().compareToIgnoreCase(r2.name())).collect(Collectors.toList());

	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.StateService#update(java.util.Collection)
	 */
	@Override
	public Collection<Room> update(final Collection<State<?>> states) {
		final ResourceIdentifier resourceIdentifier = resourceIdentifier();
		
		final Collection<Entry<Long,String>> entries = states.stream().collect(Collectors.toMap(State::id, state-> conversionService.convert(state.value(), String.class))).entrySet();
		final Map<Long,? > values = states.stream().collect(Collectors.toMap(State::id, state-> state.value()));
		stateRepository.changeState(resourceIdentifier,  entries);
		
		
		final Collection<Room> deviceStates = deviceStates();
	
		deviceStates.forEach(room -> room.states().stream().filter(state -> values.containsKey(state.id())).forEach(state -> state.assign( values.get(state.id()))));
		return deviceStates;
		
	}

}
