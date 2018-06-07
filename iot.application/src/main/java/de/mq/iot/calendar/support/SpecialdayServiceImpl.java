package de.mq.iot.calendar.support;

import java.time.Duration;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.calendar.support.SpecialdayImpl.Type;



class SpecialdayServiceImpl implements SpecialdayService {
	
	private SpecialdayRepository specialdaysRepository;
	private Duration duration;
	SpecialdayServiceImpl(final SpecialdayRepository specialdaysRepository, @Value("${mongo.timeout:500}") final Integer timeout) {
		this.specialdaysRepository=specialdaysRepository;
		this.duration=Duration.ofMillis(timeout);
	}
	
	/* (non-Javadoc)
	 * @see de.mq.iot.calendar.support.SpecialdaysService#save(de.mq.iot.calendar.Specialday)
	 */
	@Override
	public final void save(final Specialday specialday) {
		specialdaysRepository.save(specialday).block();
	}
	
	/* (non-Javadoc)
	 * @see de.mq.iot.calendar.support.SpecialdaysService#specialdays(java.time.Year)
	 */
	@Override
	public final Collection<Specialday> specialdays(final Year year) {
		final Collection<Specialday> results = new ArrayList<>();
		results.addAll(specialdaysRepository.findByType(Type.Fix).collectList().block(duration));
		results.addAll(specialdaysRepository.findByType(Type.Fix).collectList().block(duration));
		return Collections.unmodifiableCollection(results);
		
	}

}
