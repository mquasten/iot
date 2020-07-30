package de.mq.iot.calendar;

public interface DayGroup {
	
	
	
	static final String NON_WORKINGDAY_GROUP_NAME ="NonWorkingDay";
	static final String WORKINGDAY_GROUP_NAME ="WorkingDay";
	
	static final String  SPECIAL_WORKINGDAY_GROUP_NAME="SpecialWorkingDay";
	String name();
	int priority();

}
