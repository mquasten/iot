package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService;

public class CalendarRuleTest {
	
	
	private static final LocalDate DATE = LocalDate.of(2019, 3, 29);

	private final SpecialdayService specialdayService = Mockito.mock(SpecialdayService.class);
	
	private final DefaultRuleInput ruleInput = new DefaultRuleInput(LocalTime.of(5, 15), LocalTime.of(7, 15)); 
	
	
	private final Specialday specialday = Mockito.mock(Specialday.class);
	
	private final Calendar calendar = new Calendar();
	
	@BeforeEach
	void setup() {
		Mockito.when(specialday.date(DATE.getYear())).thenReturn(DATE);
	}
	
	@Test
	void calculateCalendarWorkingDay() {
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> DATE);
		ruleInput.useUpdateMode();
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(DATE, calendar.date());
		assertTrue(calendar.workingDay());
		
	}
	
	@Test
	void calculateCalendarWeekend() {
		
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> DATE);
	
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(DATE.plusDays(1), calendar.date());
		assertFalse(calendar.workingDay());
	}
	
	@Test
	void calculateCalendarHoliday() {
		
		Mockito.when(specialdayService.specialdays(Year.of(DATE.getYear()))).thenReturn(Arrays.asList(specialday));
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> DATE);
		ruleInput.useUpdateMode();
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(DATE, calendar.date());
		assertFalse(calendar.workingDay());
		
	}

}
