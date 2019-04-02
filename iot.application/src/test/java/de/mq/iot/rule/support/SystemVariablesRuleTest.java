package de.mq.iot.rule.support;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.rule.support.Calendar.Time;
import de.mq.iot.rule.support.SystemVariablesRuleImpl;
import de.mq.iot.state.State;
import de.mq.iot.state.StateService;
import de.mq.iot.state.support.ItemList;

class SystemVariablesRuleTest {
	
	private final StateService stateService = Mockito.mock(StateService.class);
	
	private final SystemVariablesRuleImpl systemVariablesRule = new SystemVariablesRuleImpl(stateService);
	
	
	@SuppressWarnings("unchecked")
	private final State<Integer> timeState = Mockito.mock(State.class,Mockito.withSettings().extraInterfaces(ItemList.class));
	
	@SuppressWarnings("unchecked")
	private final State<Integer> monthState = Mockito.mock(State.class,Mockito.withSettings().extraInterfaces(ItemList.class));
	
	private final List<State<?>> results = new ArrayList<>();
	
	@BeforeEach
	void setup( ) {
		Mockito.doReturn(Arrays.asList(timeState,monthState)).when(stateService).states();
		Mockito.doReturn(SystemVariablesRuleImpl.TIME_STATE_NAME).when(timeState).name();
		
		Mockito.doReturn(SystemVariablesRuleImpl.MONTH_STATE_NAME).when(monthState).name();
		
	}
	
	@Test
	 void updateSystemVariables() {
		final Calendar calendar = new Calendar();
		calendar.assignTime(Time.Summer);
		final LocalDate now = LocalDate.now();
		calendar.assignDate(now);
		 
		systemVariablesRule.updateSystemVariables(calendar,results);
		 
		assertEquals(2, results.size());
		
		assertTrue(results.contains(timeState));
		 
		Mockito.verify(((ItemList)timeState)).assign(Time.Summer.name());
		
		Mockito.verify(((ItemList)monthState)).assign(now.getMonth().name());
	 }

}
