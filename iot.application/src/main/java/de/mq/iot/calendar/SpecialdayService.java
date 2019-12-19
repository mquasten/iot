package de.mq.iot.calendar;

import java.time.LocalDate;
import java.time.Year;
import java.util.Collection;
import java.util.Map.Entry;

public interface SpecialdayService {
	
	public enum DayType {
		NonWorkingDay,
		WorkingDay,
		SpecialWorkingDay;
	}

	void save(final Specialday specialday);

	Collection<Specialday> specialdays(final Year year);

	Collection<Specialday> vacation(final LocalDate begin, final LocalDate end);

	Collection<Specialday> specialdays();
	
	void delete(Specialday specialday);

	Collection<Specialday> vacationsBeforeEquals(final LocalDate minDate);

	Entry<DayType, String> typeOfDay(LocalDate date);

}