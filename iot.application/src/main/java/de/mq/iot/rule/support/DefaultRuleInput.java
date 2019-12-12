package de.mq.iot.rule.support;

import java.time.LocalTime;

import java.util.HashMap;
import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import de.mq.iot.calendar.SpecialdayService.DayType;

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
	
	final LocalTime alarmTime(final DayType dayType) {
		Assert.notNull(dayType, "DayType is required.");
		final Map<DayType,LocalTime> alarmTimes = new HashMap<>();
		alarmTimes.put(DayType.NonWorkingDay, holidayAlarmTime);
		alarmTimes.put(DayType.WorkingDay, workingdayAlarmTime);
		alarmTimes.put(DayType.SpecialWorkingDay, specialWorkingdayAlarmTime);
		Assert.isTrue(alarmTimes.containsKey(dayType), "Alarmtime not found for: " + dayType.name());
		return alarmTimes.get(dayType);
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
