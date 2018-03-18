package de.mq.iot.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.mq.iot.resource.ResourceIdentifier.ResourceType;

class ResourceTypeTest {
	
	@Test
	void values () {
		assertEquals(Integer.valueOf(1), Integer.valueOf(ResourceType.values().length));
	}

}
