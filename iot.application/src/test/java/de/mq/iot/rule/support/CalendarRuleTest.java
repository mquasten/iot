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

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.calendar.SpecialdayService.DayType;
import de.mq.iot.calendar.support.SpecialdaysRulesEngineResult;
import de.mq.iot.rule.support.Calendar.Time;

public class CalendarRuleTest {
	
	
	private static final String DESCRIPTION = "description";

	private static final LocalDate DATE = LocalDate.of(2019, 3, 29);

	private final SpecialdayService specialdayService = Mockito.mock(SpecialdayService.class);
	
	private final DefaultRuleInput ruleInput = new DefaultRuleInput(LocalTime.of(5, 15), LocalTime.of(7, 15),LocalTime.of(6, 15),  LocalTime.of(17, 15)); 
	
	
	private final Specialday specialday = Mockito.mock(Specialday.class);
	
	private SpecialdaysRulesEngineResult specialdaysRulesEngineResult = Mockito.mock(SpecialdaysRulesEngineResult.class);
	
	private final Calendar calendar = new Calendar();
	
	@BeforeEach
	void setup() {
		Mockito.doReturn(DESCRIPTION).when(specialdaysRulesEngineResult).description();
		Mockito.when(specialday.date(DATE.getYear())).thenReturn(DATE);
	}
	
	@Test
	void calculateCalendarWorkingDay() {
		Mockito.doReturn(DayType.WorkingDay).when(specialdaysRulesEngineResult).dayType();
		Mockito.when(specialdayService.specialdaysRulesEngineResult(DATE)).thenReturn(specialdaysRulesEngineResult);
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> DATE);
		ruleInput.useUpdateMode();
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(DATE, calendar.date());
		assertTrue(calendar.workingDay());
		assertEquals(DayType.WorkingDay, calendar.dayType());
		
	}
	
	@Test
	void calculateCalendarWeekend() {
		Mockito.doReturn(DayType.NonWorkingDay).when(specialdaysRulesEngineResult).dayType();
		Mockito.when(specialdayService.specialdaysRulesEngineResult(DATE.plusDays(1))).thenReturn(specialdaysRulesEngineResult);
		
		
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> DATE);
	
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(DATE.plusDays(1), calendar.date());
		assertFalse(calendar.workingDay());
		assertEquals(DayType.NonWorkingDay, calendar.dayType());
	}
	
	@Test
	void calculateCalendarHoliday() {
		
		Mockito.doReturn(DayType.NonWorkingDay).when(specialdaysRulesEngineResult).dayType();
		Mockito.when(specialdayService.specialdaysRulesEngineResult(DATE)).thenReturn(specialdaysRulesEngineResult);
		
		
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> DATE);
		ruleInput.useUpdateMode();
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(DATE, calendar.date());
		assertFalse(calendar.workingDay());
		assertEquals(DayType.NonWorkingDay, calendar.dayType());
		
	}
	
	@Test
	void timeWinterBeforeSummer() {
		Mockito.doReturn(DayType.WorkingDay).when(specialdaysRulesEngineResult).dayType();
		Mockito.when(specialdayService.specialdaysRulesEngineResult(DATE.plusDays(1))).thenReturn(specialdaysRulesEngineResult);
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> DATE);
		
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(Time.Winter, calendar.time());
	}
	
	@Test
	void timeSummer() {
		
		Mockito.doReturn(DayType.WorkingDay).when(specialdaysRulesEngineResult).dayType();
		Mockito.when(specialdayService.specialdaysRulesEngineResult(DATE.plusDays(2))).thenReturn(specialdaysRulesEngineResult);
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> DATE.plusDays(1));
		
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(Time.Summer, calendar.time());
	}
	
	@Test
	void timeSummerBeforeWinter() {
		Mockito.doReturn(DayType.WorkingDay).when(specialdaysRulesEngineResult).dayType();
		Mockito.when(specialdayService.specialdaysRulesEngineResult(LocalDate.of(2019, 10, 26))).thenReturn(specialdaysRulesEngineResult);
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> LocalDate.of(2019, 10, 25));
		
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(Time.Summer, calendar.time());
		
	}
	
	@Test
	void timeWinter() {
		Mockito.doReturn(DayType.WorkingDay).when(specialdaysRulesEngineResult).dayType();
		Mockito.when(specialdayService.specialdaysRulesEngineResult(LocalDate.of(2019, 10, 27))).thenReturn(specialdaysRulesEngineResult);
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> LocalDate.of(2019, 10, 26));
		
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(Time.Winter, calendar.time());
		
	}
	
	@Test
	void date() {
		Mockito.doReturn(DayType.WorkingDay).when(specialdaysRulesEngineResult).dayType();
		Mockito.when(specialdayService.specialdaysRulesEngineResult(DATE.plusDays(1))).thenReturn(specialdaysRulesEngineResult);
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
