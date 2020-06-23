package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;

class DayOfWeekTest {
	private static final int PRIORITY = 2;

	private final DayGroup dayGroup = new DayGroupImpl("Feiertag", PRIORITY);
	private final DayOfWeek dayOfWeek = DayOfWeek.FRIDAY;
	private Day<DayOfWeek> day = new DayOfWeekImpl(dayGroup, dayOfWeek);
	
	
	
	
	@Test
	void id() {
		assertEquals(new UUID(DayOfWeekImpl.KEY_PREFIX, dayOfWeek.getValue()).toString(), day.id());
	}
	
	@Test
	void evaluate() {
		assertTrue(day.evaluate(LocalDate.of(2020, 5, 29)));
		assertFalse(day.evaluate(LocalDate.of(2020, 5, 28)));
	}
	@Test
	void value() {
		assertEquals(dayOfWeek, day.value());
	}
	
	@Test
	void string() {
		assertEquals(String.format(DayOfWeekImpl.TO_STRING_PATTERN, dayOfWeek, dayGroup.name()), day.toString());
	}
	@Test
	void frequency() {
		assertEquals(DayOfWeekImpl.FREQUENCY_ONCE_PER_WEEK, day.frequency());
	}
	
	@Test
	void compare() {
		final List<Day<?>> days = new ArrayList<>();
		
		final Day<LocalDate> otherDay = new GaussDayImpl<>(dayGroup, 0);
		days.add(day);
		days.add(otherDay);
		
		Collections.sort(days);
		assertEquals(otherDay, days.get(0));
		
	}
	
	@Test
	void privateConstructor() throws NoSuchMethodException, SecurityException {
		final Constructor<?> constructor =DayOfWeekImpl.class.getDeclaredConstructor();
		assertNotNull(BeanUtils.instantiateClass(constructor));
	}
	
}
