package de.mq.iot.calendar.support;

import java.util.Locale;

import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;



public class CalendarModelImpl  implements CalendarModel  {

	
	private final Subject<CalendarModel.Events, CalendarModel> subject;


	

	CalendarModelImpl(final Subject<Events, CalendarModel> subject) {
		this.subject = subject;

	}


	@Override
	public final Observer register(final Events key, final Observer observer) {
		return subject.register(key, observer);
	}

	@Override
	public final void notifyObservers(final Events key) {
		subject.notifyObservers(key);

	}


	@Override
	public Locale locale() {
		return Locale.GERMAN;
	}
	

}
