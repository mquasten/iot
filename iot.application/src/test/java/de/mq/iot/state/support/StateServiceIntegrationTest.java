package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
	void functions() {
		final Map<String, Set<String>> functions  = stateService.functions().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue)); 
		
		assertEquals(2, functions.size());
		assertTrue(functions.containsKey("Rolladen"));
		assertTrue(functions.containsKey("Licht"));
	
		
		functions.values().forEach(value -> assertEquals(1,value.size()));
		functions.entrySet().stream().filter(entry -> entry.getKey().equals("Rolladen")).map(entry -> entry.getValue().iterator().next()).forEach(value -> assertEquals("LEVEL", value));
	
		functions.entrySet().stream().filter(entry -> entry.getKey().equals("Licht")).map(entry -> entry.getValue().iterator().next()).forEach(value -> assertEquals("STATE", value));
	}
	

}
