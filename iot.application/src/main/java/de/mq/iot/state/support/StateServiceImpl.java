package de.mq.iot.state.support;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
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

	private final Duration timeout;
	private final ResourceIdentifierRepository resourceIdentifierRepository;
	private final StateRepository stateRepository;
	private final Map<String, StateConverter<State<?>>> stateConverters = new HashMap<>();
	private final ConversionService conversionService;

	static final String HOST_PARAMETER_NAME = "host";

	@SuppressWarnings("unchecked")
	@Autowired
	StateServiceImpl(final ResourceIdentifierRepository resourceIdentifierRepository, final StateRepository stateRepository, final Collection<StateConverter<?>> stateConverters, final ConversionService conversionService, @Value("${mongo.timeout:500}") final Integer timeout) {
		this.resourceIdentifierRepository = resourceIdentifierRepository;
		this.stateRepository = stateRepository;
		this.conversionService = conversionService;

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
	public Collection<Room> deviceStates(final Collection<DeviceType> types) {
		final ResourceIdentifier resourceIdentifier = resourceIdentifier();

		final Map<Long, String> channelIds = stateRepository.findChannelIds(resourceIdentifier).stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (first, second) -> first));

		final Map<Long, String> rooms = stateRepository.findCannelsRooms(resourceIdentifier);

		final Collection<State<?>> states = stateRepository.findDeviceStates(resourceIdentifier, types).stream().map(map -> mapToState(map)).sorted((state1, state2) -> state1.name().compareToIgnoreCase(state2.name())).filter(state -> channelIds.containsKey(state.id())).collect(Collectors.toList());

		final Map<String, Room> results = new HashMap<>();

		states.forEach(state -> {

			final String roomName = rooms.containsKey(state.id()) ? rooms.get(state.id()) : MISSING_ROOM_NAME;

			if (!results.containsKey(roomName)) {
				results.put(roomName, new RoomImpl(roomName));
			}

			state.assignFunction(channelIds.get(state.id()));
			((RoomImpl) results.get(roomName)).assign(state);

		});

		return results.values().stream().sorted((r1, r2) -> r1.name().compareToIgnoreCase(r2.name())).collect(Collectors.toList());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.state.StateService#update(java.util.Collection)
	 */
	@Override
	public void update(final Collection<State<Object>> states) {
		final ResourceIdentifier resourceIdentifier = resourceIdentifier();

		final Collection<Entry<Long, String>> entries = states.stream().collect(Collectors.toMap(State::id, state -> conversionService.convert(state.value(), String.class))).entrySet();
		stateRepository.changeState(resourceIdentifier, entries);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.state.StateService#deviceTypes()
	 */
	@Override
	public final Collection<DeviceType> deviceTypes() {
		return Arrays.asList(DeviceType.Level, DeviceType.State);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.state.StateService#pingAndUpdateIp(java.lang.String)
	 */
	@Override
	public boolean pingAndUpdateIp(final String ip) {
		Assert.hasText(ip, "Ip is required.");
		final ResourceIdentifier resourceIdentifier = resourceIdentifier();

		final String existingIp = Optional.ofNullable(resourceIdentifier.parameters().get(HOST_PARAMETER_NAME)).orElse("");
		final Map<String, String> parameter = new HashMap<>(resourceIdentifier.parameters());
		parameter.put(HOST_PARAMETER_NAME, ip.trim());
		resourceIdentifier.assign(parameter);
		if (!tryGuessHomematicIp(resourceIdentifier)) {
			return false;
		}

		if (existingIp.trim().equals(ip.trim())) {
			return true;
		}

		resourceIdentifierRepository.save(resourceIdentifier).block(timeout);

		return true;

	}

	private boolean tryGuessHomematicIp(final ResourceIdentifier resourceIdentifier) {
		try {

			return stateRepository.findVersion(resourceIdentifier) > 1d;

		} catch (final Exception ex) {
			return false;
		}
	}
}
