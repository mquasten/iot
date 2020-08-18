package de.mq.iot.calendar.support;

import java.util.Collection;

import de.mq.iot.calendar.DayGroup;


public interface TestDays {
	
	
	public static  Collection<DayGroup> dayGroups() {
		final DayConfiguration dayConfiguration = new DayConfiguration();	
		return dayConfiguration.dayGroups();
	}

}
