package de.mq.iot.calendar.support;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.time.LocalDate;
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

	private static final String TO_METHOD = "to";

	private static final String FROM_METHOD = "from";

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
	

	@Test
	public final void validateTo() {
		assertEquals(ValidationErrors.Ok, calendarModel.validateTo(DATE));
		Mockito.verify(subject).notifyObservers(Events.ValuesChanged);
	}
	
	@Test
	public final void vaidate() {
		
		calendarModel.assignFrom("01.01.2018");
		calendarModel.assignTo("10.01.2018");
		assertEquals(ValidationErrors.Ok, calendarModel.vaidate(30));
	}
	
	@Test
	public final void vaidateInvalid() {
		
		assertEquals(ValidationErrors.Invalid, calendarModel.vaidate(30));
	}
	
	@Test
	public final void vaidateFromBeforeTo() {
		
		calendarModel.assignFrom("10.01.2018");
		calendarModel.assignTo("01.01.2018");
		assertEquals(ValidationErrors.FromBeforeTo, calendarModel.vaidate(30));
	}
	
	@Test
	public final void vaidateFromMaxDays() {
		
		calendarModel.assignFrom("10.01.2018");
		calendarModel.assignTo("31.12.2018");
		assertEquals(ValidationErrors.RangeSize, calendarModel.vaidate(30));
	}
	
	@Test
	public final void vaidatefromOldDate() {
		assertEquals(ValidationErrors.Invalid, calendarModel.validateFrom("01.01.1900"));
		Mockito.verify(subject).notifyObservers(Events.ValuesChanged);
	}
	
	@Test
	public final void assignFrom() {
		assertEquals(Optional.empty(), ReflectionTestUtils.getField(calendarModel, FROM_METHOD));
		calendarModel.assignFrom(DATE);
		
		assertEquals(localDate(), calendarModel.from());
		calendarModel.assignFrom("");
		assertEquals(Optional.empty(), ReflectionTestUtils.getField(calendarModel, FROM_METHOD));
	}


	private  LocalDate localDate() {
		final String[] cols = DATE.split("[.]");
		return LocalDate.of(Integer.valueOf(cols[2]), Integer.valueOf(cols[1]), Integer.valueOf(cols[0]));
	}
	
	@Test
	public final void assignTo() {
		assertEquals(Optional.empty(), ReflectionTestUtils.getField(calendarModel, TO_METHOD));
		calendarModel.assignTo(DATE);
		
		assertEquals(localDate(), calendarModel.to());
		calendarModel.assignTo("");
		assertEquals(Optional.empty(), ReflectionTestUtils.getField(calendarModel, TO_METHOD));
	}
	
	@Test
	public final void valid() {
		calendarModel.assignTo(DATE);
		calendarModel.assignFrom(DATE);
		assertTrue(calendarModel.valid());
		calendarModel.assignFrom("");
		assertFalse(calendarModel.valid());
		calendarModel.assignTo("");
		assertFalse(calendarModel.valid());
		calendarModel.assignTo(DATE);
		assertFalse(calendarModel.valid());
	}

	
	@Test
	public final void from() {
		calendarModel.assignFrom(DATE);
		assertEquals(localDate(), calendarModel.from());
	}
	
	@Test
	public final void fromNotAssigned() {
		assertThrows(IllegalArgumentException.class,  () -> calendarModel.from());
	}
	
	@Test
	public final void to() {
		calendarModel.assignTo(DATE);
		assertEquals(localDate(), calendarModel.to());
	}
	
	@Test
	public final void toNotAssigned() {
		assertThrows(IllegalArgumentException.class,  () -> calendarModel.to());
	}
	
	@Test
	public final void filter() {
		assertTrue(calendarModel.filter().test(null));
		final Specialday specialday = Mockito.mock(Specialday.class);
		calendarModel.assign(CalendarModel.Filter.Vacation);
		assertFalse(calendarModel.filter().test(specialday));
		Mockito.when(specialday.isVacation()).thenReturn(true);
		assertTrue(calendarModel.filter().test(specialday));
	}
	
}
