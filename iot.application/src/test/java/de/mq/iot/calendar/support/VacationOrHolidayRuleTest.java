package de.mq.iot.calendar.support;




import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService.DayType;

class VacationOrHolidayRuleTest {
	
	private final AbstractSpecialdaysRule vacationOrHolidayRule = new VacationOrHolidayRuleImpl();
	
	private final Specialday gauss = new SpecialdayImpl(1);
	private final Specialday fix = new SpecialdayImpl(MonthDay.of(1, 1));
	private final Specialday vacation = new SpecialdayImpl(LocalDate.of(2020, 5, 28));
	private final Collection<Specialday>specialdays = Set.of(gauss,fix,vacation);
	
	
	
	@Test
	void gauss() {
		final LocalDate date = LocalDate.of(2020, 4, 13);
		final Optional<Entry<DayType, String>> result = vacationOrHolidayRule.execute(specialdays, date);
		assertTrue(result.isPresent());
		assertEquals(DayType.NonWorkingDay, result.get().getKey());
		assertEquals(String.format(AbstractSpecialdaysRule.DAY_TYPE_INFO_FORMAT, Specialday.Type.Gauss, date), result.get().getValue());;
	}
	
	@Test
	void fix() {
		final LocalDate date = LocalDate.of(2020, 1, 1);
		final Optional<Entry<DayType, String>> result = vacationOrHolidayRule.execute(specialdays, date);
		assertTrue(result.isPresent());
		assertEquals(DayType.NonWorkingDay, result.get().getKey());
		assertEquals(String.format(AbstractSpecialdaysRule.DAY_TYPE_INFO_FORMAT, Specialday.Type.Fix, date), result.get().getValue());
	}
	@Test
	void vacation() {
		final LocalDate date = LocalDate.of(2020, 5, 28);
		final Optional<Entry<DayType, String>> result = vacationOrHolidayRule.execute(specialdays, date);
		assertTrue(result.isPresent());
		assertEquals(DayType.NonWorkingDay, result.get().getKey());
		assertEquals(String.format(AbstractSpecialdaysRule.DAY_TYPE_INFO_FORMAT, Specialday.Type.Vacation, date), result.get().getValue());
	}
	
	@Test
	void nothing() {
		final LocalDate date = LocalDate.of(2020, 1, 2);
		final Optional<Entry<DayType, String>> result = vacationOrHolidayRule.execute(specialdays, date);
		assertFalse(result.isPresent());
	}
	
	@Test
	void priority() {
		assertEquals(2, vacationOrHolidayRule.getPriority());
	}

}
