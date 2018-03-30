package de.mq.iot.resource.support;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

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
@Disabled
class ResourceIdentifierIntegrationTest {
	
	
	@Autowired
	private ResourceIdentifierRepository resourceIdentifierRepository;
	
	@Test
	final void save() {
		final ResourceIdentifier resourceIdentifier = new ResourceIdentifierImpl(ResourceType.XmlApi, "http://{host}:{port}/addons/xmlapi/:{resource}") ; 
		final Map<String,String> parameters = new HashMap<>();
		parameters.put("host", "192.168.2.103");
		parameters.put("port", "80");
		resourceIdentifier.assign(parameters);
		//final Mono<ResourceIdentifier> mono = mongoOperations.save(resourceIdentifier);
		//mono.block(Duration.ofMillis(500));
	
		final Duration duration = Duration.ofMillis(500);
		resourceIdentifierRepository.save(resourceIdentifier).block(duration);
		
		assertTrue(resourceIdentifierRepository.findById(ResourceType.XmlApi).blockOptional(duration).isPresent());
		
	}

}


