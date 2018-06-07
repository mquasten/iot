package de.mq.iot.calendar;

import java.time.Year;
import java.util.Collection;

import de.mq.iot.calendar.Specialday;

public interface SpecialdayService {

	void save(Specialday specialday);

	Collection<Specialday> specialdays(Year year);

}