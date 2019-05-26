package de.mq.iot.rule.support;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.state.StateService;

class HomematicGatewayFinderRuleTest {
	
	private static final String ROUTER_PREFIX = "192.168.2.";

	private static final String HOMEMATIC_IP = ROUTER_PREFIX + "105";

	private final StateService stateService = Mockito.mock(StateService.class);
	

	
	private final HomematicGatewayFinderRuleImpl rule = new HomematicGatewayFinderRuleImpl(stateService, "8.8.8.8");
	
	private final EndOfDayRuleInput ruleInput = new EndOfDayRuleInput(100,6,30,false);
	
	private final Collection<String> results = new ArrayList<>();
	
	@BeforeEach
	void setup() {
		
		
		Mockito.when(stateService.pingAndUpdateIp(HOMEMATIC_IP,false)).thenReturn(true);
		
		
		
	}
	
	@Test
	void evaluate() {
		assertTrue(rule.evaluate());
	}
	@Test
	void update() {
		rule.update(ruleInput, results);
		
		IntStream.range(100, 106).forEach(i -> Mockito.verify(stateService).pingAndUpdateIp(ROUTER_PREFIX +i,false));
		
		
		assertEquals(1, results.size());
		assertEquals(String.format(HomematicGatewayFinderRuleImpl.SUCCESS_MESSAGE, HOMEMATIC_IP),results.stream().findAny().get() );
		
	}
	
	@Test
	void updateNotFound() {
		
		ReflectionTestUtils.setField(ruleInput, "maxIpCount", 5);
		
		
		rule.update(ruleInput, results);
		
		IntStream.range(100, 105).forEach(i -> Mockito.verify(stateService).pingAndUpdateIp(ROUTER_PREFIX +i,false));
		
		
		assertEquals(1, results.size());
		assertEquals(String.format(HomematicGatewayFinderRuleImpl.NOT_FOUND_MESSAGE, ROUTER_PREFIX.replaceFirst("[.]$", "")),results.stream().findAny().get() );
		
	}
	


	

	
	
	
	@Test
	void updateDnsInvalid() {
		final HomematicGatewayFinderRuleImpl rule = new HomematicGatewayFinderRuleImpl(stateService,"x.x.x.x");
		
		
		assertThrows(IllegalStateException.class, () -> rule.update(ruleInput, results));
	}

}
