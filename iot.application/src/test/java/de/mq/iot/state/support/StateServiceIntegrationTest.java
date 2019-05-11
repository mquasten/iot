package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mq.iot.state.State;
import de.mq.iot.state.StateService;
import de.mq.iot.support.ApplicationConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })

class StateServiceIntegrationTest {
	
	private static final String WORKINGDAY_STATE = "Workingday";
	@Autowired
	private StateService stateService;

	@Test
	@Disabled
	void states() {
		final Collection<State<?>> states = stateService.states();
		
		final Optional<State<?>> workingDayState = states.stream().filter(state -> state.name().equalsIgnoreCase(WORKINGDAY_STATE)).findAny();
		assertTrue(workingDayState.isPresent());
		assertTrue(workingDayState.get().id() > 0);
		assertEquals(WORKINGDAY_STATE, workingDayState.get().name());
		assertTrue(workingDayState.get() instanceof BooleanStateImpl);
	}
	
	
	@Test
	@Disabled
	void ping() {
		assertTrue(stateService.pingAndUpdateIp("192.168.2.104"));
	}
	

}
