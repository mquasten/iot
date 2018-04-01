package de.mq.iot.resource.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.resource.ResourceIdentifier.ResourceType;

class ResourceIdentifierTest {
	
	private static final String URI = "http://{host}:{port}/addons/xmlapi/{resource}";
	private final ResourceIdentifier resourceIdentifier = new ResourceIdentifierImpl(ResourceType.XmlApi, URI); 
	
	@Test
	void uri() {
		assertEquals(URI, resourceIdentifier.uri());
	}
	@Test
	 void id() {
		 assertEquals(ResourceType.XmlApi, resourceIdentifier.id());
	 }
	
	@Test
	 void parameters() {
		assertTrue(resourceIdentifier.parameters().isEmpty());
		
		final Map<String,String> parameters = new HashMap<>();
		parameters.put("host", "kylie.com");
		
		resourceIdentifier.assign(parameters);
		assertEquals(parameters, resourceIdentifier.parameters());
	}
	
	@Test
	void createDefaultConstructor() {
		final ResourceIdentifier resourceIdentifier =  BeanUtils.instantiateClass(ResourceIdentifierImpl.class);
		assertNull(resourceIdentifier.id());
		assertNull(resourceIdentifier.uri());
		assertTrue(resourceIdentifier.parameters().isEmpty());
	}
}
