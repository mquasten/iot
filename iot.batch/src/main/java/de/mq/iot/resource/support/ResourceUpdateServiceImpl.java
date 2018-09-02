package de.mq.iot.resource.support;

import java.net.InetAddress;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.resource.ResourceIdentifier.ResourceType;
import de.mq.iot.state.Commands;
import de.mq.iot.state.Command;
import reactor.core.publisher.Mono;

@Service 
public class ResourceUpdateServiceImpl implements ResourceUpdateService {
	
	
	static final String OPEN_WEATHER_KEY_VALUE = "607cd43d4d9b17d8a96df387fe4ede62";
	static final String OPEN_WEATHER_COUNTRY_VALUE = "de";
	static final String OPEN_WEATHER_CITY_VALUE = "Wegberg";
	static final String OPEN_WEATHER_VERSION_VALUE = "2.5";
	static final String OPEN_WEATHER_KEY_PARAM = "key";
	static final String OPEN_WEATHER_COUNTRY_PARAM = "country";
	static final String OPEN_WEATHER_CITY_PARAM = "city";
	static final String OPEN_WEATHER_VERSION_PARAM = "version";
	static final String OPEN_WEATHER_URL = "http://api.openweathermap.org/data/{version}/{resource}?q={city},{country}&appid={key}&units=metric";
	static final String XMLPORT = "80";
	static final String XML_API_URL = "http://{host}:{port}/addons/xmlapi/{resource}";
	static final String PORT_PARAMETER_NAME = "port";
	static final String IP_PREFIX = "192.168.2.";
	static final String HOMEMATIC_HOST = "HOMEMATIC-CCU2";
	static final String HOST_PARAMETER_NAME = "host";
	private final ResourceIdentifierRepository resourceIdentifierRepository;
	private final Duration duration; 
	ResourceUpdateServiceImpl(final ResourceIdentifierRepository resourceIdentifierRepository,  @Value("${mongo.timeout:500}") final Integer timeout) {
		this.resourceIdentifierRepository = resourceIdentifierRepository;
		this.duration=Duration.ofMillis(timeout);
	}

	
	/* (non-Javadoc)
	 * @see de.mq.iot.state.support.IPUpdateService#update()
	 */
	@Override
	@Commands(commands = {  @Command( name = "updateIP", arguments = {}) })
	public final void updateIp() {
		
		final  Map<String,String> ips = IntStream.range(100, 111).mapToObj(address -> toEntry(address)).filter(entry -> ! entry.getKey().startsWith("192")).collect(Collectors.toMap( Entry::getKey, Entry::getValue));
		
		if( ! ips.containsKey(HOMEMATIC_HOST) ) {
		   System.out.println("HOMEMATIC-CCU2 not found!");	
		   return;
		}
		
		System.out.println(HOMEMATIC_HOST +":" + ips.get(HOMEMATIC_HOST));
		
		
		
		final Mono<ResourceIdentifier> mono =  resourceIdentifierRepository.findById(ResourceType.XmlApi);
	
		final ResourceIdentifier resourceIdentifier  = mono.block(duration); 
		
		final String host = resourceIdentifier.parameters().get(HOST_PARAMETER_NAME);
		
		Assert.notNull(host, "Host is mandatory.");
		System.out.println("Existing host: " + host);
		
	
		if(host.equals(ips.get(HOMEMATIC_HOST))) {
			System.out.println("IPs are identical, nothing do.");
			return ;
		}
		
		
		final Map<String, String>  parameters = new HashMap<>();
		
		parameters.putAll(resourceIdentifier.parameters());
		
		
		parameters.put(HOST_PARAMETER_NAME, ips.get(HOMEMATIC_HOST));
		
		resourceIdentifier.assign(parameters);
		
		resourceIdentifierRepository.save(resourceIdentifier).block(duration);
		
		
		System.out.println("Update ip to : "+  ips.get(HOMEMATIC_HOST));
	}


	Entry<String,String> toEntry(int address) {
		final String host = IP_PREFIX + address;
		try {
			return new AbstractMap.SimpleImmutableEntry<String,String>(InetAddress.getByName(host).getHostName().split("[.]")[0].toUpperCase(), host);
		} catch (final Exception e) {
			throw new IllegalStateException();
		}
	}


	@Override
	@Commands(commands = {  @Command( name = "updateResources", arguments = {}) })
	public final void updateResources() {
		updateXMLApi();
		
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


	private void updateXMLApi() {
		final String  ip =  homematicIp();
		
		final ResourceIdentifier resourceIdentifier = new ResourceIdentifierImpl(ResourceType.XmlApi, XML_API_URL) ; 
		final Map<String,String> parameters = new HashMap<>();
		parameters.put(HOST_PARAMETER_NAME, ip);
		parameters.put(PORT_PARAMETER_NAME, XMLPORT);
		resourceIdentifier.assign(parameters);
		resourceIdentifierRepository.save(resourceIdentifier);
		System.out.println("Update " + resourceIdentifier.uri() +":"  + parameters);
	}


	private String homematicIp() {
		return IntStream.range(100, 111).mapToObj(address -> toEntry(address)).filter(entry ->  entry.getKey().equals(HOMEMATIC_HOST)).map(Entry::getValue).findAny().orElseThrow(() -> new IllegalStateException("Homematic-Host not found"));
	}
	
		
	
	
	
}
