package de.mq.iot.calendar;

import java.time.LocalDate;
import java.time.Year;
import java.util.Collection;

import de.mq.iot.calendar.Specialday.Type;
import de.mq.iot.calendar.support.SpecialdaysRulesEngineResult;

public interface SpecialdayService {
	
	public enum DayType {
		NonWorkingDay,
		WorkingDay,
		SpecialWorkingDay;
	}

	void save(final Specialday specialday);

	Collection<Specialday> specialdays(final Year year);

	Collection<Specialday> vacationOrSpecialWorkingDates(final LocalDate begin, final LocalDate end, final boolean specialWorkingDate);

	Collection<Specialday> specialdays();
	
	void delete(Specialday specialday);

	Collection<Specialday> vacationsOrSpecialWorkingDatesBeforeEquals(final LocalDate minDate);

	

	Collection<Specialday> specialdays(Collection<Type> types);

	SpecialdaysRulesEngineResult specialdaysRulesEngineResult(LocalDate date);  

	

}