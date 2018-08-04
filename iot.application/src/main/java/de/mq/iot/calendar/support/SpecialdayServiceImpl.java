package de.mq.iot.calendar.support;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
		return Arrays.asList();
		
	}

}
