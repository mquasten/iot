package de.mq.iot.calendar.support;

import java.time.LocalDate;

import de.mq.iot.model.LocaleAware;
import de.mq.iot.model.Subject;


public interface CalendarModel extends Subject<CalendarModel.Events, CalendarModel> , LocaleAware  {

	enum Events {
	
		ChangeLocale,
		ValuesChanged;
	}
	
	enum ValidationErrors {
		Ok,
		Mandatory,
		Invalid,
		FromBeforeTo, 
		RangeSize;
	}
	
	ValidationErrors validateFrom(final String from);
	
	ValidationErrors validateTo(final String to);

	boolean valid();

	void assignFrom(String from);

	void assignTo(String to);

	ValidationErrors vaidate(final int maxDays);

	LocalDate from();

	LocalDate to();
}
