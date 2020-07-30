package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.calendar.DayGroup;
import de.mq.iot.calendar.support.DayService;
import de.mq.iot.rule.support.Calendar.Time;

public class CalendarRuleTest {
	

	private static final LocalDate DATE = LocalDate.of(2019, 3, 29);

	private final DayService specialdayService = Mockito.mock(DayService.class);
	
	private final DefaultRuleInput ruleInput = new DefaultRuleInput(LocalTime.of(5, 15), LocalTime.of(7, 15),LocalTime.of(6, 15),  LocalTime.of(17, 15)); 
	
	private final DayGroup dayGroup = Mockito.mock(DayGroup.class);
	
	private final Calendar calendar = new Calendar();
	
	@BeforeEach
	void setup() {
		Mockito.when(dayGroup.name()).thenReturn(DayGroup.WORKINGDAY_GROUP_NAME);
	}
	
	@Test
	void calculateCalendar() {
		
		
		
		Mockito.when(specialdayService.dayGroup(DATE)).thenReturn(dayGroup);
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> DATE);
		ruleInput.useUpdateMode();
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(DATE, calendar.date());
		assertTrue(calendar.workingDay());
		assertEquals(dayGroup, calendar.dayGroup());
		
		
	}
	
	
	
	@Test
	void timeWinterBeforeSummer() {
		
		Mockito.when(specialdayService.dayGroup(DATE.plusDays(1))).thenReturn(dayGroup);
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> DATE);
		
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(Time.Winter, calendar.time());
	}
	
	@Test
	void timeSummer() {
		
		
		Mockito.when(specialdayService.dayGroup(DATE.plusDays(2))).thenReturn(dayGroup);
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> DATE.plusDays(1));
		
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(Time.Summer, calendar.time());
	}
	
	@Test
	void timeSummerBeforeWinter() {
		
		Mockito.when(specialdayService.dayGroup(LocalDate.of(2019, 10, 26))).thenReturn(dayGroup);
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> LocalDate.of(2019, 10, 25));
		
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(Time.Summer, calendar.time());
		
	}
	
	@Test
	void timeWinter() {
		
		Mockito.when(specialdayService.dayGroup(LocalDate.of(2019, 10, 27))).thenReturn(dayGroup);
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> LocalDate.of(2019, 10, 26));
		
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(Time.Winter, calendar.time());
		
	}
	
	@Test
	void date() {
	
		Mockito.when(specialdayService.dayGroup(DATE.plusDays(1))).thenReturn(dayGroup);
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> DATE);
		
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(DATE.plusDays(1), calendar.date());
		assertEquals(Month.MARCH, calendar.month());
		assertEquals(89, calendar.dayOfYear());
	}
	
	@Test
	void evaluate() {
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> DATE);
		
		assertTrue(calendarRule.evaluate(ruleInput));
		
		Arrays.asList(DefaultRuleInput.class.getDeclaredFields()).forEach(field -> ReflectionTestUtils.setField(ruleInput, field.getName(), null));
		assertFalse(calendarRule.evaluate(ruleInput));
	}

}
