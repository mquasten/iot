package de.mq.iot.resource.support;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mongodb.MongoClient;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.resource.ResourceIdentifier.ResourceType;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class })
@Disabled
class ResourceIdentifierIntegrationTest {
	
	@Autowired
	private MongoOperations mongoOperations; 
	
	@Test
	final void save() {
		final ResourceIdentifier resourceIdentifier = new ResourceIdentifierImpl(ResourceType.XmlApiSysVarlist, "http://{host}:{port}/addons/xmlapi/sysvarlist.cgi") ; 
		final Map<String,String> parameters = new HashMap<>();
		parameters.put("host", "192.168.2.103");
		parameters.put("port", "80");
		resourceIdentifier.assign(parameters);
		mongoOperations.save(resourceIdentifier);
	}

}


class TestConfiguration {
	
	@Bean
	MongoOperations mongoTemplate() {
		return new MongoTemplate(new SimpleMongoDbFactory(new MongoClient("127.0.0.1", 27017), "iot"));
	}
	
}
