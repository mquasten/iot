package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;

class FixedDayTest {
	private final DayGroup dayGroup = new DayGroupImpl("Feiertag", 2);
	private final MonthDay monthDay =  MonthDay.of(5, 28);
	private final Day<LocalDate> day = new FixedDayImpl<>(dayGroup,monthDay);
	
	private final Supplier<YearMonth> yearMonth = () -> YearMonth.of(2020, 1);
	
	@BeforeEach
	void setup() {
		Arrays.asList(AbstractDay.class.getDeclaredFields()).stream().filter(field -> field.getType()==Supplier.class ).forEach(field -> ReflectionTestUtils.setField(day, field.getName(), yearMonth));
	}

	@Test
	void id() {
		assertEquals(new UUID(FixedDayImpl.class.hashCode(), monthDay.hashCode()).toString(), day.id());
	}
	
	@Test
	void evaluate() {
		assertTrue(day.evaluate(LocalDate.of(yearMonth.get().getYear(), monthDay.getMonth(), monthDay.getDayOfMonth())));
		assertFalse(day.evaluate(LocalDate.of(Year.now().getValue(), 1, 1)));
	}
	@Test
	void value() {
		assertEquals(LocalDate.of(yearMonth.get().getYear(), monthDay.getMonth(), monthDay.getDayOfMonth()), day.value());
	}
	@Test
	void string() {
		assertEquals(String.format(FixedDayImpl.TO_STRING_PATTERN, monthDay.getMonth().getValue(), monthDay.getDayOfMonth(), dayGroup.name()), day.toString());
	}
}
