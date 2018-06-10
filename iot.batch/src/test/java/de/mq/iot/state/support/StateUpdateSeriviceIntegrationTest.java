package de.mq.iot.state.support;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mq.iot.state.StateUpdateService;
import de.mq.iot.support.ApplicationConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })
class StateUpdateSeriviceIntegrationTest {
	
	@Autowired
	private StateUpdateService stateUpdateSerivice;
	
	@Test
	void update() {
	
		
		stateUpdateSerivice.update(0);
	}

}
