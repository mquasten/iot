package de.mq.iot.rule.support;

import java.time.LocalTime;

import org.springframework.lang.Nullable;



class DefaultRuleInput implements ValidFieldValues {
	
	private LocalTime workingdayAlarmTime;
	
	private LocalTime holidayAlarmTime;
	
	private Boolean updateMode=false;
	
	private Boolean testMode=false;
	
	@Nullable
	private LocalTime minSunDownTime=LocalTime.MIDNIGHT;

	DefaultRuleInput() {
		
	}
	
	DefaultRuleInput(final LocalTime workingdayAlarmTime, final LocalTime holidayAlarmTime, final LocalTime minSunDownTime) {
		this.workingdayAlarmTime=workingdayAlarmTime;
		this.holidayAlarmTime=holidayAlarmTime;
		this.minSunDownTime=minSunDownTime;
	}
	
	final void useUpdateMode() {
		this.updateMode=true;
	}
	
	final void useTestMode() {
		this.testMode=true;
	}
	
	final LocalTime workingdayAlarmTime() {
		return workingdayAlarmTime;
	}

	final LocalTime holidayAlarmTime() {
		return holidayAlarmTime;
		
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
}
