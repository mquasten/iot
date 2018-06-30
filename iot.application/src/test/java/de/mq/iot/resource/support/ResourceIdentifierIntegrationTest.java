package de.mq.iot.resource.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.resource.ResourceIdentifier.ResourceType;
import de.mq.iot.support.ApplicationConfiguration;



@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })

class ResourceIdentifierIntegrationTest {
	
	
	@Autowired
	private ResourceIdentifierRepository resourceIdentifierRepository;
	
	@Test
	@Disabled
	final void saveXmlApi() {
		final ResourceIdentifier resourceIdentifier = new ResourceIdentifierImpl(ResourceType.XmlApi, "http://{host}:{port}/addons/xmlapi/{resource}") ; 
		final Map<String,String> parameters = new HashMap<>();
		parameters.put("host", "192.168.2.102");
		parameters.put("port", "80");
		resourceIdentifier.assign(parameters);
		//final Mono<ResourceIdentifier> mono = mongoOperations.save(resourceIdentifier);
		//mono.block(Duration.ofMillis(500));
	
		final Duration duration = Duration.ofMillis(500);
		resourceIdentifierRepository.save(resourceIdentifier).block(duration);
		
		assertTrue(resourceIdentifierRepository.findById(ResourceType.XmlApi).blockOptional(duration).isPresent());
		
	}
	
	
	@Test
	@Disabled
	final void saveOpenWeather() {
		final ResourceIdentifier resourceIdentifier = new ResourceIdentifierImpl(ResourceType.OpenWeather, "http://api.openweathermap.org/data/{version}/{resource}?q={city},{country}&appid={key}&units=metric") ; 
		final Map<String,String> parameters = new HashMap<>();
		parameters.put("version", "2.5");
		parameters.put("city", "Wegberg");
		parameters.put("country", "de");
		parameters.put("key", "607cd43d4d9b17d8a96df387fe4ede62");
		resourceIdentifier.assign(parameters);
		final Duration duration = Duration.ofMillis(500);
		resourceIdentifierRepository.save(resourceIdentifier).block(duration);
		
		final Optional<ResourceIdentifier>  result = resourceIdentifierRepository.findById(ResourceType.XmlApi).blockOptional(duration);
		assertTrue(result.isPresent());
		assertEquals(parameters, result.get().parameters());
		
		
	}

}


