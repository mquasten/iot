package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.mq.iot.calendar.SpecialdayService.DayType;

class WorkingdayRuleTest {
	
	private final AbstractSpecialdaysRule rule = new WorkingdayRuleImpl();
	
	@Test
	void execute() {
		final LocalDate date = LocalDate.now();
		final Optional<Entry<DayType, String>> result = rule.execute(Arrays.asList(), date);
		
		assertTrue(result.isPresent());
		assertEquals(DayType.WorkingDay, result.get().getKey());
		assertEquals(String.format(AbstractSpecialdaysRule.DAY_TYPE_INFO_FORMAT, DayType.WorkingDay,
				date), result.get().getValue());
	}
	@Test
	void priority() {
		assertEquals(9, rule.getPriority());
	}

}
