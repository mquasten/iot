package de.mq.iot.calendar.support;

import java.time.LocalDate;
import java.util.Collection;

import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;

public interface DayService {

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

	/**
	 * save the given day in mongo db
	 * @param day the day that should be saved.
	 */
	void save(final Day<?> day);

	/**
	 * Delete the given day from mongo db
	 * @param day the day that should be deleted.
	 */
	void delete(Day<?> day);

	/**
	 * Create a transient Collection of Days for every day between beginDate and endDate, if the day not exists in the database.
	 * The days will not be stored in the database.
	 * @param dayGroup the DayGroup, for that the days should be created.
	 * @param beginDate the begin of the dateRange.
	 * @param endDate the begin of the dateRange.
	 * @return the days between beginDate and endDate , if the day doesn't  exist.
	 */
	Collection<Day<LocalDate>> newLocalDateDay(final DayGroup dayGroup, final LocalDate beginDate, final LocalDate endDate);

}