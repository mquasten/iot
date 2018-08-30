package de.mq.iot.calendar.support;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.support.CalendarModel.Events;
import de.mq.iot.calendar.support.CalendarModel.Filter;
import de.mq.iot.model.Subject;

class CalendarModelTest {

	@SuppressWarnings("unchecked")
	private final Subject<Events, CalendarModel>   subject = Mockito.mock( Subject.class);
	
	private final CalendarModel calendarModel = new CalendarModelImpl(subject);
	
	@Test
	public final void create() {
		final Map<Class<?>, Object> dependencies = new HashMap<>();
		Arrays.asList(calendarModel.getClass().getDeclaredFields()).stream().filter( field -> ! Modifier.isStatic(field.getModifiers())).filter(field -> ! field.getType().equals(Optional.class)).forEach(field -> dependencies.put(field.getType(), ReflectionTestUtils.getField(calendarModel, field.getName())));
	
	    assertEquals(3, dependencies.size());
	    assertEquals(subject, dependencies.get(Subject.class));
	    assertEquals(CalendarModel.Filter.All ,  dependencies.get(CalendarModel.Filter.class));
	    
	    @SuppressWarnings("unchecked")
		final Map<CalendarModel.Filter, Predicate<Specialday>> filters =  (Map<Filter, Predicate<Specialday>>) dependencies.get(Map.class);
	    assertEquals(2, filters.size());
	    
	   assertTrue( filters.get(CalendarModel.Filter.All).test(Mockito.mock(Specialday.class)));
	   final Specialday specialday = Mockito.mock(Specialday.class);
	 
	   Mockito.when(specialday.isVacation()).thenReturn(true);
	   assertTrue( filters.get(CalendarModel.Filter.Vacation).test(specialday));
	 
	}
	
}
