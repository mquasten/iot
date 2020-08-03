package de.mq.iot.calendar.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import de.mq.iot.calendar.DayGroup;

@Configuration
class DayConfiguration {
	
	static final DayGroup defaultDayGroup = new DayGroupImpl(DayGroup.WORKINGDAY_GROUP_NAME, 2);

	@Bean()
	DayGroup defaultDayGroup() {
		return defaultDayGroup;
	}

}
