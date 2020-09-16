package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;

class GaussDayTest {
	
	private static final int PRIORITY = 1;
	private static final int OFFSET = -2;
	private final DayGroup dayGroup = new DayGroupImpl("Feiertag", 2);
	private Day<LocalDate> day = new GaussDayImpl(dayGroup, OFFSET);
	private final Supplier<YearMonth> yearMonth = () -> YearMonth.of(2020, 1);
	private LocalDate goodFriday = LocalDate.of(2020, 4, 10);
	
	@BeforeEach
	void setup() {
		Arrays.asList(AbstractDay.class.getDeclaredFields()).stream().filter(field -> field.getType()==Supplier.class ).forEach(field -> ReflectionTestUtils.setField(day, field.getName(), yearMonth));
	}
	
	@Test
	void id() {
		assertEquals(new UUID( dayGroup.name().hashCode()*100 + GaussDayImpl.KEY_PREFIX, OFFSET).toString(), day.id());
		
	}
	
	@Test
	void dayGroup() {
		assertEquals(dayGroup, day.dayGroup());
	}
	
	@Test
	void compare() {
		final List<Day<LocalDate>> days = new ArrayList<>();
		days.add(day);
		final Day<LocalDate> otherDay = new GaussDayImpl(new DayGroupImpl(dayGroup.name(), 0), 0);
		days.add(otherDay);
		
		assertEquals(day, days.get(0));
		
		Collections.sort(days);
		assertEquals(otherDay, days.get(0));
	}
	@Test
	void evaluate() {
		assertTrue(day.evaluate(goodFriday));
		assertFalse(day.evaluate(LocalDate.of(2020, 5, 28)));
	}
	
	@Test
	void string() {
		assertEquals(String.format(GaussDayImpl.TO_STRING_PATTERN,  OFFSET, dayGroup.name()), day.toString());
	}
	
	@Test
	void yearMonth() {
		assertEquals(YearMonth.now(), new GaussDayImpl(dayGroup, PRIORITY).yearMonth());
	}
	
	@Test
	void value() {
		assertEquals(goodFriday, day.value());
	}
	@Test
	void frequency() {
		assertEquals(AbstractDay.FREQUENCY_ONCE_PER_YEAR, day.frequency());
	}
	@Test
	void hash() {
		assertEquals(new UUID(dayGroup.name().hashCode()*100 + GaussDayImpl.KEY_PREFIX, OFFSET).toString().hashCode(), day.hashCode());
		ReflectionTestUtils.setField(day, "id", null);
		assertEquals(System.identityHashCode(day), day.hashCode());
	}
	@SuppressWarnings("unlikely-arg-type")
	@Test
	void equals() {
		assertFalse(day.equals(""));
		final Day<?> dayWithNoId = new GaussDayImpl(dayGroup, 0);
		ReflectionTestUtils.setField(dayWithNoId, "id", null);
		assertFalse(day.equals(dayWithNoId));
		assertFalse(dayWithNoId.equals(day));
		assertTrue(dayWithNoId.equals(dayWithNoId));
		assertTrue(day.equals(new GaussDayImpl(dayGroup, OFFSET)));
	}
	
	@Test
	void most() {
		final long mostSigBits = new GaussDayImpl(dayGroup, OFFSET).mostSigBits(GaussDayImpl.KEY_PREFIX);
		assertEquals(dayGroup.name().hashCode()*100 + GaussDayImpl.KEY_PREFIX , mostSigBits);
	}
	
	@Test
	void privateConstructor() throws NoSuchMethodException, SecurityException {
		assertNotNull(BeanUtils.instantiateClass(GaussDayImpl.class.getDeclaredConstructor()));
	}
}
