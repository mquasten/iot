package de.mq.iot.calendar.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import de.mq.iot.calendar.DayGroup;
import de.mq.iot.calendar.SpecialdayService.DayType;

@Configuration
class DayConfiguration {
	
	static final DayGroup defaultDayGroup = new DayGroupImpl(DayType.WorkingDay.name(), 2);

	@Bean()
	DayGroup defaultDayGroup() {
		return defaultDayGroup;
	}

}
