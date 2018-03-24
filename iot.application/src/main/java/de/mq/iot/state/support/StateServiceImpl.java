package de.mq.iot.state.support;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.resource.ResourceIdentifier.ResourceType;
import de.mq.iot.resource.support.ResourceIdentifierRepository;
import de.mq.iot.state.StateService;

@Service
class StateServiceImpl implements StateService {

	private final Duration timeout;
	private final ResourceIdentifierRepository resourceIdentifierRepository;
	private final StateRepository stateRepository;
	private final Map<String, StateConverter<State<?>>> stateConverters = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Autowired
	StateServiceImpl(final ResourceIdentifierRepository resourceIdentifierRepository, final StateRepository stateRepository, final Collection<StateConverter<?>> stateConverters, @Value("${mongo.timeout:500}") final Integer timeout) {
		this.resourceIdentifierRepository = resourceIdentifierRepository;
		this.stateRepository = stateRepository;
		this.stateConverters.putAll((Map<String, ? extends StateConverter<State<?>>>) stateConverters.stream().collect(Collectors.toMap(StateConverter::key, stateConverter -> stateConverter)));
		this.timeout = Duration.ofMillis(timeout);
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.StateService#states()
	 */
	public Collection<State<?>> states() {
		final ResourceIdentifier resourceIdentifier = resourceIdentifierRepository.findById(ResourceType.XmlApiSysVarlist).blockOptional(timeout).orElseThrow(() -> new EmptyResultDataAccessException(String.format("ResourceType: %s not found in Database.", ResourceType.XmlApiSysVarlist), 1));
		return stateRepository.findStates(resourceIdentifier).stream().map(this::mapToState).collect(Collectors.toList());

	}

	private State<?> mapToState(Map<String, String> stateMap) {

		Assert.isTrue(stateMap.containsKey(StateConverter.KEY_TYPE), "Type must be specified.");
		final String type = stateMap.get(StateConverter.KEY_TYPE);
		Assert.isTrue(stateConverters.containsKey(type), "No Converter found vor type " + type + ".");
		final StateConverter<?> converter = stateConverters.get(type);
		return converter.convert(stateMap);
	}
}
