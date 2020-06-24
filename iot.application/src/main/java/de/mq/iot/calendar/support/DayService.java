package de.mq.iot.calendar.support;

import java.time.LocalDate;
import java.util.Collection;

import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;

interface DayService {

	/**
	 * Read the DayGroup with the lowest priority.
	 * @param date the Date.
	 * @return the DayGroup.
	 */
	DayGroup dayGroup(final LocalDate date);
	
	/**
	 * Read Days older or equals the date. Only days with type LocalDateDayImpl.class will be selected.
	 * @param date days equals or elder the date will be selected. 
	 * @return Collection with selected days.
	 */
	Collection<Day<LocalDate>> localDateDaysBeforeOrEquals(final LocalDate date);

}