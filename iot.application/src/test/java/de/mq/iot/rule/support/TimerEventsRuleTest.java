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
import de.mq.iot.rule.support.TimerEventsBuilder.Key;
import de.mq.iot.support.SunDownCalculationService;

class TimerEventsRuleTest {
	

	private static final LocalTime SUN_DOWNTIME = LocalTime.of(21, 45);

	private static final LocalTime SUN_UPTIME = LocalTime.of(5, 40);

	private static final LocalDate date = LocalDate.of(Year.now().getValue(),Month.JUNE.getValue(), 21);

	private final SunDownCalculationService sunDownCalculationService = Mockito.mock(SunDownCalculationService.class);
	
	private final TimerEventsRule timerEventsRule = new TimerEventsRule(sunDownCalculationService);
	
	
	private final DefaultRuleInput ruleInput = new DefaultRuleInput(LocalTime.of(5, 15), LocalTime.of(7, 15), LocalTime.of(17, 15));
	
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
		assertEquals(String.format("%s:5.15;%s:5.4;%s:21.45", Key.T0, Key.T1, Key.T6), calendar.events().get().getValue());
		assertEquals(TimerEventsBuilder.DAILY_EVENTS_VARIABLE_NAME, calendar.events().get().getKey());
		
	}

	@Test
	void calculateEventsNonWorkingDay() {
		vaidCalendar(calendar);
		calendar.assignWorkingDay(false);
		
		Mockito.when(sunDownCalculationService.sunUpTime(calendar.dayOfYear(), Time.Summer.offset())).thenReturn(SUN_UPTIME);
		Mockito.when(sunDownCalculationService.sunDownTime(calendar.dayOfYear(), Time.Summer.offset())).thenReturn(SUN_DOWNTIME);
		timerEventsRule.calculateEvents(calendar, ruleInput);
		
		assertTrue(calendar.events().isPresent());
		assertEquals(String.format("%s:7.15;%s:7.15;%s:21.45", Key.T0, Key.T1, Key.T6), calendar.events().get().getValue());
		assertEquals(TimerEventsBuilder.DAILY_EVENTS_VARIABLE_NAME, calendar.events().get().getKey());
	}
}
