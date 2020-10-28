package de.mq.iot.calendar.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.util.Arrays;
import java.util.Collection;

import java.util.Comparator;
import java.util.function.Predicate;

import org.springframework.util.Assert;

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
		Vacation(DayGroup.NON_WORKINGDAY_GROUP_NAME, true, LocalDateDayImpl.class),
		WorkingDate(DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME,true, LocalDateDayImpl.class),
		WorkingDay(DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME, true, DayOfWeekImpl.class),
		Holiday(DayGroup.NON_WORKINGDAY_GROUP_NAME,false, GaussDayImpl.class, FixedDayImpl.class);
		
		private final String group;
		
		private boolean editable;
		
		private final Collection<Class<?>> classes;
 		Filter(final String group, final boolean editable, final Class<?>...classes){
 			Assert.hasText(group, "Group is required.");
 			Assert.notEmpty(classes, "Al least one Class isrequired.");
 			this.classes=Arrays.asList(classes);
			this.group=group;
			this.editable=editable;
		}
		
		final String group() {
			return group;
		}
		
		final Collection<Class<?>> types() {
			return classes;
		}
		
		final boolean editable() {
			return editable;
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

	boolean editable();

}
