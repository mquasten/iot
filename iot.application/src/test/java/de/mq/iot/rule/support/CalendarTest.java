package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import de.mq.iot.calendar.SpecialdayService.DayType;
import de.mq.iot.rule.support.Calendar.Time;

class CalendarTest {
	
	private final Calendar calendar = new Calendar();
	private final LocalDate now = LocalDate.now();
	
	@Test
	void timeKeys() {
		Arrays.asList(Time.values()).forEach(time -> assertEquals(time.name().toUpperCase(), time.key()));
	}
	
	@Test
	void offsetWinter() {
		assertEquals(1,Time.Winter.offset());
	}
	
	@Test
	void offsetSummer() {
		assertEquals(2,Time.Summer.offset());
	}
	
	@Test
	void date() {
		assertThrows(IllegalArgumentException.class, () -> calendar.date());
		
		calendar.assignDate(now);
		
		assertEquals(now, calendar.date());
		
	}
	
	
	@Test
	void month() {
		assertThrows(IllegalArgumentException.class, () -> calendar.month());
		
		calendar.assignDate(now);
		
		assertEquals(now.getMonth(), calendar.month());
	}
	
	@Test
	void dayOfYear() {
		assertThrows(IllegalArgumentException.class, () -> calendar.dayOfYear());
		
		calendar.assignDate(now);
		
		assertEquals(now.getDayOfYear(), calendar.dayOfYear());
	}

	
	@Test
	void workingDay() {
		assertThrows(IllegalArgumentException.class, () -> calendar.workingDay());
		
		calendar.assignDayType(DayType.WorkingDay);
		assertTrue(calendar.workingDay());
		
		calendar.assignDayType(DayType.SpecialWorkingDay);
		assertTrue(calendar.workingDay());
		
		calendar.assignDayType(DayType.NonWorkingDay);
		assertFalse(calendar.workingDay());
	}
	
	@Test
	void time() {
		assertThrows(IllegalArgumentException.class, () -> calendar.time());
		
		calendar.assignTime(Time.Summer);
		
		assertEquals(Time.Summer, calendar.time());
	}
	
	@Test
	void valid() {
		assertFalse(calendar.valid());
		
		calendar.assignDate(now);
		
		assertFalse(calendar.valid());
		
		calendar.assignDayType(DayType.WorkingDay);
		
		assertFalse(calendar.valid());
		
		calendar.assignTime(Time.Winter);
		
		assertTrue(calendar.valid());
		
		calendar.assignTime(null);
		
		assertFalse(calendar.valid());
		
		calendar.assignDate(null);
		
		assertFalse(calendar.valid());
	}

	@Test
	void temperature() {
		assertFalse(calendar.temperature().isPresent());
		
		calendar.assignTemperature(30.0);
		
		
		assertEquals( calendar.temperature() , calendar.temperature());
	}
	
	@Test
	void assignEvents() {
		calendar.assignEvents(SystemVariablesRuleImpl.DAILY_EVENTS, "T0:14:44");
		
		assertEquals(calendar.events() , calendar.events());
	}

}



