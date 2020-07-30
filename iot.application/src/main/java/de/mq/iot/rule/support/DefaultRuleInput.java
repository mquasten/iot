package de.mq.iot.rule.support;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import de.mq.iot.calendar.DayGroup;

class DefaultRuleInput implements ValidFieldValues {
	
	private LocalTime workingdayAlarmTime;
	
	private LocalTime holidayAlarmTime;
	
	private LocalTime specialWorkingdayAlarmTime;
	
	private Boolean updateMode=false;
	
	private Boolean testMode=false;
	
	@Nullable
	private LocalTime minEventTime=LocalTime.now();
	

	@Nullable
	private LocalTime minSunDownTime=LocalTime.MIDNIGHT;

	DefaultRuleInput() {
		
	}
	
	DefaultRuleInput(final LocalTime workingdayAlarmTime, final LocalTime holidayAlarmTime, final LocalTime specialWorkingdayAlarmTime, final LocalTime minSunDownTime) {
		this.workingdayAlarmTime=workingdayAlarmTime;
		this.holidayAlarmTime=holidayAlarmTime;
		this.specialWorkingdayAlarmTime=specialWorkingdayAlarmTime;
		this.minSunDownTime=minSunDownTime;
	}
	
	final void useUpdateMode() {
		this.updateMode=true;
	}
	
	final void useTestMode() {
		this.testMode=true;
	}
	
	final LocalTime alarmTime(final DayGroup dayGroup) {
		Assert.notNull(dayGroup, "DayGroup is required.");
		final Map<String,LocalTime> alarmTimes = new HashMap<>();
		alarmTimes.put(DayGroup.NON_WORKINGDAY_GROUP_NAME, holidayAlarmTime);
		alarmTimes.put(DayGroup.WORKINGDAY_GROUP_NAME, workingdayAlarmTime);
		alarmTimes.put(DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME, specialWorkingdayAlarmTime);
		Assert.isTrue(alarmTimes.containsKey(dayGroup.name()), "Alarmtime not found for: " + dayGroup.name());
		return alarmTimes.get(dayGroup.name());
	}

	final boolean isUpdateMode() {
		return updateMode;
	}

	final boolean isTestMode() {
		return testMode;
	}
	
	final LocalTime minSunDownTime() {
		return minSunDownTime;
	}
	
	final  LocalTime minEventTime() {
		return minEventTime;
	}
}
