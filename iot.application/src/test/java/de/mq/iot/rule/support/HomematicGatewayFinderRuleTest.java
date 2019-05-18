package de.mq.iot.rule.support;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import de.mq.iot.state.StateService;

class HomematicGatewayFinderRuleTest {
	
	private static final String ROUTER_PREFIX = "192.168.2.";

	private static final String HOMEMATIC_IP = ROUTER_PREFIX + "105";

	private final StateService stateService = Mockito.mock(StateService.class);
	
	private final ConversionService conversionService = new DefaultConversionService(); 
	
	private final HomematicGatewayFinderRuleImpl rule = new HomematicGatewayFinderRuleImpl(stateService, conversionService,"8.8.8.8");
	
	private final Map<String, String> ruleInputMap = new HashMap<>();
	
	private final Collection<String> results = new ArrayList<>();
	
	@BeforeEach
	void setup() {
		ruleInputMap.put(HomematicGatewayFinderRuleImpl.MAX_IP_COUNT_KEY, "6");
		Mockito.when(stateService.pingAndUpdateIp(HOMEMATIC_IP,false)).thenReturn(true);
		
		
		
	}
	
	@Test
	void evaluate() {
		assertTrue(rule.evaluate());
	}
	@Test
	void update() {
		rule.update(ruleInputMap, results);
		
		IntStream.range(100, 106).forEach(i -> Mockito.verify(stateService).pingAndUpdateIp(ROUTER_PREFIX +i,false));
		
		
		assertEquals(1, results.size());
		assertEquals(String.format(HomematicGatewayFinderRuleImpl.SUCCESS_MESSAGE, HOMEMATIC_IP),results.stream().findAny().get() );
		
	}
	
	@Test
	void updateNotFound() {
		ruleInputMap.put(HomematicGatewayFinderRuleImpl.MAX_IP_COUNT_KEY, "5");
		rule.update(ruleInputMap, results);
		
		IntStream.range(100, 105).forEach(i -> Mockito.verify(stateService).pingAndUpdateIp(ROUTER_PREFIX +i,false));
		
		
		assertEquals(1, results.size());
		assertEquals(String.format(HomematicGatewayFinderRuleImpl.NOT_FOUND_MESSAGE, ROUTER_PREFIX.replaceFirst("[.]$", "")),results.stream().findAny().get() );
		
	}
	
	@Test
	void updateMaxIpsEmpty() {
		ruleInputMap.clear();
		Mockito.when(stateService.pingAndUpdateIp(HOMEMATIC_IP,false)).thenReturn(false);
		
		final String ip1 = ROUTER_PREFIX + "109";
		Mockito.when(stateService.pingAndUpdateIp(ip1,false)).thenReturn(true);
		final String ip = ip1;
		
		rule.update(ruleInputMap, results);
		
		IntStream.range(100, 110).forEach(i -> Mockito.verify(stateService).pingAndUpdateIp(ROUTER_PREFIX +i,false));
		 Mockito.verify(stateService,Mockito.never()).pingAndUpdateIp(ROUTER_PREFIX +"111",false);
	
		assertEquals(1, results.size());
		assertEquals(String.format(HomematicGatewayFinderRuleImpl.SUCCESS_MESSAGE, ip),results.stream().findAny().get() );
		
	}

	

	@Test
	void updateMaxIpsToLess() {
		ruleInputMap.put(HomematicGatewayFinderRuleImpl.MAX_IP_COUNT_KEY, "0");
		Mockito.when(stateService.pingAndUpdateIp(HOMEMATIC_IP,false)).thenReturn(false);
		
		final String ip = ROUTER_PREFIX + "109";
		Mockito.when(stateService.pingAndUpdateIp(ip,false)).thenReturn(true);
		
		
		rule.update(ruleInputMap, results);
		
		IntStream.range(100, 110).forEach(i -> Mockito.verify(stateService).pingAndUpdateIp(ROUTER_PREFIX +i,false));
		 Mockito.verify(stateService,Mockito.never()).pingAndUpdateIp(ROUTER_PREFIX +"111",false);
	
		assertEquals(1, results.size());
		assertEquals(String.format(HomematicGatewayFinderRuleImpl.SUCCESS_MESSAGE, ip),results.stream().findAny().get() );
		
	}
	
	@Test
	void updateMaxIpsToLarge() {
		ruleInputMap.put(HomematicGatewayFinderRuleImpl.MAX_IP_COUNT_KEY, "155");
		Mockito.when(stateService.pingAndUpdateIp(HOMEMATIC_IP,false)).thenReturn(false);
		
		final String ip = ROUTER_PREFIX + "155";
		Mockito.when(stateService.pingAndUpdateIp(ip,false)).thenReturn(true);
		
		
		rule.update(ruleInputMap, results);
		
		IntStream.range(100, 110).forEach(i -> Mockito.verify(stateService).pingAndUpdateIp(ROUTER_PREFIX +i,false));
		 Mockito.verify(stateService,Mockito.never()).pingAndUpdateIp(ROUTER_PREFIX +"111",false);
	
		assertEquals(1, results.size());
		assertEquals(String.format(HomematicGatewayFinderRuleImpl.NOT_FOUND_MESSAGE, ROUTER_PREFIX.replaceFirst("[.]$", "")),results.stream().findAny().get() );
		
	}
	
	@Test
	void updateDnsInvalid() {
		final HomematicGatewayFinderRuleImpl rule = new HomematicGatewayFinderRuleImpl(stateService, conversionService,"x.x.x.x");
		
		
		assertThrows(IllegalStateException.class, () -> rule.update(ruleInputMap, results));
	}

}
