package de.mq.iot.resource.support;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.AbstractMap;
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
import reactor.core.publisher.Mono;

class ResourceUpdateServiceTest {

	
	private final ResourceIdentifierRepository resourceIdentifierRepository = Mockito.mock(ResourceIdentifierRepository.class);
	
	private final ResourceIdentifier resourceIdentifier = Mockito.mock(ResourceIdentifier.class);
	private final ResourceUpdateService ipUpdateService =  Mockito.mock(ResourceUpdateServiceImpl.class, Mockito.CALLS_REAL_METHODS);
	
	@SuppressWarnings("unchecked")
	private final ArgumentCaptor<Map<String,String>> argumentCaptor = ArgumentCaptor.forClass(Map.class);
	private final Map<String,String> parameter = new HashMap<>();
	@BeforeEach
	void setup() {
		
		final Map<Class<?>, Object> dependencies = new HashMap<>();
		dependencies.put(ResourceIdentifierRepository.class, resourceIdentifierRepository);
		dependencies.put(Duration.class, Duration.ofMillis(500));
		Arrays.asList(ResourceUpdateServiceImpl.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType())).forEach(field -> ReflectionTestUtils.setField(ipUpdateService, field.getName(), dependencies.get(field.getType())));
	
	    final Mono<ResourceIdentifier> mono =  Mono.just(resourceIdentifier);
	  
		
		Mockito.doReturn(mono).when(resourceIdentifierRepository).findById(ResourceType.XmlApi);
		
	
		parameter.put(ResourceUpdateServiceImpl.HOST_PARAMETER_NAME, "1.1.1.1");
		Mockito.doReturn(parameter).when(resourceIdentifier).parameters();
		Mockito.doReturn(mono).when(resourceIdentifierRepository).save(resourceIdentifier);
		
	
		
		
	}
	
	
	@Test
	void toEntry() {
		final int address = 100;
		final Entry<String,String> result =  ((ResourceUpdateServiceImpl)ipUpdateService).toEntry(address);
		assertEquals(ResourceUpdateServiceImpl.IP_PREFIX + address, result.getValue());
		assertNotNull(result.getKey());
		
	}
	
	@Test
	void toEntryBadAddress() {
		assertThrows(IllegalStateException.class, () -> ((ResourceUpdateServiceImpl)ipUpdateService).toEntry(-1));
	}
	
	
	@Test

	void update() {
		Mockito.doReturn(new AbstractMap.SimpleImmutableEntry<String, String>(ResourceUpdateServiceImpl.HOMEMATIC_HOST, ResourceUpdateServiceImpl.IP_PREFIX +100), new AbstractMap.SimpleImmutableEntry<String, String>("192", "192")).when((ResourceUpdateServiceImpl)ipUpdateService).toEntry(Mockito.anyInt());
		
		ipUpdateService.updateIp();
		
		Mockito.verify(resourceIdentifier).assign(argumentCaptor.capture());
		assertEquals(ResourceUpdateServiceImpl.IP_PREFIX +100, argumentCaptor.getValue().get(ResourceUpdateServiceImpl.HOST_PARAMETER_NAME));
		Mockito.verify(resourceIdentifierRepository).save(resourceIdentifier);
	}
	
	
	@Test
	void updateHomematicHostNotFound() {
		
		Mockito.doReturn(new AbstractMap.SimpleImmutableEntry<String, String>("192", "192")).when((ResourceUpdateServiceImpl)ipUpdateService).toEntry(Mockito.anyInt());

		ipUpdateService.updateIp();
		
		Mockito.verify(resourceIdentifierRepository, Mockito.never()).findById(Mockito.any());
		Mockito.verify(resourceIdentifier, Mockito.never()).assign(Mockito.any());
		Mockito.verify(resourceIdentifierRepository, Mockito.never()).save(Mockito.any());
	}
	
	@Test
	void updateIPsIdentical() {
		parameter.put(ResourceUpdateServiceImpl.HOST_PARAMETER_NAME, ResourceUpdateServiceImpl.IP_PREFIX +100);
		Mockito.doReturn(new AbstractMap.SimpleImmutableEntry<String, String>(ResourceUpdateServiceImpl.HOMEMATIC_HOST, ResourceUpdateServiceImpl.IP_PREFIX +100), new AbstractMap.SimpleImmutableEntry<String, String>("192", "192")).when((ResourceUpdateServiceImpl)ipUpdateService).toEntry(Mockito.anyInt());
		
		ipUpdateService.updateIp();
		
		Mockito.verify(resourceIdentifierRepository).findById(Mockito.any());
		Mockito.verify(resourceIdentifier, Mockito.never()).assign(Mockito.any());
		Mockito.verify(resourceIdentifierRepository,Mockito.never()).save(Mockito.any());
	}
	
	@Test
	void dependencies() {
		final int timeout = 500;
		final ResourceUpdateService ipUpdateService = new ResourceUpdateServiceImpl(resourceIdentifierRepository,timeout);
		
		final Map<Class<?>, Object> results = Arrays.asList(ResourceUpdateServiceImpl.class.getDeclaredFields()).stream().filter(field -> ! Modifier.isStatic(field.getModifiers())).map(field -> new SimpleEntry<>(field.getType(), ReflectionTestUtils.getField(ipUpdateService, field.getName()))).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	
		assertEquals(2, results.size());
		assertEquals(resourceIdentifierRepository, results.get(ResourceIdentifierRepository.class));
		
		assertEquals(Duration.ofMillis(timeout),results.get(Duration.class));
		
		
	}
	
	@Test
	void updateResources() {
		final ArgumentCaptor<ResourceIdentifier>  argumentCaptor = ArgumentCaptor.forClass(ResourceIdentifier.class);
		ipUpdateService.updateResources();
		
		Mockito.verify(resourceIdentifierRepository, Mockito.times(2)).save(argumentCaptor.capture());
		
		assertEquals(2,argumentCaptor.getAllValues().size());
		
		assertResourceIdentifierXmlApi(argumentCaptor.getAllValues().get(0));
	}


	private void assertResourceIdentifierXmlApi(final ResourceIdentifier resourceIdentifierXmlApi) {
		assertEquals(ResourceType.XmlApi, resourceIdentifierXmlApi.id());
		assertEquals(ResourceUpdateServiceImpl.XML_API_URL, resourceIdentifierXmlApi.uri());
		
		final Map<String,String> parameter =  resourceIdentifierXmlApi.parameters();
		assertTrue(parameter.get(ResourceUpdateServiceImpl.HOST_PARAMETER_NAME).startsWith(ResourceUpdateServiceImpl.IP_PREFIX));
		assertEquals(ResourceUpdateServiceImpl.XMLPORT, parameter.get(ResourceUpdateServiceImpl.PORT_PARAMETER_NAME));
	}
	
}
