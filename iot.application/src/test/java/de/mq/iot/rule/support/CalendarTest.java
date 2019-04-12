package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import de.mq.iot.rule.support.Calendar.Time;

class CalendarTest {
	
	private final Calendar calendar = new Calendar();
	private final LocalDate now = LocalDate.now();
	
	@Test
	void timeKeys() {
		Arrays.asList(Time.values()).forEach(time -> assertEquals(time.name().toUpperCase(), time.key()));
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
		
		calendar.assignWorkingDay(true);
		
		assertTrue(calendar.workingDay());
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
		
		calendar.assignWorkingDay(true);
		
		assertFalse(calendar.valid());
		
		calendar.assignTime(Time.Winter);
		
		assertTrue(calendar.valid());
		
		calendar.assignTime(null);
		
		assertFalse(calendar.valid());
		
		calendar.assignDate(null);
		
		assertFalse(calendar.valid());
	}
}
