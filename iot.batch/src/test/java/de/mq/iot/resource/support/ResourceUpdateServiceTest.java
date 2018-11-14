package de.mq.iot.resource.support;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.resource.ResourceIdentifier.ResourceType;
import de.mq.iot.state.support.StateRepository;
import reactor.core.publisher.Mono;

class ResourceUpdateServiceTest {

	
	private final ResourceIdentifierRepository resourceIdentifierRepository = Mockito.mock(ResourceIdentifierRepository.class);
	
	private final ResourceIdentifier resourceIdentifier = Mockito.mock(ResourceIdentifier.class);
	private final ResourceUpdateService ipUpdateService =  Mockito.mock(ResourceUpdateServiceImpl.class, Mockito.CALLS_REAL_METHODS);
	
	
	private StateRepository stateRepository = Mockito.mock(StateRepository.class);
	
	private static String XMLPORT = "80";
	
	private final Map<String,String> parameter = new HashMap<>();
	@BeforeEach
	void setup() {
		
		final Map<Class<?>, Object> dependencies = new HashMap<>();
		dependencies.put(ResourceIdentifierRepository.class, resourceIdentifierRepository);
		dependencies.put(StateRepository.class, stateRepository);
		dependencies.put(Duration.class, Duration.ofMillis(500));
		Arrays.asList(ResourceUpdateServiceImpl.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType())).forEach(field -> ReflectionTestUtils.setField(ipUpdateService, field.getName(), dependencies.get(field.getType())));
	
	    final Mono<ResourceIdentifier> mono =  Mono.just(resourceIdentifier);
	  
		
		Mockito.doReturn(mono).when(resourceIdentifierRepository).findById(ResourceType.XmlApi);
		
	
		parameter.put(ResourceUpdateServiceImpl.HOST_PARAMETER_NAME, "1.1.1.1");
		parameter.put(ResourceUpdateServiceImpl.PORT_PARAMETER_NAME, XMLPORT);
		Mockito.doReturn(parameter).when(resourceIdentifier).parameters();
		Mockito.doReturn(mono).when(resourceIdentifierRepository).save(Mockito.any(ResourceIdentifier.class));
		
	
		Mockito.when(resourceIdentifier.uri()).thenReturn(ResourceUpdateServiceImpl.XML_API_URL);
		Mockito.when(resourceIdentifier.id()).thenReturn(ResourceType.XmlApi);
		
		Mockito.doAnswer(answer -> {
			final String[] cols =  ((ResourceIdentifier)answer.getArgument(0)).parameters().get(ResourceUpdateServiceImpl.HOST_PARAMETER_NAME).split("[.]");
			if(  Integer.valueOf(cols[cols.length-1]) != ResourceUpdateServiceImpl.MAX_SUB_COUNT-1 ) {
			   throw new IllegalStateException();	
			}
				
				
			
			return 1.1d;
		}).when(stateRepository).findVersion(Mockito.any());
		
	}
	
	@Test
	void dependencies() {
		final int timeout = 500;
		final ResourceUpdateService ipUpdateService = new ResourceUpdateServiceImpl(resourceIdentifierRepository, stateRepository, timeout);
		
		final Map<Class<?>, Object> results = Arrays.asList(ResourceUpdateServiceImpl.class.getDeclaredFields()).stream().filter(field -> ! Modifier.isStatic(field.getModifiers())).map(field -> new SimpleEntry<>(field.getType(), ReflectionTestUtils.getField(ipUpdateService, field.getName()))).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	
		assertEquals(3, results.size());
		assertEquals(resourceIdentifierRepository, results.get(ResourceIdentifierRepository.class));
		assertEquals(stateRepository, results.get(StateRepository.class));
		assertEquals(Duration.ofMillis(timeout),results.get(Duration.class));
		
		
	}
	
	@Test
	void updateResources() {
		final ArgumentCaptor<ResourceIdentifier>  argumentCaptor = ArgumentCaptor.forClass(ResourceIdentifier.class);
		ipUpdateService.updateResources();
		
		
		Mockito.verify(resourceIdentifierRepository, Mockito.times(2)).save(argumentCaptor.capture());
		
		assertEquals(2,argumentCaptor.getAllValues().size());
		
		assertResourceIdentifierXmlApi(argumentCaptor.getAllValues().get(0));
		
		ResourceIdentifier resourceIdentifier = argumentCaptor.getAllValues().get(1);
		assertEquals(ResourceType.OpenWeather, resourceIdentifier.id());
		assertEquals(ResourceUpdateServiceImpl.OPEN_WEATHER_URL, resourceIdentifier.uri());
		
		final Map<String,String> parameter =  resourceIdentifier.parameters();
		
		assertEquals(4, parameter.size());
		
		
		
		assertEquals(ResourceUpdateServiceImpl.OPEN_WEATHER_VERSION_VALUE, parameter.get(ResourceUpdateServiceImpl.OPEN_WEATHER_VERSION_PARAM));
		assertEquals(ResourceUpdateServiceImpl.OPEN_WEATHER_CITY_VALUE, parameter.get(ResourceUpdateServiceImpl.OPEN_WEATHER_CITY_PARAM));
		assertEquals(ResourceUpdateServiceImpl.OPEN_WEATHER_COUNTRY_VALUE, parameter.get(ResourceUpdateServiceImpl.OPEN_WEATHER_COUNTRY_PARAM));
		assertEquals(ResourceUpdateServiceImpl.OPEN_WEATHER_KEY_VALUE, parameter.get(ResourceUpdateServiceImpl.OPEN_WEATHER_KEY_PARAM));
		
	}


	private void assertResourceIdentifierXmlApi(final ResourceIdentifier resourceIdentifierXmlApi) {
		assertEquals(ResourceType.XmlApi, resourceIdentifierXmlApi.id());
		assertEquals(ResourceUpdateServiceImpl.XML_API_URL, resourceIdentifierXmlApi.uri());
		
		final Map<String,String> parameter =  resourceIdentifierXmlApi.parameters();
		
		assertEquals(parameter.get(ResourceUpdateServiceImpl.HOST_PARAMETER_NAME), ResourceUpdateServiceImpl.IP_PREFIX + (ResourceUpdateServiceImpl.MAX_SUB_COUNT-1));
		assertEquals(XMLPORT, parameter.get(ResourceUpdateServiceImpl.PORT_PARAMETER_NAME));
	}
	
	@Test
	public final void updateIp() {
		final ArgumentCaptor<ResourceIdentifier>  argumentCaptor = ArgumentCaptor.forClass(ResourceIdentifier.class);
		ipUpdateService.updateIp();
		
		Mockito.verify(resourceIdentifierRepository).save(argumentCaptor.capture());
		
		assertResourceIdentifierXmlApi(argumentCaptor.getValue());
		
	}
	
	@Test
	public final void updateIpHomematicNotfound() {
		
		Mockito.doAnswer(answer -> {
			   throw new IllegalStateException();	
		}).when(stateRepository).findVersion(Mockito.any());//final ArgumentCaptor<ResourceIdentifier>  argumentCaptor = ArgumentCaptor.forClass(ResourceIdentifier.class);
		ipUpdateService.updateIp();
		
	
		Mockito.verify(resourceIdentifierRepository).findById(ResourceType.XmlApi);
		Mockito.verify(resourceIdentifierRepository, Mockito.never()).save(Mockito.any(ResourceIdentifier.class));
		
	}
	
	@Test
	public final void updateWrongVersion() {
		Mockito.doAnswer(answer -> {
			final String[] cols =  ((ResourceIdentifier)answer.getArgument(0)).parameters().get(ResourceUpdateServiceImpl.HOST_PARAMETER_NAME).split("[.]");
			if(  Integer.valueOf(cols[cols.length-1]) != ResourceUpdateServiceImpl.MAX_SUB_COUNT-1 ) {
			   throw new IllegalStateException();	
			}
				
				
			
			return 0.9d;
		}).when(stateRepository).findVersion(Mockito.any());
		
		
		ipUpdateService.updateIp();
		
		
		Mockito.verify(resourceIdentifierRepository).findById(ResourceType.XmlApi);
		Mockito.verify(resourceIdentifierRepository, Mockito.never()).save(Mockito.any(ResourceIdentifier.class));
		
	}
	
	@Test
	public final void updateIpNotChanged() {
		
		parameter.put(ResourceUpdateServiceImpl.HOST_PARAMETER_NAME,  ResourceUpdateServiceImpl.IP_PREFIX + (ResourceUpdateServiceImpl.MAX_SUB_COUNT-1));
		Mockito.doReturn(parameter).when(resourceIdentifier).parameters();
		
		
		ipUpdateService.updateIp();
		
		Mockito.verify(resourceIdentifierRepository).findById(ResourceType.XmlApi);
		Mockito.verify(resourceIdentifierRepository, Mockito.never()).save(Mockito.any(ResourceIdentifier.class));
	
	
		
	}
	
}
