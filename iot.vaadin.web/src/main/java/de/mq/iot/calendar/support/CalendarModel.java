package de.mq.iot.calendar.support;

import java.time.LocalDate;
import java.util.function.Predicate;

import de.mq.iot.calendar.Specialday;
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
		All,
		Vacation;
	}
	
	ValidationErrors validateFrom(final String from);
	
	ValidationErrors validateTo(final String to);

	boolean valid();

	void assignFrom(String from);

	void assignTo(String to);

	ValidationErrors vaidate(final int maxDays);

	LocalDate from();

	LocalDate to();

	Predicate<Specialday> filter();

	void assign(Filter filter);
}
