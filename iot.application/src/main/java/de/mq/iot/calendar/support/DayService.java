package de.mq.iot.calendar.support;

import java.time.LocalDate;

import de.mq.iot.calendar.DayGroup;

interface DayService {

	/**
	 * Read the DayGroup with the lowest priority.
	 * @param date the Date.
	 * @return the DayGroup.
	 */
	DayGroup dayGroup(final LocalDate date);

}