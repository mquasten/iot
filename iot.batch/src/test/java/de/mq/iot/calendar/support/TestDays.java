package de.mq.iot.calendar.support;

import java.time.LocalDate;
import java.util.Collection;

import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;


public interface TestDays {
	
	public static Day<LocalDate> gaussDay() {
		return new GaussDayImpl(new DayGroupImpl(DayGroup.NON_WORKINGDAY_GROUP_NAME,2),  -2);
		
	}
	
	public static  Collection<DayGroup> dayGroups() {
		final DayConfiguration dayConfiguration = new DayConfiguration();	
		return dayConfiguration.dayGroups();
	}

}
