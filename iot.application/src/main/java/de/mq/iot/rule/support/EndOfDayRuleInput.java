package de.mq.iot.rule.support;

import java.time.LocalDate;
import java.time.temporal.ValueRange;


class EndOfDayRuleInput implements ValidFieldValues {
	
	
	static final int MAX_IP_COUNT_DEFAULT = 25;

	static final int FIRST_IP_DEFAULT = 100;

	static final Integer DAYS_BACK_DEFAULT = 30;



	EndOfDayRuleInput() {
		
	}
	
	EndOfDayRuleInput(final Integer firstIp, final Integer maxIpCount, final Integer daysBack,final boolean updateMode, final boolean testMode) {
		this.firstIp = firstIp;
		this.maxIpCount = maxIpCount;
		this.daysBack = daysBack;
		this.updateMode=updateMode;
		this.testMode=testMode;
	}
	
	private Integer firstIp = FIRST_IP_DEFAULT;
	
	private Integer maxIpCount = MAX_IP_COUNT_DEFAULT;
	
	private Integer daysBack = DAYS_BACK_DEFAULT;
	
	private Boolean updateMode=false;
	
	private Boolean testMode=false;
	
	
	
	final ValueRange ipRange() {
		
		return ValueRange.of(range(firstIp,255), range(firstIp+maxIpCount,256));
		
		
	}
	
	private int range(final int value, final int end){
		return value <=end ? value : end;
	}

	

	final boolean isTestMode() {
		return testMode;
	}
	
	final LocalDate minDeletiondate() {
		final int offset = updateMode ? 1: 0;
		
		return LocalDate.now().minusDays(offset).minusDays(daysBack);
		
	}

}
