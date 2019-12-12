package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.calendar.SpecialdayService.DayType;
import de.mq.iot.rule.support.Calendar.Time;

public class CalendarRuleTest {
	
	
	private static final LocalDate DATE = LocalDate.of(2019, 3, 29);

	private final SpecialdayService specialdayService = Mockito.mock(SpecialdayService.class);
	
	private final DefaultRuleInput ruleInput = new DefaultRuleInput(LocalTime.of(5, 15), LocalTime.of(7, 15),LocalTime.of(6, 15),  LocalTime.of(17, 15)); 
	
	
	private final Specialday specialday = Mockito.mock(Specialday.class);
	
	private final Calendar calendar = new Calendar();
	
	@BeforeEach
	void setup() {
		Mockito.when(specialday.date(DATE.getYear())).thenReturn(DATE);
	}
	
	@Test
	void calculateCalendarWorkingDay() {
		Mockito.when(specialdayService.typeOfDay(DATE)).thenReturn(new SimpleImmutableEntry<>(DayType.WorkingDay, null));
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> DATE);
		ruleInput.useUpdateMode();
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(DATE, calendar.date());
		assertTrue(calendar.workingDay());
		assertEquals(DayType.WorkingDay, calendar.dayType());
		
	}
	
	@Test
	void calculateCalendarWeekend() {
		Mockito.when(specialdayService.typeOfDay(DATE.plusDays(1))).thenReturn(new SimpleImmutableEntry<>(DayType.NonWorkingDay, null));
		
		
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> DATE);
	
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(DATE.plusDays(1), calendar.date());
		assertFalse(calendar.workingDay());
		assertEquals(DayType.NonWorkingDay, calendar.dayType());
	}
	
	@Test
	void calculateCalendarHoliday() {
		
		//Mockito.when(specialdayService.specialdays(Year.of(DATE.getYear()))).thenReturn(Arrays.asList(specialday));
		Mockito.when(specialdayService.typeOfDay(DATE)).thenReturn(new SimpleImmutableEntry<>(DayType.NonWorkingDay, null));
		
		
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> DATE);
		ruleInput.useUpdateMode();
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(DATE, calendar.date());
		assertFalse(calendar.workingDay());
		assertEquals(DayType.NonWorkingDay, calendar.dayType());
		
	}
	
	@Test
	void timeWinterBeforeSummer() {
		
		Mockito.when(specialdayService.typeOfDay(DATE.plusDays(1))).thenReturn(new SimpleImmutableEntry<>(DayType.WorkingDay, null));
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> DATE);
		
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(Time.Winter, calendar.time());
	}
	
	@Test
	void timeSummer() {
		
		
		Mockito.when(specialdayService.typeOfDay(DATE.plusDays(2))).thenReturn(new SimpleImmutableEntry<>(DayType.WorkingDay, null));
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> DATE.plusDays(1));
		
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(Time.Summer, calendar.time());
	}
	
	@Test
	void timeSummerBeforeWinter() {
		Mockito.when(specialdayService.typeOfDay(LocalDate.of(2019, 10, 26))).thenReturn(new SimpleImmutableEntry<>(DayType.WorkingDay, null));
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> LocalDate.of(2019, 10, 25));
		
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(Time.Summer, calendar.time());
		
	}
	
	@Test
	void timeWinter() {
		Mockito.when(specialdayService.typeOfDay(LocalDate.of(2019, 10, 27))).thenReturn(new SimpleImmutableEntry<>(DayType.WorkingDay, null));
		final  CalendarRuleImpl calendarRule = new CalendarRuleImpl(specialdayService, () -> LocalDate.of(2019, 10, 26));
		
		calendarRule.calculateCalendar(ruleInput, calendar);
		
		assertEquals(Time.Winter, calendar.time());
		
	}
	
	@Test
	void date() {
		Mockito.when(specialdayService.typeOfDay(DATE.plusDays(1))).thenReturn(new SimpleImmutableEntry<>(DayType.WorkingDay, null));
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
