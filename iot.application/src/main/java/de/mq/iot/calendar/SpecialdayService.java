package de.mq.iot.calendar;

import java.time.LocalDate;
import java.time.Year;
import java.util.Collection;

import de.mq.iot.calendar.Specialday;

public interface SpecialdayService {

	void save(final Specialday specialday);

	Collection<Specialday> specialdays(final Year year);

	Collection<Specialday> vacation(final LocalDate begin, final LocalDate end);

}