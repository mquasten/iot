package de.mq.iot.resource.support;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.resource.ResourceIdentifier.ResourceType;
import de.mq.iot.state.Command;
import de.mq.iot.state.Commands;
import de.mq.iot.state.support.StateRepository;

@Service 
public class ResourceUpdateServiceImpl implements ResourceUpdateService {
	
	
	static final int MAX_SUB_COUNT = 111;
	static final String OPEN_WEATHER_KEY_VALUE = "607cd43d4d9b17d8a96df387fe4ede62";
	static final String OPEN_WEATHER_COUNTRY_VALUE = "de";
	static final String OPEN_WEATHER_CITY_VALUE = "Wegberg";
	static final String OPEN_WEATHER_VERSION_VALUE = "2.5";
	static final String OPEN_WEATHER_KEY_PARAM = "key";
	static final String OPEN_WEATHER_COUNTRY_PARAM = "country";
	static final String OPEN_WEATHER_CITY_PARAM = "city";
	static final String OPEN_WEATHER_VERSION_PARAM = "version";
	static final String OPEN_WEATHER_URL = "http://api.openweathermap.org/data/{version}/{resource}?q={city},{country}&appid={key}&units=metric";
	//static final String XMLPORT = "80";
	static final String XML_API_URL = "http://{host}:{port}/addons/xmlapi/{resource}";
	static final String PORT_PARAMETER_NAME = "port";
	static final String IP_PREFIX = "192.168.2.";
	static final String HOMEMATIC_HOST = "HOMEMATIC-CCU2";
	static final String HOST_PARAMETER_NAME = "host";
	private final ResourceIdentifierRepository resourceIdentifierRepository;
	private final Duration duration; 
	
	
	private final  StateRepository stateRepository; 
	
	ResourceUpdateServiceImpl(final ResourceIdentifierRepository resourceIdentifierRepository, final   StateRepository stateRepository,  @Value("${mongo.timeout:500}") final Integer timeout) {
		this.resourceIdentifierRepository = resourceIdentifierRepository;
		this.stateRepository=stateRepository;
		this.duration=Duration.ofMillis(timeout);
		
	}

	
	/* (non-Javadoc)
	 * @see de.mq.iot.state.support.IPUpdateService#update()
	 */
	@Override
	@Commands(commands = {  @Command( name = "updateIP", arguments = {}) })
	public final void updateIp() {
		final ResourceIdentifier existing  = resourceIdentifierRepository.findById(ResourceType.XmlApi).block(duration); 
	
		findHomematic(existing).ifPresent(resourceIdentifier -> updateResourceIdentifier(existing, resourceIdentifier));		
	}


	private void updateResourceIdentifier(final ResourceIdentifier existing, final ResourceIdentifier resourceIdentifier) {
	
		
		
		if( ! resourceIdentifier.parameters().equals(existing.parameters())) {
			
			resourceIdentifierRepository.save(resourceIdentifier).block(duration);
			System.out.println(String.format("updateIp %s to %s", existing.parameters().get(HOST_PARAMETER_NAME), resourceIdentifier.parameters().get(HOST_PARAMETER_NAME) ));
		}
	}

	
	private Optional<ResourceIdentifier> findHomematic(final ResourceIdentifier resourceIdentifier) {
		
		final ResourceIdentifier result = new ResourceIdentifierImpl(resourceIdentifier.id(), resourceIdentifier.uri());
		result.assign(resourceIdentifier.parameters());
		for(int i=100;i <MAX_SUB_COUNT; i++) {
			final Map<String,String> parameters = new HashMap<>(result.parameters());
			parameters.put(HOST_PARAMETER_NAME,  IP_PREFIX + i);
			result.assign(parameters);
			
			if( tryGuessHomematicIp(result)) {
				return Optional.of(result);
			}
		
			
		}
		return Optional.empty();
				
	}


	private boolean tryGuessHomematicIp(final ResourceIdentifier result) {
		try {
		
		
			return stateRepository.findVersion(result) > 1d;
		
		} catch (final Exception ex) {
			return false;
		}
	}

	


	@Override
	@Commands(commands = {  @Command( name = "updateResources", arguments = {}) })
	public final void updateResources() {
		updateIp();
		
		updateOpenWeatherApi();
		
		
	}


	private void updateOpenWeatherApi() {
		final ResourceIdentifier resourceIdentifier = new ResourceIdentifierImpl(ResourceType.OpenWeather, OPEN_WEATHER_URL) ; 
		final Map<String,String> parameters = new HashMap<>();
		parameters.put(OPEN_WEATHER_VERSION_PARAM, OPEN_WEATHER_VERSION_VALUE);
		parameters.put(OPEN_WEATHER_CITY_PARAM, OPEN_WEATHER_CITY_VALUE);
		parameters.put(OPEN_WEATHER_COUNTRY_PARAM, OPEN_WEATHER_COUNTRY_VALUE);
		parameters.put(OPEN_WEATHER_KEY_PARAM, OPEN_WEATHER_KEY_VALUE);
		resourceIdentifier.assign(parameters);
		resourceIdentifierRepository.save(resourceIdentifier);
		System.out.println("Update " + resourceIdentifier.uri() +":"  + parameters);

	}


	
	
		
	
	
	
}
