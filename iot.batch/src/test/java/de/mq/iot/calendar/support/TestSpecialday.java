package de.mq.iot.calendar.support;

import java.time.LocalDate;

import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;


public interface TestSpecialday {
	
	public static Day<LocalDate> gaussDay() {
		return new GaussDayImpl(new DayGroupImpl(DayGroup.NON_WORKINGDAY_GROUP_NAME,2),  -2);
		
	}

}
