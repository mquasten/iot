package de.mq.iot.calendar.support;



import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.calendar.support.SpecialdayImpl.Type;


@Service
class SpecialdayServiceImpl implements SpecialdayService {
	
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
