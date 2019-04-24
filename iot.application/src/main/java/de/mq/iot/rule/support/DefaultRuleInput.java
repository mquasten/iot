package de.mq.iot.rule.support;

import java.time.LocalTime;



class DefaultRuleInput implements ValidFieldValues {
	
	private LocalTime workingdayAlarmTime;
	
	private LocalTime holidayAlarmTime;
	
	private Boolean updateMode=false;
	
	private Boolean testMode=false;
	
	

	DefaultRuleInput() {
		
	}
	
	DefaultRuleInput(final LocalTime workingdayAlarmTime, final LocalTime holidayAlarmTime) {
		this.workingdayAlarmTime=workingdayAlarmTime;
		this.holidayAlarmTime=holidayAlarmTime;
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
	
	final LocalTime minSunDown() {
		return LocalTime.MIDNIGHT;
	}
}
