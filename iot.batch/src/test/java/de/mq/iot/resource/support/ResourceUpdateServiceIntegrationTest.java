package de.mq.iot.resource.support;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mq.iot.support.ApplicationConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })
@Disabled
class ResourceUpdateServiceIntegrationTest {
	
	@Autowired
	private ResourceUpdateService resourceUpdateService;
	
	
	@Test
	@Disabled
	void updateIP() {
		
		
		resourceUpdateService.updateIp();
	}
	
	@Test
	@Disabled
	void updateResources() {
		
		
		resourceUpdateService.updateResources();
	}


}
