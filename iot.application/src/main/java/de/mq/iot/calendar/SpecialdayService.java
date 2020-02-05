package de.mq.iot.calendar;

import java.time.LocalDate;
import java.time.Year;
import java.util.Collection;
import java.util.Map.Entry;

import de.mq.iot.calendar.Specialday.Type;

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

	Entry<DayType, String> typeOfDay(LocalDate date);

	Collection<Specialday> specialdays(Collection<Type> types);

	

}