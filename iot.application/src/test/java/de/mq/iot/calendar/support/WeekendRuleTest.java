package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService.DayType;

class WeekendRuleTest {

	private final AbstractSpecialdaysRule rule = new WeekendRuleImpl();

	private final Specialday specialday = new SpecialdayImpl(DayOfWeek.TUESDAY);

	private final Specialday specialdayWeekend = new SpecialdayImpl(DayOfWeek.SATURDAY, true);

	@Test
	void execute() {
		final Optional<Entry<DayType, String>> result = rule.execute(Arrays.asList(specialdayWeekend, specialday),
				LocalDate.of(2020, 5, 2));

		assertTrue(result.isPresent());
		assertEquals(DayType.NonWorkingDay, result.get().getKey());
		assertEquals(String.format(AbstractSpecialdaysRule.DAY_TYPE_INFO_FORMAT, Specialday.Type.Weekend,
				DayOfWeek.SATURDAY), result.get().getValue());
	}

	@Test
	void executeNotWeekend() {
		assertEquals(Optional.empty(),
				rule.execute(Arrays.asList(specialdayWeekend, specialday), LocalDate.of(2020, 5, 1)));
	}
	@Test
	void priority() {
		assertEquals(3, rule.getPriority());
	}

}
