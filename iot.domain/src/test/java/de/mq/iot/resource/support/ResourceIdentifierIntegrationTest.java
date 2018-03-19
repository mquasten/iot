package de.mq.iot.resource.support;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mongodb.reactivestreams.client.MongoClients;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.resource.ResourceIdentifier.ResourceType;
import reactor.core.publisher.Mono;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class })
@Disabled
class ResourceIdentifierIntegrationTest {
	
	//@Autowired
	//private MongoOperations mongoOperations; 
	@Autowired
	private ReactiveMongoOperations mongoOperations; 
	

	
	@Test
	final void save() {
		final ResourceIdentifier resourceIdentifier = new ResourceIdentifierImpl(ResourceType.XmlApiSysVarlist, "http://{host}:{port}/addons/xmlapi/sysvarlist.cgi") ; 
		final Map<String,String> parameters = new HashMap<>();
		parameters.put("host", "192.168.2.103");
		parameters.put("port", "80");
		resourceIdentifier.assign(parameters);
		final Mono<ResourceIdentifier> mono = mongoOperations.save(resourceIdentifier);
		mono.block(Duration.ofMillis(500));
	
		
	
		
	}

}


class TestConfiguration {
	
	/*@Bean
	MongoOperations mongoTemplate() {
		return new MongoTemplate(new SimpleMongoDbFactory(new MongoClient("127.0.0.1", 27017), "iot"));
	} */
	
	
	@Bean
	ReactiveMongoOperations mongoTemplate() {
		 return new ReactiveMongoTemplate( MongoClients.create(String.format("mongodb://localhost:%d", 27017)), "iot");
	}
	
	
}
