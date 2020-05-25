package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;


import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;

class GaussDayTest {
	
	private static final long OFFSET = -2L;
	private final DayGroup dayGroup = new DayGroupImpl("Feiertag", 2);
	private Day day = new GaussDayImpl(dayGroup, -2);
	
	@Test
	void id() {
		assertEquals(new UUID(GaussDayImpl.class.hashCode(), OFFSET).toString(), day.id());
		
	}
	
	@Test
	void dayGroup() {
		assertEquals(dayGroup, day.dayGroup());
	}
	
	@Test
	void compare() {
		final List<Day> days = new ArrayList<>();
		days.add(day);
		final Day otherDay = new GaussDayImpl(new DayGroupImpl(dayGroup.name(), 0), 0);
		days.add(otherDay);
		
		assertEquals(day, days.get(0));
		
		Collections.sort(days);
		assertEquals(otherDay, days.get(0));
	}
	@Test
	void evaluate() {
		assertTrue(day.evaluate(LocalDate.of(2020, 4, 10)));
		assertFalse(day.evaluate(LocalDate.of(2020, 5, 28)));
	}
	
	@Test
	void string() {
		assertEquals(String.format(GaussDayImpl.TO_STRING_PATTERN,  OFFSET, dayGroup.name()), day.toString());
	}

}
