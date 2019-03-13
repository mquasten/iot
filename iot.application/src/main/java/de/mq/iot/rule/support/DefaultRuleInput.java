package de.mq.iot.rule.support;

import java.time.LocalTime;



class DefaultRuleInput {
	
	private LocalTime workingdayAlarmTime;
	
	private LocalTime holidayAlarmTime;
	
	private Boolean updateMode=false;
	
	private Boolean testMode=false;
	
	

	DefaultRuleInput() {
		workingdayAlarmTime=LocalTime.MIDNIGHT;
		holidayAlarmTime=LocalTime.MIDNIGHT;
	}
	
	DefaultRuleInput(final LocalTime workingdayAlarmTime, final LocalTime holidayAlarmTime) {
		this.workingdayAlarmTime=workingdayAlarmTime;
		this.holidayAlarmTime=holidayAlarmTime;
	}
	
	void useUpdateMode() {
		this.updateMode=true;
	}
	
	void useTestMode() {
		this.testMode=true;
	}
	
	LocalTime workingdayAlarmTime() {
		return workingdayAlarmTime;
	}

	LocalTime holidayAlarmTime() {
		return holidayAlarmTime;
		
	}

	boolean isUpdateMode() {
		return updateMode;
	}

	boolean isTestMode() {
		return testMode;
	}
	
	

}
