package de.mq.iot.calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.mq.iot.calendar.Specialday.VariantSpecialDay;

class VariantSpecialDayTest {
	
	@Test
	void goodFriday() {
		assertEquals(-2, VariantSpecialDay.GoodFriday.daysFromEasterDay());
	}
	
	@Test
	void easter() {
		assertEquals(0, VariantSpecialDay.Easter.daysFromEasterDay());
	}
	
	@Test
	void easterMonday() {
		assertEquals(1, VariantSpecialDay.EasterMonday.daysFromEasterDay());
	}

	
	@Test
	void ascension() {
		assertEquals(39, VariantSpecialDay.Ascension.daysFromEasterDay());
	}
	
	@Test
	void whitMonday() {
		assertEquals(50, VariantSpecialDay.WhitMonday.daysFromEasterDay());
	}
	
	@Test
	void  corpusChristi() {
		assertEquals(60, VariantSpecialDay.CorpusChristi.daysFromEasterDay());
	}
}
