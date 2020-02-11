package de.mq.iot.calendar.support;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.Authority;
import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.Specialday.Type;
import de.mq.iot.calendar.support.CalendarModel.Events;
import de.mq.iot.calendar.support.CalendarModel.Filter;
import de.mq.iot.calendar.support.CalendarModel.ValidationErrors;
import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;


class CalendarModelTest {

	private static final String TO_METHOD = "to";

	private static final String FROM_METHOD = "from";


	@SuppressWarnings("unchecked")
	private final Subject<Events, CalendarModel>   subject = Mockito.mock( Subject.class);
	
	private final CalendarModel calendarModel = new CalendarModelImpl(subject);
	
	private Observer observer = Mockito.mock(Observer.class);
	
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	
	@Test
	public final void create() {
		final Map<Class<?>, Object> dependencies = new HashMap<>();
		Arrays.asList(calendarModel.getClass().getDeclaredFields()).stream().filter( field -> ! Modifier.isStatic(field.getModifiers())).filter(field -> ! field.getType().equals(Optional.class)).forEach(field -> dependencies.put(field.getType(), ReflectionTestUtils.getField(calendarModel, field.getName())));
	
	    assertEquals(4, dependencies.size());
	    assertEquals(subject, dependencies.get(Subject.class));
	    assertEquals(CalendarModel.Filter.Vacation ,  dependencies.get(CalendarModel.Filter.class));
	    
	    @SuppressWarnings("unchecked")
		final Map<CalendarModel.Filter, Collection<Filter>> filters =  (Map<Filter,  Collection<Filter>>) dependencies.get(Map.class);
	    assertEquals(3, filters.size());
	    assertEquals(Arrays.asList(Specialday.Type.Vacation), filters.get(CalendarModel.Filter.Vacation));
	    assertEquals(Arrays.asList(Specialday.Type.SpecialWorkingDate), filters.get(CalendarModel.Filter.WorkingDate));
	    assertEquals(Arrays.asList(Specialday.Type.SpecialWorkingDay), filters.get(CalendarModel.Filter.WorkingDay));
	   
	 
	    final Collection<?> typesForDayOfWeek = (Collection<?>) dependencies.get(Collection.class);
	    assertEquals(2, typesForDayOfWeek.size());
	    assertTrue(typesForDayOfWeek.contains(Type.SpecialWorkingDay));
	    assertTrue(typesForDayOfWeek.contains(Type.Weekend));
	    
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
		assertEquals(ValidationErrors.Ok, calendarModel.validateFrom(LocalDate.now().format(formatter)));
		Mockito.verify(subject).notifyObservers(Events.ValuesChanged);
	}
	
	@Test
	public final void validateFromResetDate() {
		final String date = LocalDate.now().format(formatter);
		calendarModel.assignFrom(date);
		assertNotNull(calendarModel.from());
		assertEquals(ValidationErrors.Ok, calendarModel.validateFrom(date));
		
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
		assertEquals(ValidationErrors.Ok, calendarModel.validateTo(LocalDate.now().format(formatter)));
		Mockito.verify(subject).notifyObservers(Events.ValuesChanged);
	}
	
	@Test
	public final void vaidate() {
		final LocalDate date = LocalDate.now();
		calendarModel.assignFrom(date.format(formatter));
		calendarModel.assignTo(date.plusDays(10).format(formatter));
		assertEquals(ValidationErrors.Ok, calendarModel.vaidate(30));
	}
	
	@Test
	public final void vaidateInvalid() {
		
		assertEquals(ValidationErrors.Invalid, calendarModel.vaidate(30));
	}
	
	@Test
	public final void vaidateFromBeforeTo() {
		
		calendarModel.assignFrom(LocalDate.now().plusDays(10).format(formatter));
		calendarModel.assignTo(LocalDate.now().format(formatter));
		assertEquals(ValidationErrors.FromBeforeTo, calendarModel.vaidate(30));
	}
	
	@Test
	public final void vaidateFromMaxDays() {
		
		calendarModel.assignFrom(LocalDate.now().format(formatter));
		calendarModel.assignTo(LocalDate.now().plusDays(40).format(formatter));
		assertEquals(ValidationErrors.RangeSize, calendarModel.vaidate(30));
	}
	
	@Test
	public final void vaidateFromMaxDays2() {
		calendarModel.assignDayOfWeek(DayOfWeek.FRIDAY);
		calendarModel.assign(Filter.WorkingDay);
		assertEquals(ValidationErrors.Ok, calendarModel.vaidate(Integer.MAX_VALUE));
		
	}
	
	@Test
	public final void vaidatefromOldDate() {
		assertEquals(ValidationErrors.InPast, calendarModel.validateFrom("01.01.1900"));
		Mockito.verify(subject).notifyObservers(Events.ValuesChanged);
	}
	
	@Test
	public final void assignFrom() {
		assertEquals(Optional.empty(), ReflectionTestUtils.getField(calendarModel, FROM_METHOD));
		
		final LocalDate date = LocalDate.now();
		calendarModel.assignFrom(date.format(formatter));
		
		assertEquals(date, calendarModel.from());
		calendarModel.assignFrom("");
		assertEquals(Optional.empty(), ReflectionTestUtils.getField(calendarModel, FROM_METHOD));
	}

	
	@Test
	public final void assignTo() {
		assertEquals(Optional.empty(), ReflectionTestUtils.getField(calendarModel, TO_METHOD));
		final LocalDate date = LocalDate.now();
		calendarModel.assignTo(date.format(formatter));
		
		assertEquals(date, calendarModel.to());
		calendarModel.assignTo("");
		assertEquals(Optional.empty(), ReflectionTestUtils.getField(calendarModel, TO_METHOD));
	}
	
	@Test
	public final void valid() {
		String date = LocalDate.now().format(formatter);
		calendarModel.assignTo(date);
		calendarModel.assignFrom(date);
		assertTrue(calendarModel.valid());
		calendarModel.assignFrom("");
		assertFalse(calendarModel.valid());
		calendarModel.assignTo("");
		assertFalse(calendarModel.valid());
		calendarModel.assignTo(date);
		assertFalse(calendarModel.valid());
		
		calendarModel.assignTo(null);
		calendarModel.assign(Filter.WorkingDay);
		calendarModel.assignDayOfWeek(DayOfWeek.MONDAY);
		assertTrue(calendarModel.valid());
		
		calendarModel.assign(null);
		calendarModel.assignDayOfWeek(null);
		assertFalse(calendarModel.valid());
		
	}

	
	@Test
	public final void from() {
		final LocalDate date = LocalDate.now();
		calendarModel.assignFrom(date.format(formatter));
		assertEquals(date, calendarModel.from());
	}
	
	@Test
	public final void fromNotAssigned() {
		assertThrows(IllegalArgumentException.class,  () -> calendarModel.from());
	}
	
	@Test
	public final void to() {
		LocalDate date = LocalDate.now();
		calendarModel.assignTo(date.format(formatter));
		assertEquals(date, calendarModel.to());
	}
	
	@Test
	public final void toNotAssigned() {
		assertThrows(IllegalArgumentException.class,  () -> calendarModel.to());
	}
	
	@Test
	public final void filter() {
		assertEquals(1, calendarModel.filter().size());
		assertEquals(Specialday.Type.Vacation, calendarModel.filter().stream().findAny().get());
		calendarModel.assign(CalendarModel.Filter.WorkingDate);
		assertEquals(1, calendarModel.filter().size());
		assertEquals(Specialday.Type.SpecialWorkingDate, calendarModel.filter().stream().findAny().get());
		
		Mockito.verify(subject).notifyObservers(Events.DatesChanged);
	}
	
	@Test
	void isChangeVariableAllowed() {
		
		assertFalse(calendarModel.isChangeCalendarAllowed());
		
		final Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(authentication.hasRole(Authority.Calendar)).thenReturn(true);
		Mockito.when(subject.currentUser()).thenReturn(Optional.of(authentication));
		
		assertTrue(calendarModel.isChangeCalendarAllowed());
		
		Mockito.when(authentication.hasRole(Authority.Calendar)).thenReturn(false);
		
		assertFalse(calendarModel.isChangeCalendarAllowed());
		
	}
	@Test
    final void convertDate() {
		final LocalDate expectedDate = LocalDate.of(1968, 5, 28);
		assertEquals(expectedDate.format(DateTimeFormatter.ofPattern(CalendarModelImpl.DATE_PATTERN)), calendarModel.convert(new SpecialdayImpl(expectedDate), Year.of(1)));
	}
	@Test
	final void convertDayOfWeek() {
		assertEquals(DayOfWeek.FRIDAY.getDisplayName(CalendarModelImpl.STYLE_DAY_OF_WEEK, Locale.GERMAN), calendarModel.convert(new SpecialdayImpl(DayOfWeek.FRIDAY), Year.of(1)));
	}
	@Test
	final void validateDayOfWeek() {
		assertEquals(CalendarModel.ValidationErrors.Ok, calendarModel.validateDayofWeek(DayOfWeek.TUESDAY));
		Mockito.verify(subject).notifyObservers(Events.ValuesChanged);
	}
	
	@Test
	final void validateDayOfWeekMandatory() {
		assertEquals(CalendarModel.ValidationErrors.Mandatory, calendarModel.validateDayofWeek(null));
		Mockito.verify(subject).notifyObservers(Events.ValuesChanged);
	}
	@Test
	final void isDayOfWeek() {
		calendarModel.assign(Filter.WorkingDay);
		assertTrue(calendarModel.isDayOfWeek());
		
		calendarModel.assign(null);
		assertFalse(calendarModel.isDayOfWeek());
		
		calendarModel.assign(Filter.Vacation);
		assertFalse(calendarModel.isDayOfWeek());
	}
	@Test
	final void isSpecialWorkingDate() {
		calendarModel.assign(Filter.Vacation);
		assertFalse(calendarModel.isSpecialWorkingDate());
		
		calendarModel.assign(Filter.WorkingDate);
		assertTrue(calendarModel.isSpecialWorkingDate());
	}
	@Test
	void daysOfWeek() {
		final Collection<DayOfWeek> results = calendarModel.daysOfWeek();
		assertEquals(5, results.size());
		assertEquals(expectedDaysOfWeek(), results);
	}


	private Collection<DayOfWeek> expectedDaysOfWeek() {
		final Collection<DayOfWeek> expected = new ArrayList<>(Arrays.asList(DayOfWeek.values()));
		expected.remove(DayOfWeek.SATURDAY);
		expected.remove(DayOfWeek.SUNDAY);
		return expected;
	}
	@Test
	void dayOfWeek() {
		calendarModel.assignDayOfWeek(DayOfWeek.FRIDAY);
		final Specialday result = calendarModel.dayOfWeek();
		assertEquals(DayOfWeek.FRIDAY, result.dayOfWeek());
	}
	
	@Test
	void dayOfWeekMissing() {
		assertThrows(IllegalArgumentException.class, () -> calendarModel.dayOfWeek());
	}
	
}
