package de.mq.iot.state.support;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import de.mq.iot.resource.support.ResourceIdentifierRepository;
import de.mq.iot.state.IPUpdateService;
import reactor.core.publisher.Mono;

class IPUpdateServiceTest {

	
	private final ResourceIdentifierRepository resourceIdentifierRepository = Mockito.mock(ResourceIdentifierRepository.class);
	
	private final ResourceIdentifier resourceIdentifier = Mockito.mock(ResourceIdentifier.class);
	private final IPUpdateService ipUpdateService =  Mockito.mock(IPUpdateServiceImpl.class, Mockito.CALLS_REAL_METHODS);
	
	@SuppressWarnings("unchecked")
	private final ArgumentCaptor<Map<String,String>> argumentCaptor = ArgumentCaptor.forClass(Map.class);
	private final Map<String,String> parameter = new HashMap<>();
	@BeforeEach
	void setup() {
		
		final Map<Class<?>, Object> dependencies = new HashMap<>();
		dependencies.put(ResourceIdentifierRepository.class, resourceIdentifierRepository);
		Arrays.asList(IPUpdateServiceImpl.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType())).forEach(field -> ReflectionTestUtils.setField(ipUpdateService, field.getName(), dependencies.get(field.getType())));
	
	    final Mono<ResourceIdentifier> mono =  Mono.just(resourceIdentifier);
	  
		
		Mockito.doReturn(mono).when(resourceIdentifierRepository).findById(ResourceType.XmlApi);
		
	
		parameter.put(IPUpdateServiceImpl.HOST_PARAMETER_NAME, "1.1.1.1");
		Mockito.doReturn(parameter).when(resourceIdentifier).parameters();
		Mockito.doReturn(mono).when(resourceIdentifierRepository).save(resourceIdentifier);
		
	
		
		
	}
	
	
	@Test
	void toEntry() {
		final int address = 100;
		final Entry<String,String> result =  ((IPUpdateServiceImpl)ipUpdateService).toEntry(address);
		assertEquals(IPUpdateServiceImpl.IP_PREFIX + address, result.getValue());
		assertNotNull(result.getKey());
		
	}
	
	@Test
	void toEntryBadAddress() {
		assertThrows(IllegalStateException.class, () -> ((IPUpdateServiceImpl)ipUpdateService).toEntry(-1));
	}
	
	
	@Test

	void update() {
		Mockito.doReturn(new AbstractMap.SimpleImmutableEntry<String, String>(IPUpdateServiceImpl.HOMEMATIC_HOST, IPUpdateServiceImpl.IP_PREFIX +100), new AbstractMap.SimpleImmutableEntry<String, String>("192", "192")).when((IPUpdateServiceImpl)ipUpdateService).toEntry(Mockito.anyInt());
		
		ipUpdateService.update();
		
		Mockito.verify(resourceIdentifier).assign(argumentCaptor.capture());
		assertEquals(IPUpdateServiceImpl.IP_PREFIX +100, argumentCaptor.getValue().get(IPUpdateServiceImpl.HOST_PARAMETER_NAME));
		Mockito.verify(resourceIdentifierRepository).save(resourceIdentifier);
	}
	
	
	@Test
	void updateHomematicHostNotFound() {
		
		Mockito.doReturn(new AbstractMap.SimpleImmutableEntry<String, String>("192", "192")).when((IPUpdateServiceImpl)ipUpdateService).toEntry(Mockito.anyInt());

		ipUpdateService.update();
		
		Mockito.verify(resourceIdentifierRepository, Mockito.never()).findById(Mockito.any());
		Mockito.verify(resourceIdentifier, Mockito.never()).assign(Mockito.any());
		Mockito.verify(resourceIdentifierRepository, Mockito.never()).save(Mockito.any());
	}
	
	@Test
	void updateIPsIdentical() {
		parameter.put(IPUpdateServiceImpl.HOST_PARAMETER_NAME, IPUpdateServiceImpl.IP_PREFIX +100);
		Mockito.doReturn(new AbstractMap.SimpleImmutableEntry<String, String>(IPUpdateServiceImpl.HOMEMATIC_HOST, IPUpdateServiceImpl.IP_PREFIX +100), new AbstractMap.SimpleImmutableEntry<String, String>("192", "192")).when((IPUpdateServiceImpl)ipUpdateService).toEntry(Mockito.anyInt());
		
		ipUpdateService.update();
		
		Mockito.verify(resourceIdentifierRepository).findById(Mockito.any());
		Mockito.verify(resourceIdentifier, Mockito.never()).assign(Mockito.any());
		Mockito.verify(resourceIdentifierRepository,Mockito.never()).save(Mockito.any());
	}
	
	@Test
	void dependencies() {
		final IPUpdateService ipUpdateService = new IPUpdateServiceImpl(resourceIdentifierRepository);
		
		final List<Object> results = Arrays.asList(IPUpdateServiceImpl.class.getDeclaredFields()).stream().filter(field -> ! Modifier.isStatic(field.getModifiers())).map(field -> ReflectionTestUtils.getField(ipUpdateService, field.getName())).collect(Collectors.toList());
	
		assertEquals(1, results.size());
		assertEquals(resourceIdentifierRepository, results.stream().findAny().get());
	}
}
