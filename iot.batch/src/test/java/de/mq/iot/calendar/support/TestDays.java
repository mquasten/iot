package de.mq.iot.calendar.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Collection;

import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;


public interface TestDays {
	
	public static Day<LocalDate> gaussDay() {
		return new GaussDayImpl(new DayGroupImpl(DayGroup.NON_WORKINGDAY_GROUP_NAME,2),  -2);
		
	}
	
	public static Day<LocalDate> fixedDay() {
		return new FixedDayImpl(new DayGroupImpl(DayGroup.NON_WORKINGDAY_GROUP_NAME,2),  MonthDay.of(5, 28));
		
	}
	

	public static Day<DayOfWeek> dayOfWeek() {
		return new DayOfWeekImpl(new DayGroupImpl(DayGroup.NON_WORKINGDAY_GROUP_NAME,2), DayOfWeek.SUNDAY);
		
	}
	
	public static Day<LocalDate> localDateDay() {
		return new LocalDateDayImpl(new DayGroupImpl(DayGroup.NON_WORKINGDAY_GROUP_NAME,2), LocalDate.of(1968, 5, 28));
		
	}
	
	public static  Collection<DayGroup> dayGroups() {
		final DayConfiguration dayConfiguration = new DayConfiguration();	
		return dayConfiguration.dayGroups();
	}

}
