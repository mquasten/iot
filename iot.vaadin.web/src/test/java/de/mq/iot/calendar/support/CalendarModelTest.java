package de.mq.iot.calendar.support;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.support.CalendarModel.Events;
import de.mq.iot.calendar.support.CalendarModel.Filter;
import de.mq.iot.calendar.support.CalendarModel.ValidationErrors;
import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;

class CalendarModelTest {

	private static final String DATE = "31.12.2011";

	@SuppressWarnings("unchecked")
	private final Subject<Events, CalendarModel>   subject = Mockito.mock( Subject.class);
	
	private final CalendarModel calendarModel = new CalendarModelImpl(subject);
	
	private Observer observer = Mockito.mock(Observer.class);
	
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
	
	
	@Test
	public final void register() {
		calendarModel.register(	CalendarModel.Events.DatesChanged, observer);
		Mockito.verify(subject).register(CalendarModel.Events.DatesChanged, observer);
	}
	
	
	@Test
	public final void notifyObservers() {
		calendarModel.notifyObservers(CalendarModel.Events.DatesChanged);
		Mockito.verify(subject).notifyObservers(CalendarModel.Events.DatesChanged);
	}
	
	
	@Test
	public final void locale() {
		assertEquals(Locale.GERMAN, calendarModel.locale());
	}
	
	
	@Test
	public final void validateFrom() {
		assertEquals(ValidationErrors.Ok, calendarModel.validateFrom(DATE));
		Mockito.verify(subject).notifyObservers(Events.ValuesChanged);
	}
	
	@Test
	public final void validateFromResetDate() {
		calendarModel.assignFrom(DATE);
		assertNotNull(calendarModel.from());
		assertEquals(ValidationErrors.Ok, calendarModel.validateFrom(DATE));
		
		assertThrows(IllegalArgumentException.class,() ->  calendarModel.from());
	}
	
	@Test
	public final void validateFromMandatory() {
		assertEquals(ValidationErrors.Mandatory, calendarModel.validateFrom(" "));
		
		Mockito.verify(subject).notifyObservers(Events.ValuesChanged);
	}
	
	
	@Test
	public final void validateFromInvalidIncomplete() {
		assertEquals(ValidationErrors.Invalid, calendarModel.validateFrom("31.12"));
		
		Mockito.verify(subject).notifyObservers(Events.ValuesChanged);
	}
	
	
	@Test
	public final void validateFromInvalidParseException() {
		assertEquals(ValidationErrors.Invalid, calendarModel.validateFrom("31.12.xxxx"));
		
		Mockito.verify(subject).notifyObservers(Events.ValuesChanged);
	}
	


}
