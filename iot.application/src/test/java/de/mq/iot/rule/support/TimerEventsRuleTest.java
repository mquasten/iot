package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.rule.support.Calendar.Time;
import de.mq.iot.support.SunDownCalculationService;

class TimerEventsRuleTest {
	

	private static final LocalTime SUN_DOWNTIME = LocalTime.of(23 ,59);

	private static final LocalTime SUN_UPTIME = LocalTime.of(5, 40);
	
	

	private static final LocalDate date = LocalDate.of(Year.now().getValue(),Month.JUNE.getValue(), 21);



	private final SunDownCalculationService sunDownCalculationService = Mockito.mock(SunDownCalculationService.class);
	
	private final TimerEventsRule timerEventsRule = new TimerEventsRule(sunDownCalculationService);
	
	
	private  DefaultRuleInput ruleInput = new DefaultRuleInput(LocalTime.of(5, 15) , LocalTime.of(7, 15), LocalTime.of(17, 15));
	
	private final Calendar calendar = new Calendar();

	
	
@Test
	void evaluateValidCalendar() {
		vaidCalendar(calendar);
		assertTrue(timerEventsRule.evaluate(calendar));
	}

	private void vaidCalendar(Calendar calendar) {
		calendar.assignDate(date);
		calendar.assignTime(Time.Summer);
		calendar.assignWorkingDay(true);
	}
	
	@Test
	void evaluateInValidCalendar( ) {
		
		assertFalse(timerEventsRule.evaluate(calendar));
	}
	
	
	
	
	@Test
	void calculateEvents() {
		vaidCalendar(calendar);
		Mockito.when(sunDownCalculationService.sunUpTime(calendar.dayOfYear(), Time.Summer.offset())).thenReturn(SUN_UPTIME);
		Mockito.when(sunDownCalculationService.sunDownTime(calendar.dayOfYear(), Time.Summer.offset())).thenReturn(SUN_DOWNTIME);
		
	
		timerEventsRule.calculateEvents(calendar, ruleInput);
		
		assertTrue(calendar.events().isPresent());
		assertEquals(SystemVariablesRuleImpl.DAILY_EVENTS, calendar.events().get().getKey());
		assertEquals("T0:5.15;T1:5.4;T6:23.59", calendar.events().get().getValue());
	}
	
	@Test
	void calculateEvents2() {
	
	
		vaidCalendar(calendar);
		Mockito.when(sunDownCalculationService.sunUpTime(calendar.dayOfYear(), Time.Summer.offset())).thenReturn(SUN_UPTIME);
		Mockito.when(sunDownCalculationService.sunDownTime(calendar.dayOfYear(), Time.Summer.offset())).thenReturn(SUN_DOWNTIME);
		ruleInput.useUpdateMode();
		
		
		timerEventsRule.calculateEvents(calendar, ruleInput);
		
		
		assertTrue(calendar.events().isPresent());
		assertEquals(SystemVariablesRuleImpl.EVENT_EXECUTIONS, calendar.events().get().getKey());
		assertEquals("T6:23.59", calendar.events().get().getValue());

	}
	
	

	@Test
	void calculateEventsHoliday() {
		vaidCalendar(calendar);
		Mockito.when(sunDownCalculationService.sunUpTime(calendar.dayOfYear(), Time.Summer.offset())).thenReturn(SUN_UPTIME);
		Mockito.when(sunDownCalculationService.sunDownTime(calendar.dayOfYear(), Time.Summer.offset())).thenReturn(SUN_DOWNTIME);
		
		calendar.assignWorkingDay(false);
		
		timerEventsRule.calculateEvents(calendar, ruleInput);
		
		assertTrue(calendar.events().isPresent());
		assertEquals(SystemVariablesRuleImpl.DAILY_EVENTS, calendar.events().get().getKey());
		assertEquals("T0:7.15;T1:7.15;T6:23.59", calendar.events().get().getValue());
	}
	
}
