package de.mq.iot.state.support;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mq.iot.support.ApplicationConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })
class StateUpdateSeriviceIntegrationTest {
	
	@Autowired
	private StateUpdateSeriviceImpl stateUpdateSerivice;
	
	@Test
	void update() {
		System.out.println(stateUpdateSerivice);
		
		stateUpdateSerivice.update(LocalDate.of(2018,  5,  1));
	}

}
