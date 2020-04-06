package de.mq.iot.calendar.support;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService.DayType;

class SpecialWorkingDateRuleTest {
	
	private AbstractSpecialdaysRule rule = new SpecialWorkingDateRuleImpl();
	
	@Test
	void priority() {
		assertEquals(1, rule.getPriority());
	}
	@Test
	void execute() {
		final LocalDate date = LocalDate.now();
		final Specialday workingdate = Mockito.mock(Specialday.class);
		Mockito.when(workingdate.type()).thenReturn(Specialday.Type.SpecialWorkingDate);
		Mockito.when(workingdate.date(date.getYear())).thenReturn(date);
	
		final Specialday workingday = Mockito.mock(Specialday.class);
		Mockito.when(workingday.type()).thenReturn(Specialday.Type.SpecialWorkingDay);
		
		final Specialday otherWorkingdate = Mockito.mock(Specialday.class);
		Mockito.when(otherWorkingdate.type()).thenReturn(Specialday.Type.SpecialWorkingDate);
		final LocalDate otherDate = LocalDate.now().minusDays(10);
		Mockito.when(otherWorkingdate.date(otherDate.getYear())).thenReturn(otherDate);
		
		final Collection<Specialday> specialdays = Arrays.asList(otherWorkingdate,workingday, workingdate);
		final Optional<Entry<DayType,String>> result = rule.execute(specialdays, date);
		assertFalse(result.isEmpty());
		assertEquals(DayType.SpecialWorkingDay, result.get().getKey());
		assertEquals(String.format(SpecialWorkingDateRuleImpl.DAY_TYPE_INFO_FORMAT,Specialday.Type.SpecialWorkingDate, date ), result.get().getValue());
	}
	
	@Test
	void executeNotFound() {
		assertEquals(Optional.empty(), rule.execute(Collections.emptyList(), LocalDate.now()));
	}

}
