package de.mq.iot.calendar.support;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.Authority;
import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;
import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;




class CalendarModelImpl  implements CalendarModel  {

	
	static final TextStyle STYLE_DAY_OF_WEEK = TextStyle.FULL_STANDALONE;


	static final String DATE_PATTERN = "dd.MM.uuuu";


	private final Subject<CalendarModel.Events, CalendarModel> subject;


	private Optional<LocalDate> from = Optional.empty();
	
	private final Map<CalendarModel.Filter, Predicate<Day<?>>> filters = new HashMap<>();

	private CalendarModel.Filter filter = CalendarModel.Filter.Vacation;
	

	private Optional<LocalDate> to = Optional.empty();
	
	private Optional<DayOfWeek> dayOfWeek = Optional.empty();
	

	@SuppressWarnings("unchecked")
	private final  Comparator<Day<?>> comparator =  ((d1, d2) ->  ( (Comparable<Object>)  d1.value()).compareTo( d2.value()));
	
	private final Map<String, DayGroup> dayGroups = new HashMap<>();

	CalendarModelImpl(final Subject<Events, CalendarModel> subject, Collection<DayGroup> dayGroups) {
		this.subject = subject;
	
		this.dayGroups.putAll(dayGroups.stream().collect(Collectors.toMap(DayGroup::name, dayGroup -> dayGroup)));
		
		filters.put(CalendarModel.Filter.Vacation, day-> day.dayGroup().name().equals(DayGroup.NON_WORKINGDAY_GROUP_NAME)&&day.getClass().equals(LocalDateDayImpl.class));
		filters.put(CalendarModel.Filter.WorkingDate,  day -> day.dayGroup().name().equals(DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME)&&day.getClass().equals(LocalDateDayImpl.class) );
		filters.put(CalendarModel.Filter.WorkingDay, day -> day.dayGroup().name().equals(DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME)&&day.getClass().equals(DayOfWeekImpl.class) );

	
		
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


	@Override
	public ValidationErrors validateFrom(final String date) {
		this.from=Optional.empty();
		notifyObservers(Events.ValuesChanged);
		
		return validateDate(date);
	}
	
	
	@Override
	public ValidationErrors validateTo(final String date) {
		this.to=Optional.empty();
		notifyObservers(Events.ValuesChanged);
		
		return validateDate(date);
	}

	@Override
	public ValidationErrors validateDayofWeek(final DayOfWeek dayOfWeek) {
		this.dayOfWeek=Optional.empty();
		notifyObservers(Events.ValuesChanged);
		if(dayOfWeek==null) {
			return ValidationErrors.Mandatory;
		}
		return ValidationErrors.Ok;
	}

	
	@Override
	public ValidationErrors vaidate(final int maxDays) {
		if( ! valid()) {
			return ValidationErrors.Invalid;
		}	
		if(isDayOfWeek()) {
			return ValidationErrors.Ok;
		}
		if( to.get().isBefore(from.get())) {
			return ValidationErrors.FromBeforeTo;
		}
		
		if( ChronoUnit.DAYS.between(from.get(), to.get()) > maxDays ) {
			return ValidationErrors.RangeSize;
		}
		
		return ValidationErrors.Ok;
	}

	private ValidationErrors validateDate(final String date) {
		
		if ( ! StringUtils.hasText(date) ) {
			return ValidationErrors.Mandatory;
		}
		final String[] cols = date.trim().split("[.]");
		
		if ( cols.length != 3) {
			return  ValidationErrors.Invalid;
		}
		try {
			final LocalDate localDate = LocalDate.of(Integer.valueOf(cols[2]), Integer.valueOf(cols[1]), Integer.valueOf(cols[0]));
			
			if( localDate.isBefore(LocalDate.now()) ) {
				return ValidationErrors.InPast;
			}

		} catch ( final Exception ex) {
			return  ValidationErrors.Invalid;
		
		}
		return ValidationErrors.Ok;
	}
	
	@Override
	public void assignFrom(final String from ) {
		if( validateFrom(from) == ValidationErrors.Ok) {
			
			final String[] cols = from.trim().split("[.]");
			this.from=Optional.of(LocalDate.of(Integer.valueOf(cols[2]), Integer.valueOf(cols[1]), Integer.valueOf(cols[0])));
			notifyObservers(Events.ValuesChanged);
			return;
		}
		this.from=Optional.empty();
		notifyObservers(Events.ValuesChanged);
		
	}
	@Override
	public void assignTo(final String to ) {
		if( validateTo(to) == ValidationErrors.Ok) {
			final String[] cols = to.trim().split("[.]");
			this.to=Optional.of(LocalDate.of(Integer.valueOf(cols[2]), Integer.valueOf(cols[1]), Integer.valueOf(cols[0])));
			notifyObservers(Events.ValuesChanged);
			
			return;
		}
		this.from=Optional.empty();
		notifyObservers(Events.ValuesChanged);
	}
	
	@Override
	public void assignDayOfWeek(final DayOfWeek dayOfWeek ) {
		if( validateDayofWeek(dayOfWeek) == ValidationErrors.Ok) {
			
			this.dayOfWeek=Optional.of(dayOfWeek);
			notifyObservers(Events.ValuesChanged);
			
			return;
		}
		this.from=Optional.empty();
		notifyObservers(Events.ValuesChanged);
	}
	
	@Override
	public boolean valid() {
		if (filter == null) {
			return false;
		}
		if( isDayOfWeek()){
			return dayOfWeek.isPresent();
		}
		
		return this.to.isPresent() && this.from.isPresent();
	}

	@Override
	public LocalDate from() {
		return from.orElseThrow(() -> new IllegalArgumentException("FromDate is missing."));
	}

	@Override
	public LocalDate to() {
		return to.orElseThrow(() -> new IllegalArgumentException("ToDate is missing."));
	}
	
	
	@Override
	public Predicate<Day<?>> filter() {
		if( filter == null) {
			return day -> false;
		}
		return filters.get(filter);
	}
	
	@Override
	public void assign(CalendarModel.Filter filter) {
		this.filter=filter;
		notifyObservers(Events.DatesChanged);
		
	}
	
	@Override
	public final  boolean isDayOfWeek() {
		return this.filter==Filter.WorkingDay;
	}
	
	@Override
	public final boolean isSpecialWorkingDate() {
	 return  Filter.WorkingDate == this.filter;
	}


	@Override
	public boolean isChangeCalendarAllowed() {
		Optional<Authentication> authentication = subject.currentUser();
		if( ! authentication.isPresent() ) {
			return false;	
		}
		return authentication.get().hasRole(Authority.Calendar);
		
	}
	
	@Override
	public final String convert(final Day<?> day, final Year year) {
		
		if (day.value() instanceof DayOfWeek) {
			final DayOfWeek dayOfWeek = (DayOfWeek) day.value();
			return dayOfWeek.getDisplayName(STYLE_DAY_OF_WEEK, locale());
			
		}
		
		
		final LocalDate  date = (LocalDate) day.value();
		
		return date.format(DateTimeFormatter.ofPattern( DATE_PATTERN, locale()));
	}
	
	@Override
	public final Collection<DayOfWeek> daysOfWeek() {
		return IntStream.range(1, 6).mapToObj(DayOfWeek::of).collect(Collectors.toList());	
	}
	@Override
	public final Day<?> dayOfWeek() {
		dayOfWeek.orElseThrow(() -> new IllegalArgumentException("DayOfWeek is missing."));
		return new DayOfWeekImpl(dayGroup(), this.dayOfWeek.get());
	}
	
	@Override
	public final DayGroup dayGroup() {
		Assert.notNull(filter, "Filter is required.");
		
		Assert.isTrue(dayGroups.containsKey(filter.group()), String.format("DayGroup %s not aware", filter.group()));
		return dayGroups.get(filter.group());
	}
	
	@Override
	public Filter filter (final Day<?> day) {
		final Map<String, Filter> filters =  Arrays.asList(Filter.values()).stream().collect(Collectors.toMap(value -> key(value.type(), value.group()), value -> value));
		Assert.notNull(day.dayGroup() , "DayGroup is mandatory." );
		Assert.notNull(day.dayGroup().name() , "Name is mandatory." );
		
		final String key= key(day.getClass(), day.dayGroup().name());
		Assert.isTrue(filters.containsKey(key), String.format("Invalid Combination %s %s", day.getClass().getSimpleName() , day.dayGroup().name()));
		return filters.get(key);
	}
	
	
	private String key(final Class<?> clazz , final String group) {
		return clazz.getSimpleName()+ "-"  + group;
	}

	@Override
	public Comparator<Day<?>> comparator() {
		return   comparator;
	}
	

}
