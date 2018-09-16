package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;

class CalendarModelConfigurationTest {
	
	@SuppressWarnings("unchecked")
	private final Subject<CalendarModel.Events, CalendarModel> subject = Mockito.mock(Subject.class);
	
	private final CalendarModelConfiguration calendarModelConfiguration = new CalendarModelConfiguration(); 
	
	private final Observer observer = Mockito.mock(Observer.class);
	
	@Test
	void calendarModel() {
		final CalendarModel calendarModel = calendarModelConfiguration.calendarModel(subject);
		assertNotNull(calendarModel);
		
		calendarModel.register(CalendarModel.Events.DatesChanged, observer);
		
		Mockito.verify(subject).register(CalendarModel.Events.DatesChanged, observer);
		
		
	}

}
