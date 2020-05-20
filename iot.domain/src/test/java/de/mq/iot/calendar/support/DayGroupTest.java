package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

import de.mq.iot.calendar.DayGroup;

class DayGroupTest {
	
	
	static final int PRIORITY = 1;
	static final String NAME = "NonWorkingDay";
	DayGroup dayGroup = new DayGroupImpl(NAME, PRIORITY);
	
	@Test
	void name() {
		assertEquals(NAME, dayGroup.name());
	}
	@Test
	void priority() {
		assertEquals(PRIORITY, dayGroup.priority());
	}
	@Test
	void hash() {
		assertEquals(NAME.hashCode(), dayGroup.hashCode());
	}
	@Test
	void equals() {
		assertTrue(new DayGroupImpl(NAME, 9).equals(dayGroup));
		assertFalse(dayGroup.equals(new Date()));
		assertFalse(new DayGroupImpl("name", PRIORITY).equals(dayGroup));
		assertTrue(dayGroup.equals(dayGroup));
	}
	@Test
	void wrongPriorityNegatic() {
		assertThrows(IllegalArgumentException.class, () -> new DayGroupImpl(NAME, Integer.MIN_VALUE));
		
	}
	@Test
	void wrongPriorityMoreThan9() {
		assertThrows(IllegalArgumentException.class, () -> new DayGroupImpl(NAME, Integer.MAX_VALUE));
	}
	@Test
	void string() {
		assertEquals(String.format("DayGroupImpl[name=%s, priority=%s]", NAME,  PRIORITY), dayGroup.toString());
		
	}

}
