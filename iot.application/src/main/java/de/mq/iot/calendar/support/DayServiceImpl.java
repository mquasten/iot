package de.mq.iot.calendar.support;

import java.time.Duration;
import java.time.LocalDate;

import java.util.Collections;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;


@Service
class DayServiceImpl {
	
	
	
	private final DayRepository dayRepository;
	private final Duration duration;
	private final DayGroup defaultDayGroup;
	
	@Autowired
	DayServiceImpl(final DayRepository dayRepository,  final DayGroup defaultDayGroup, @Value("${mongo.timeout:500}") final Integer timeout) {
		this.dayRepository=dayRepository;
		this.defaultDayGroup=defaultDayGroup;
		this.duration=Duration.ofMillis(timeout);
	}
	
	DayGroup dayGroup(final LocalDate date) {
		final List<Day<?>> days =  dayRepository.findAll().collectList().block(duration);
		Collections.sort(days);
		if( CollectionUtils.isEmpty(days) ) {
			return defaultDayGroup;
		}
		return days.get(0).dayGroup();
		
	}
	
	

}
