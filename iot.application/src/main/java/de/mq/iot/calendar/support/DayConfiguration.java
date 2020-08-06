package de.mq.iot.calendar.support;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import de.mq.iot.calendar.DayGroup;

@Configuration
class DayConfiguration {
	
	static final DayGroup defaultDayGroup = new DayGroupImpl(DayGroup.WORKINGDAY_GROUP_NAME, 2);
	
	private final Collection<DayGroup> dayGroups = Arrays.asList(defaultDayGroup, new DayGroupImpl(DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME, 1), new DayGroupImpl(DayGroup.NON_WORKINGDAY_GROUP_NAME, 1) );

	@Bean()
	DayGroup defaultDayGroup() {
		return defaultDayGroup;
	}
	
	@Bean()
    Collection<DayGroup> dayGroups() {
    	return dayGroups;
    }

}
