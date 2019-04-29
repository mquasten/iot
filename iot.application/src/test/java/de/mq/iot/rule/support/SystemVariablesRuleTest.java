package de.mq.iot.rule.support;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import de.mq.iot.rule.support.Calendar.Time;
import de.mq.iot.state.State;
import de.mq.iot.state.StateService;
import de.mq.iot.state.support.ItemList;

class SystemVariablesRuleTest {
	
	private static final String EVENTS = "EVENTS";

	private static final double TEMPERATURE_VALUE = 22.22d;

	private final StateService stateService = Mockito.mock(StateService.class);
	
	private final SystemVariablesRuleImpl systemVariablesRule = new SystemVariablesRuleImpl(stateService);
	
	
	@SuppressWarnings("unchecked")
	private final State<Integer> timeState = Mockito.mock(State.class,Mockito.withSettings().extraInterfaces(ItemList.class));
	
	@SuppressWarnings("unchecked")
	private final State<Integer> monthState = Mockito.mock(State.class,Mockito.withSettings().extraInterfaces(ItemList.class));
	
	
	@SuppressWarnings("unchecked")
	private final State<Boolean> workingdayState = Mockito.mock(State.class);
	
	@SuppressWarnings("unchecked")
	private final State<String> lastupdateState = Mockito.mock(State.class);
	@SuppressWarnings("unchecked")
	private final State<Double> temperatureState = Mockito.mock(State.class);
	
	private final List<State<?>> results = new ArrayList<>();
	
	@BeforeEach
	void setup( ) {
		Mockito.doReturn(Arrays.asList(timeState,monthState,workingdayState,lastupdateState, temperatureState)).when(stateService).states();
		Mockito.doReturn(SystemVariablesRuleImpl.TIME_STATE_NAME).when(timeState).name();
		
		Mockito.doReturn(SystemVariablesRuleImpl.MONTH_STATE_NAME).when(monthState).name();
		Mockito.doReturn(SystemVariablesRuleImpl.WORKINGDAY_STATE_NAME).when(workingdayState).name();
		Mockito.doReturn(SystemVariablesRuleImpl.LAST_BATCHRUN_STATE_NAME).when(lastupdateState).name();
		Mockito.doReturn(SystemVariablesRuleImpl.TEMPERATURE_STATE_NAME).when(temperatureState).name();
	}
	
	@Test
	 void updateSystemVariables() throws ParseException {
		final Calendar calendar = new Calendar();
		calendar.assignTime(Time.Summer);
		final LocalDate now = LocalDate.now();
		calendar.assignDate(now);
		calendar.assignWorkingDay(true);
		
		calendar.assignTemperature(TEMPERATURE_VALUE);
		 
		calendar.assignEvents(EVENTS);
		
		systemVariablesRule.updateSystemVariables(calendar,results);
		 
		
		
		assertEquals(5, results.size());
		
		assertTrue(results.contains(timeState));
		assertTrue(results.contains(monthState));
		assertTrue(results.contains(workingdayState));
		assertTrue(results.contains(lastupdateState));
		assertTrue(results.contains(temperatureState));
		
		
		
		assertEquals(lastupdateState, results.get(results.size()-1));
		 
		Mockito.verify(((ItemList)timeState)).assign(Time.Summer.name());
		
		Mockito.verify(((ItemList)monthState)).assign(now.getMonth().name());
		
		
		Mockito.verify(workingdayState).assign(true);
		
		ArgumentCaptor<String> dateCapture = ArgumentCaptor.forClass(String.class);
		Mockito.verify(lastupdateState).assign(dateCapture.capture());
		
		assertTrue(System.currentTimeMillis() - new SimpleDateFormat(SystemVariablesRuleImpl.LAST_BATCHRUN_DATE_FORMAT).parse(dateCapture.getValue()).getTime() < 1000d);
		

	 }
	
	@Test
	 void updateSystemVariablesNothingChanged() throws ParseException {
		final Calendar calendar = new Calendar();
		calendar.assignTime(Time.Summer);
		final LocalDate now = LocalDate.now();
		calendar.assignDate(now);
		calendar.assignWorkingDay(true);
		calendar.assignTemperature(TEMPERATURE_VALUE); 
		calendar.assignEvents(EVENTS);
		
		Mockito.when(((ItemList) timeState).hasLabel(Time.Summer.name())).thenReturn(true);
		
		Mockito.when(((ItemList) monthState).hasLabel(now.getMonth().name())).thenReturn(true);
		Mockito.doReturn(true).when(workingdayState).hasValue(true);
		Mockito.doReturn(true).when(temperatureState).hasValue(TEMPERATURE_VALUE);
		Mockito.doReturn(true).when(lastupdateState).hasValue(Mockito.anyString());
		
		systemVariablesRule.updateSystemVariables(calendar,results);
		
		assertEquals(0, results.size());
	
	}
	
	@Test
	void evaluate() {
		final Calendar calendar = new Calendar();
		assertFalse(systemVariablesRule.evaluate(calendar));
		
		calendar.assignDate(LocalDate.now());
		calendar.assignTime(Time.Summer);
		calendar.assignWorkingDay(true);
		assertTrue(systemVariablesRule.evaluate(calendar));
	}

}
