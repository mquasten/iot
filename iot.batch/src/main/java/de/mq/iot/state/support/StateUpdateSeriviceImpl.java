package de.mq.iot.state.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.util.Arrays;
import java.util.Collection;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mq.iot.calendar.SpecialdayService;

@Service
class StateUpdateSeriviceImpl {

	private final SpecialdayService specialdayService;
	
	@Autowired
	StateUpdateSeriviceImpl(final SpecialdayService specialdayService) {
		
		this.specialdayService = specialdayService;
	}

	public void update(final LocalDate date) {
		System.out.println("update");
		
		
		System.out.println(isWorkingsday(date));
	
		
	}

	private  boolean  isWorkingsday(final LocalDate date) {
		Collection<DayOfWeek> weekend = Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
		if ( weekend.contains(date.getDayOfWeek())) {
			return false;
		}
		
		final Collection<LocalDate> specialdates = specialdayService.specialdays(Year.from(date)).stream().map(specialday -> specialday.date(date.getYear())).collect(Collectors.toSet());
		if(specialdates.contains(date)) {
			return false;
		}
	    
		return true;
	}
	
}
