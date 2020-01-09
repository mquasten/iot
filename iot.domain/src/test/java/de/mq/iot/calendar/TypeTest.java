package de.mq.iot.calendar;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;

class TypeTest {
	private final Collection<Specialday.Type> typesWithYear = Arrays.asList(Specialday.Type.SpecialWorkingDate, Specialday.Type.Vacation);
	
	@Test
	void withYear() {
		Arrays.asList(Specialday.Type.values()).stream().filter(type -> typesWithYear.contains(type)  ).forEach(type -> assertTrue(type.isWithYear()));
	}
	@Test
	void withoutYear() {
		Arrays.asList(Specialday.Type.values()).stream().filter(type -> !typesWithYear.contains(type)  ).forEach(type -> assertFalse(type.isWithYear()));
	}

}
