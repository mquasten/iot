package de.mq.iot.calendar.support;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.util.StringUtils;



import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.Authority;
import de.mq.iot.calendar.Specialday;
import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;




class CalendarModelImpl  implements CalendarModel  {

	
	static final TextStyle STYLE_DAY_OF_WEEK = TextStyle.FULL_STANDALONE;


	static final String DATE_PATTERN = "dd.MM.uuuu";


	private final Subject<CalendarModel.Events, CalendarModel> subject;


	private Optional<LocalDate> from = Optional.empty();
	
	private final Map<CalendarModel.Filter, Collection<Specialday.Type>> filters = new HashMap<>();

	private CalendarModel.Filter filter = CalendarModel.Filter.Vacation;
	

	private Optional<LocalDate> to = Optional.empty();
	
	private Optional<DayOfWeek> dayOfWeek = Optional.empty();
	
	private Collection<Specialday.Type> typesWithDayOfWeek = Arrays.asList(Specialday.Type.SpecialWorkingDay, Specialday.Type.Weekend);


	CalendarModelImpl(final Subject<Events, CalendarModel> subject) {
		this.subject = subject;
	
		
		filters.put(CalendarModel.Filter.Vacation, Arrays.asList(Specialday.Type.Vacation));
		filters.put(CalendarModel.Filter.WorkingDate,  Arrays.asList(Specialday.Type.SpecialWorkingDate) );
		filters.put(CalendarModel.Filter.WorkingDay, Arrays.asList(Specialday.Type.SpecialWorkingDay));

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
			LocalDate.of(Integer.valueOf(cols[2]), Integer.valueOf(cols[1]), Integer.valueOf(cols[0]));
			
			if(Integer.valueOf(cols[2]) < 2000 ) {
				return ValidationErrors.Invalid;
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
	public Collection<Specialday.Type> filter() {
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
	public final String convert(final Specialday specialday, final Year year) {
		if( typesWithDayOfWeek.contains(specialday.type())){
			final DayOfWeek dayOfWeek = specialday.dayOfWeek();
			return dayOfWeek.getDisplayName(STYLE_DAY_OF_WEEK, locale());
			
		}
		
		final LocalDate  date = specialday.date(year.getValue());
		
		return date.format(DateTimeFormatter.ofPattern( DATE_PATTERN, locale()));
	}
	
	@Override
	public final Collection<DayOfWeek> daysOfWeek() {
		return IntStream.range(1, 6).mapToObj(DayOfWeek::of).collect(Collectors.toList());	
	}
	@Override
	public final Specialday dayOfWeek() {
		dayOfWeek.orElseThrow(() -> new IllegalArgumentException("DayOfWeek is missing."));
		return new SpecialdayImpl(this.dayOfWeek.get());
	}
	

}
