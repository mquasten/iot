package de.mq.iot.calendar.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.util.Collection;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.Specialday.Type;
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
		FromBeforeTo, 
		RangeSize;
	}
	
	enum Filter {
		Vacation,
		WorkingDate,
		WorkingDay;
	}
	
	ValidationErrors validateFrom(final String from);
	
	ValidationErrors validateTo(final String to);

	boolean valid();

	void assignFrom(String from);

	void assignTo(String to);

	ValidationErrors vaidate(final int maxDays);

	LocalDate from();

	LocalDate to();

	Collection<Type> filter();

	void assign(Filter filter);

	boolean isChangeCalendarAllowed();


	String convert(final Specialday specialday, final Year year);

	boolean isDayOfWeek();

	Collection<DayOfWeek> daysOfWeek();

	ValidationErrors validateDayofWeek(final DayOfWeek dayOfWeek);

	void assignDayOfWeek(final DayOfWeek dayOfWeek);

	Specialday dayOfWeek();


}
