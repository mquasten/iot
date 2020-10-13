package de.mq.iot.calendar.support;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.Authority;
import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;


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
	
	private final CalendarModel calendarModel = new CalendarModelImpl(subject, new DayConfiguration().dayGroups());
	
	private Observer observer = Mockito.mock(Observer.class);
	
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	
	
	
	@Test
	public final void create() {
		
		final Map<Class<?>, Object> dependencies = dependencies();
	
		
		
	    assertEquals(4, dependencies.size());
	    assertEquals(subject, dependencies.get(Subject.class));
	    assertEquals(CalendarModel.Filter.Vacation ,  dependencies.get(CalendarModel.Filter.class));
	    
	    final Map<?,?> mapDependencies =  (Map<?,  ?>) dependencies.get(Map.class);
	    assertEquals(2, mapDependencies.size());
	    
	   
	    
	    
	    @SuppressWarnings("unchecked")
		final Map<CalendarModel.Filter, Predicate<Day<?>>> filters =  (Map<Filter,  Predicate<Day<?>>>) mapDependencies.get(CalendarModel.Filter.class);
	    assertEquals(3, filters.size());
	  
	   
	    assertTrue(filters.get(CalendarModel.Filter.Vacation) instanceof  Predicate);
	    assertTrue(filters.get(CalendarModel.Filter.WorkingDate) instanceof  Predicate);
	    assertTrue(filters.get(CalendarModel.Filter.WorkingDay) instanceof  Predicate);
	    
		@SuppressWarnings("unchecked")
		final Map<String, DayGroup> dayGoups =  (Map<String, DayGroup>) mapDependencies.get(String.class);
		assertEquals(3, dayGoups.size());
		
		dayGoups.entrySet().stream().forEach(entry -> assertEquals(entry.getKey(),  entry.getValue().name()));
	 
	   
	    
	}

	private Map<Class<?>, Object> dependencies() {
		final Map<Class<?>, Object> dependencies = new HashMap<>();
		Arrays.asList(calendarModel.getClass().getDeclaredFields()).stream().filter( field -> ! Modifier.isStatic(field.getModifiers())).filter(field -> ! field.getType().equals(Optional.class)).forEach(field -> addDependencies(dependencies, field));
		return dependencies;
	}
	
	@Test
	void filterVacation() {
		
		final Predicate<Day<?>> filter = filterFromFields(Filter.Vacation);
	  
	   
	    final DayGroup dayGroup = Mockito.mock(DayGroup.class);
	    Mockito.when(dayGroup.name()).thenReturn(DayGroup.NON_WORKINGDAY_GROUP_NAME);
	   
		assertTrue(filter.test((Day<?>) new LocalDateDayImpl(dayGroup, LocalDate.now())));
		Mockito.when(dayGroup.name()).thenReturn(DayGroup.WORKINGDAY_GROUP_NAME);
		assertFalse(filter.test((Day<?>) new LocalDateDayImpl(dayGroup, LocalDate.now())));
		assertFalse(filter.test((Day<?>) new DayOfWeekImpl(dayGroup, DayOfWeek.MONDAY)));
		Mockito.when(dayGroup.name()).thenReturn(DayGroup.NON_WORKINGDAY_GROUP_NAME);
		assertFalse(filter.test((Day<?>) new DayOfWeekImpl(dayGroup, DayOfWeek.MONDAY)));
	  
	}
	
	@Test
	void filterWorkingDate() {
		
		final Predicate<Day<?>> filter = filterFromFields(Filter.WorkingDate);
	  
	   
	    final DayGroup dayGroup = Mockito.mock(DayGroup.class);
	    Mockito.when(dayGroup.name()).thenReturn(DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME);
	   
		assertTrue(filter.test((Day<?>) new LocalDateDayImpl(dayGroup, LocalDate.now())));
		Mockito.when(dayGroup.name()).thenReturn(DayGroup.WORKINGDAY_GROUP_NAME);
		assertFalse(filter.test((Day<?>) new LocalDateDayImpl(dayGroup, LocalDate.now())));
		assertFalse(filter.test((Day<?>) new DayOfWeekImpl(dayGroup, DayOfWeek.MONDAY)));
		Mockito.when(dayGroup.name()).thenReturn(DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME);
		assertFalse(filter.test((Day<?>) new DayOfWeekImpl(dayGroup, DayOfWeek.MONDAY)));
	  
	}
	
	@Test
	void filterWorkingDay() {
		
		final Predicate<Day<?>> filter = filterFromFields(Filter.WorkingDay);
	  
	   
	    final DayGroup dayGroup = Mockito.mock(DayGroup.class);
	    Mockito.when(dayGroup.name()).thenReturn(DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME);
	   
		assertTrue(filter.test((Day<?>) new DayOfWeekImpl(dayGroup, DayOfWeek.FRIDAY)));
		Mockito.when(dayGroup.name()).thenReturn(DayGroup.WORKINGDAY_GROUP_NAME);
		assertFalse(filter.test((Day<?>) new DayOfWeekImpl(dayGroup, DayOfWeek.FRIDAY)));
		assertFalse(filter.test((Day<?>) new LocalDateDayImpl(dayGroup, LocalDate.now())));
		Mockito.when(dayGroup.name()).thenReturn(DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME);
		assertFalse(filter.test((Day<?>) new LocalDateDayImpl(dayGroup, LocalDate.now())));
	  
	}

	private Predicate<Day<?>> filterFromFields(final Filter filter ) {
		final Map<Class<?>, Object> dependencies = dependencies();
	    final Map<?,?> mapDependencies =   (Map<?, ?>) dependencies.get(Map.class);
	    @SuppressWarnings("unchecked")
		final Map<CalendarModel.Filter, Predicate<Day<?>>> filters = (Map<Filter, Predicate<Day<?>>>) mapDependencies.get(CalendarModel.Filter.class);
	    assertNotNull(filters);
	    assertTrue(filters.containsKey(filter));
	    return filters.get(filter);
	}
	
	@Test
	void comparator() {
		
		final Comparator<? super Day<?>>    comparator =  calendarModel.comparator();
	   
	    assertNotNull(comparator);
	    final DayGroup dayGroup = Mockito.mock(DayGroup.class);
	    Mockito.when(dayGroup.name()).thenReturn(DayGroup.NON_WORKINGDAY_GROUP_NAME);
	    final Day<?> day1 = new LocalDateDayImpl(dayGroup, LocalDate.now());
	    final Day<?> day2 = new LocalDateDayImpl(dayGroup, LocalDate.now().minusDays(1));
	    
	    assertEquals(1, comparator.compare(day1,day2));
	    assertEquals(-1,comparator.compare(day2,day1));
	    assertEquals(0, comparator.compare(day1,day1));
	    assertEquals(0, comparator.compare(day2,day2));
	}


	private void addDependencies(final Map<Class<?>, Object> dependencies, Field field) {
		if( field.getType()== Map.class) { 
			map(dependencies).put(clazz(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]), ReflectionTestUtils.getField(calendarModel, field.getName()) );		
		} else {
			dependencies.put(field.getType(), ReflectionTestUtils.getField(calendarModel, field.getName()));
		}
	}


	private Class<?> clazz(java.lang.reflect.Type key) {
		try {
			return Class.forName(key.getTypeName());
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}


	@SuppressWarnings("unchecked")
	private Map<Class<?>, Object> map(final Map<Class<?>, Object> dependencies) {
		if(! dependencies.containsKey(Map.class)) {
			dependencies.put(Map.class, new HashMap<>());
		}
		return  (Map<Class<?>, Object>) dependencies.get(Map.class);
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
		assertNotNull(calendarModel.filter());
		assertTrue(calendarModel.filter().test((Day<?>) new LocalDateDayImpl(new DayGroupImpl(DayGroup.NON_WORKINGDAY_GROUP_NAME,0), LocalDate.now())));
		
		calendarModel.assign(CalendarModel.Filter.WorkingDate);
		assertNotNull(calendarModel.filter());
		
		assertTrue(calendarModel.filter().test((Day<?>) new LocalDateDayImpl(new DayGroupImpl(DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME,0), LocalDate.now())));
		Mockito.verify(subject).notifyObservers(Events.DatesChanged);
		
		calendarModel.assign(null);
		assertFalse(calendarModel.filter().test(Mockito.mock(Day.class)));
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
		final DayGroup dayGroup =  Mockito.mock(DayGroup.class);
		final LocalDate expectedDate = LocalDate.of(1968, 5, 28);
		Mockito.when(dayGroup.name()).thenReturn(DayGroup.NON_WORKINGDAY_GROUP_NAME);
		assertEquals(expectedDate.format(DateTimeFormatter.ofPattern(CalendarModelImpl.DATE_PATTERN)), calendarModel.convert(new LocalDateDayImpl(dayGroup, expectedDate), Year.of(1)));
	}
	@Test
	final void convertDayOfWeek() {
		final DayGroup dayGroup =  Mockito.mock(DayGroup.class);
		Mockito.when(dayGroup.name()).thenReturn(DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME);
		assertEquals(DayOfWeek.FRIDAY.getDisplayName(CalendarModelImpl.STYLE_DAY_OF_WEEK, Locale.GERMAN), calendarModel.convert(new DayOfWeekImpl(dayGroup, DayOfWeek.FRIDAY), Year.of(1)));
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
		final Day<?> result = calendarModel.dayOfWeek();
		assertEquals(DayOfWeek.FRIDAY, result.value());
	}
	
	@Test
	void dayOfWeekMissing() {
		assertThrows(IllegalArgumentException.class, () -> calendarModel.dayOfWeek());
	}
	@Test
	void filterDay() {
		final DayGroup dayGroup = Mockito.mock(DayGroup.class);
	
		Mockito.doReturn(DayGroup.NON_WORKINGDAY_GROUP_NAME).when(dayGroup).name();
		assertEquals(Filter.Vacation, calendarModel.filter((Day<?>) new LocalDateDayImpl(dayGroup, LocalDate.now())));
		
		Mockito.doReturn(DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME).when(dayGroup).name();
		assertEquals(Filter.WorkingDate, calendarModel.filter((Day<?>)new LocalDateDayImpl(dayGroup,  LocalDate.now())));
		
		assertEquals(Filter.WorkingDay, calendarModel.filter((Day<?>)new DayOfWeekImpl(dayGroup, DayOfWeek.FRIDAY)));
		
	}
	@Test
	void filterDayInvalid() {
		final DayGroup dayGroup = Mockito.mock(DayGroup.class);
		Mockito.doReturn("Unkown").when(dayGroup).name();
		assertThrows(IllegalArgumentException.class, () -> calendarModel.filter((Day<?>) new LocalDateDayImpl(dayGroup, LocalDate.now())));
	}
	@Test
	void events() {
		assertEquals(3, Events.values().length);
	}
	
	@Test
	void validationErrors() {
		assertEquals(6, ValidationErrors.values().length);
	}
	@Test
	void filterEnum() {
		assertEquals(3, Filter.values().length);
		assertEquals(DayGroup.NON_WORKINGDAY_GROUP_NAME, Filter.Vacation.group());
		assertEquals(LocalDateDayImpl.class,  Filter.Vacation.type());
		
		assertEquals(DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME, Filter.WorkingDate.group());
		assertEquals(LocalDateDayImpl.class,  Filter.WorkingDate.type());
		
		assertEquals(DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME, Filter.WorkingDay.group());
		assertEquals(DayOfWeekImpl.class,  Filter.WorkingDay.type());
	}
	
}
