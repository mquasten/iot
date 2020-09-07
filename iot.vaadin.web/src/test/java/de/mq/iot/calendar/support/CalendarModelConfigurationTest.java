package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.calendar.DayGroup;
import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;

class CalendarModelConfigurationTest {
	
	@SuppressWarnings("unchecked")
	private final Subject<CalendarModel.Events, CalendarModel> subject = Mockito.mock(Subject.class);
	
	private final CalendarModelConfiguration calendarModelConfiguration = new CalendarModelConfiguration(); 
	
	private final Observer observer = Mockito.mock(Observer.class);

	private final DayGroup dayGroup = Mockito.mock(DayGroup.class); 
	@Test
	void calendarModel() {
		
	Mockito.when(dayGroup.name()).thenReturn(DayGroup.NON_WORKINGDAY_GROUP_NAME);
		
		final CalendarModel calendarModel = calendarModelConfiguration.calendarModel(subject, Arrays.asList(dayGroup));
		assertNotNull(calendarModel);
		
		calendarModel.register(CalendarModel.Events.DatesChanged, observer);
		
		Mockito.verify(subject).register(CalendarModel.Events.DatesChanged, observer);
		
		final Map<?,?> dayGroups = (Map<?, ?>) ReflectionTestUtils.getField(calendarModel, CalendarModelImpl.class, "dayGroups");
		assertEquals(1, dayGroups.size());
		assertEquals(dayGroup, dayGroups.get(DayGroup.NON_WORKINGDAY_GROUP_NAME));
		
		
	}

}
