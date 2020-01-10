package de.mq.iot.calendar.support;



import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.calendar.Specialday.Type;


@Service
class SpecialdayServiceImpl implements SpecialdayService {
	
	static final String VACATION_OR_PUBLIC_HOLIDAY_INFO = "Vacation or public holiday";
	static final String DAY_TYPE_INFO_FORMAT = "%s: %s";
	private SpecialdayRepository specialdaysRepository;
	private Duration duration;
	
	@Autowired
	SpecialdayServiceImpl(final SpecialdayRepository specialdaysRepository, @Value("${mongo.timeout:500}") final Integer timeout) {
		this.specialdaysRepository=specialdaysRepository;
		this.duration=Duration.ofMillis(timeout);
	}
	
	/* (non-Javadoc)
	 * @see de.mq.iot.calendar.support.SpecialdaysService#save(de.mq.iot.calendar.Specialday)
	 */
	@Override
	public final void save(final Specialday specialday) {
		specialdaysRepository.save(specialday).block(duration);
	}
	
	
	@Override
	public final void delete(final Specialday specialday) {
		specialdaysRepository.delete(specialday).block(duration);
	}
	
	/* (non-Javadoc)
	 * @see de.mq.iot.calendar.support.SpecialdaysService#specialdays(java.time.Year)
	 */
	@Override
	public final Collection<Specialday> specialdays(final Year year) {
		final Collection<Specialday> results = new ArrayList<>();
		results.addAll(specialdaysRepository.findByTypeIn(Arrays.asList(Type.Fix, Type.Gauss)).collectList().block(duration));	
		results.addAll(specialdaysRepository.findByTypeAndYear(Type.Vacation, year.getValue()).collectList().block(duration));
		return Collections.unmodifiableCollection(results);
		
	}
	
	
	@Override
	public Collection<Specialday> specialdays(final Collection<Type> types){
		
		Assert.notEmpty(types, "At least one type required.");
		
		final List<Specialday> results = new ArrayList<>();
		
		types.stream().filter(type -> type.isWithYear()).forEach(type  -> results.addAll(specialdaysRepository.findByTypeAndYear(type, Year.now().getValue()).collectList().block(duration)));
		
		
		
		final List<Type> typesWithOutYear = types.stream().filter(type -> !type.isWithYear()).collect(Collectors.toList());
		
		if ( CollectionUtils.isEmpty(typesWithOutYear)){
			
			Collections.sort(results);
			return Collections.unmodifiableCollection(results);
		}
		
		results.addAll(specialdaysRepository.findByTypeIn(typesWithOutYear).collectList().block(duration));	
		Collections.sort(results);
		return Collections.unmodifiableCollection(results);
	
		
	}
	
	
	
	@Override
	public Entry<DayType,String> typeOfDay(final LocalDate date) {
		
		final Collection<DayOfWeek> weekendDays = readDayOfWeek(Type.Weekend);
		if (weekendDays.contains(date.getDayOfWeek())) {
			
			return typeOfDayResult(DayType.NonWorkingDay, SpecialdayImpl.Type.Weekend ,date.getDayOfWeek());
		}
		final Collection<LocalDate> specialdates = specialdays(Year.from(date)).stream().map(specialday -> specialday.date(date.getYear())).collect(Collectors.toSet());
		
		if (specialdates.contains(date)) {
			return typeOfDayResult(DayType.NonWorkingDay,  VACATION_OR_PUBLIC_HOLIDAY_INFO,   date);
			
		}
		
		final Collection<DayOfWeek> specialWorkingDays = readDayOfWeek(Type.SpecialWorkingDay);
		if (specialWorkingDays.contains(date.getDayOfWeek())) {
			return typeOfDayResult(DayType.SpecialWorkingDay, SpecialdayImpl.Type.SpecialWorkingDay, date.getDayOfWeek());
		}
		
		final Collection<LocalDate> specialWorkingDates=specialdaysRepository.findByTypeIn(Arrays.asList(Type.SpecialWorkingDate)).collectList().block(duration).stream().map(specialday -> specialday.date(1)).collect(Collectors.toSet());
		
		if (specialWorkingDates.contains(date)) {
			return typeOfDayResult(DayType.SpecialWorkingDay, SpecialdayImpl.Type.SpecialWorkingDate, date);
		}
		
		return new AbstractMap.SimpleImmutableEntry<>(DayType.WorkingDay, DayType.WorkingDay.name());
	}

	private SimpleImmutableEntry<DayType, String> typeOfDayResult(final DayType dayType, final Object type, final Object info) {
	
		return new AbstractMap.SimpleImmutableEntry<>(dayType, String.format(DAY_TYPE_INFO_FORMAT, type, info) );
		
	}

	private Collection<DayOfWeek> readDayOfWeek(final Type type) {
		
		
		return specialdaysRepository.findByTypeIn(Arrays.asList(type)).collectList().block(duration).stream().map(specialday -> specialday.dayOfWeek()).collect(Collectors.toSet());
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.calendar.SpecialdayService#vacation(java.time.LocalDate, java.time.LocalDate)
	 */
	@Override
	public final Collection<Specialday>vacation(final LocalDate begin, final LocalDate end) {
		Assert.isTrue(!begin.isAfter(end), "Begin should be before or equals end.");
		final Collection<LocalDate> publicHolidays = new HashSet<>();
		final Collection<Specialday> specialdays = specialdaysRepository.findByTypeIn(Arrays.asList(Type.Fix, Type.Gauss)).collectList().block(duration);
		publicHolidays.addAll(specialdays.stream().map(specialday -> specialday.date(begin.getYear())).collect(Collectors.toList()));
		if( begin.getYear() != end.getYear()) {
			publicHolidays.addAll(specialdays.stream().map(specialday -> specialday.date(end.getYear())).collect(Collectors.toList()));
		}
		publicHolidays.addAll(specialdays.stream().map(specialday -> specialday.date(end.getYear())).collect(Collectors.toList()));
		
	
		final long daysOffset = ChronoUnit.DAYS.between(begin, end);
		final Collection<Specialday> results = LongStream.rangeClosed(0, daysOffset).mapToObj(i -> new SpecialdayImpl(begin.plusDays(i))).filter(specialday -> filterSpecialDay(publicHolidays, specialday)).collect(Collectors.toList());
		return results;
		
	}

	private boolean filterSpecialDay(final Collection<LocalDate> publicHolidays, final Specialday specialday) {
		final LocalDate localDate = specialday.date(1);
		
		if (localDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
			return false;
		}
		
		if (localDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
			return false;
		}
		if( publicHolidays.contains(localDate) ) {
			return false;
		}
		return true;
	}

	@Override
	public Collection<Specialday> specialdays() {
		return specialdaysRepository.findByTypeIn(Arrays.asList(Type.values())).collectList().block(duration);
	}
	
	@Override
	public Collection<Specialday> vacationsBeforeEquals(final LocalDate date) {
	 return specialdaysRepository.findByTypeIn(Arrays.asList(Type.Vacation)).collectList().block(duration).stream().filter(sd -> ! sd.date(1).isAfter(date)).collect(Collectors.toList());
		
	}

}
