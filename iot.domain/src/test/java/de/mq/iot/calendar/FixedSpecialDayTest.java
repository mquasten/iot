package de.mq.iot.calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.MonthDay;

import org.junit.jupiter.api.Test;

import de.mq.iot.calendar.Specialday.FixedSpecialDay;

class FixedSpecialDayTest {
	
	@Test
	void newYear() {
		assertEquals(MonthDay.of(1, 1),FixedSpecialDay.NewYear.monthDay());
	}
	
	@Test
	void laborDay() {
		assertEquals(MonthDay.of(5, 1),FixedSpecialDay.LaborDay.monthDay());
	}
	
	@Test
	void germanUnity() {
		assertEquals(MonthDay.of(10, 3),FixedSpecialDay.GermanUnity.monthDay());
	}
	
	@Test
	void  allHallows() {
		assertEquals(MonthDay.of(11, 1),FixedSpecialDay.AllHallows.monthDay());
	}
	
	@Test
	void  christmasDay() {
		assertEquals(MonthDay.of(12, 25),FixedSpecialDay.ChristmasDay.monthDay());
	}
	
	@Test
	void  boxingDay() {
		assertEquals(MonthDay.of(12, 26),FixedSpecialDay.BoxingDay.monthDay());
	}

}
