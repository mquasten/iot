package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;

class LocalDateDayTest {
	
	private static final int PRIORITY = 2;

	private final DayGroup dayGroup = new DayGroupImpl("Feiertag", PRIORITY);
	private LocalDate date = LocalDate.of(2020, 5, 28);
	private Day<LocalDate> day = new LocalDateDayImpl(dayGroup, date);
	

	
	
	
	@Test
	void id() {
		assertEquals(new UUID(LocalDateDayImpl.class.hashCode(), date.hashCode()).toString(), day.id());
	}
	@Test
	void evaluate() {
		assertTrue(day.evaluate(date));
		assertFalse(day.evaluate(date.plusDays(1)));
	}
	@Test
	void value() {
		assertEquals(date, day.value());
	}
	@Test
	void string() {
		assertEquals(String.format(LocalDateDayImpl.TO_STRING_PATTERN, date,dayGroup.name()), day.toString());
	}
	
	@Test
	void  frequency() {
		assertEquals(AbstractDay.FREQUENCY_ONCE_PER_YEAR, day.frequency());
	}
	

}
