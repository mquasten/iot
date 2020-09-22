package de.mq.iot.calendar.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;

import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;
import de.mq.iot.model.LocaleAware;
import de.mq.iot.model.Subject;


public interface CalendarModel extends Subject<CalendarModel.Events, CalendarModel> , LocaleAware  {

	enum Events {
	
		ChangeLocale,
		ValuesChanged,
		DatesChanged;
	}
	
	enum ValidationErrors {
		Ok,
		Mandatory,
		Invalid,
		InPast,
		FromBeforeTo, 
		RangeSize;
	}
	
	enum Filter {
		Vacation(LocalDateDayImpl.class, DayGroup.NON_WORKINGDAY_GROUP_NAME),
		WorkingDate(LocalDateDayImpl.class, DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME),
		WorkingDay(DayOfWeekImpl.class, DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME);
		
		private final String group;
		
		private final Class<?> clazz;
 		Filter( final Class<?> clazz , final String group){
 			this.clazz=clazz;
			this.group=group;
		}
		
		String group() {
			return group;
		}
		
		Class<?> type() {
			return clazz;
		}
		
		
	}
	
	ValidationErrors validateFrom(final String from);
	
	ValidationErrors validateTo(final String to);

	boolean valid();

	void assignFrom(String from);

	void assignTo(String to);

	ValidationErrors vaidate(final int maxDays);

	LocalDate from();

	LocalDate to();

	Predicate<Day<?>> filter();

	void assign(Filter filter);

	boolean isChangeCalendarAllowed();


	String convert(final Day<?> specialday, final Year year);

	boolean isDayOfWeek();

	Collection<DayOfWeek> daysOfWeek();

	ValidationErrors validateDayofWeek(final DayOfWeek dayOfWeek);

	void assignDayOfWeek(final DayOfWeek dayOfWeek);

	Day<?> dayOfWeek();

	boolean isSpecialWorkingDate();

	DayGroup dayGroup();

	Filter filter(final Day<?> day);

	Comparator<? super Day<?>> comparator();


}
