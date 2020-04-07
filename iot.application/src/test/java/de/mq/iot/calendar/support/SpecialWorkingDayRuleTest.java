package de.mq.iot.calendar.support;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService.DayType;

class SpecialWorkingDayRuleTest {
	private final AbstractSpecialdaysRule rule = new SpecialWorkingDayRuleImpl();
	
	private final Specialday specialday = new SpecialdayImpl(DayOfWeek.TUESDAY);
	
	private final Specialday specialdayWeekend = new SpecialdayImpl(DayOfWeek.TUESDAY, true);
	
	@Test
	void execute() {
		 final Optional<Entry<DayType, String>> result = rule.execute(Arrays.asList(specialdayWeekend,specialday), LocalDate.of(2020, 4, 7));
		assertTrue(result.isPresent());
		assertEquals(DayType.SpecialWorkingDay, result.get().getKey());
		assertEquals(String.format(AbstractSpecialdaysRule.DAY_TYPE_INFO_FORMAT,  Specialday.Type.SpecialWorkingDay ,DayOfWeek.TUESDAY), result.get().getValue());
	}
	
	@Test
	void executeNotSpecialWorkingDay() {
		final Optional<Entry<DayType, String>> result = rule.execute(Arrays.asList(specialday), LocalDate.of(2020, 3, 7));
		assertFalse(result.isPresent());
	}
	@Test
	void priority() {
		assertEquals(3, rule.getPriority());
	}
	
	
}
