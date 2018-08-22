package de.mq.iot.calendar.support;


import org.mockito.Mockito;

import de.mq.iot.calendar.support.CalendarModel.Events;
import de.mq.iot.model.Subject;

class CalendarModelTest {

	@SuppressWarnings("unchecked")
	private final Subject<Events, CalendarModel>   subject = Mockito.mock( Subject.class);
	
	private final CalendarModel calendarModel = new CalendarModelImpl(subject);
	
	
	
	
	
}
