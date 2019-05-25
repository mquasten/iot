package de.mq.iot.rule.support;

class EndOfDayRuleInput implements ValidFieldValues {
	
	
	EndOfDayRuleInput() {
		
	}
	
	EndOfDayRuleInput(final Integer firstIp, final Integer maxIpCount, final Integer daysBack) {
		this.firstIp = firstIp;
		this.maxIpCount = maxIpCount;
		this.daysBack = daysBack;
	}
	
	private Integer firstIp = 100;
	
	private Integer maxIpCount = 25;
	
	private Integer daysBack = 30;
	
	private Boolean testMode=false;
	
	
	
	Integer firstIp() {
		return firstIp;
	}

	Integer maxIpCount() {
		return maxIpCount;
	}

	Integer daysBack() {
		return daysBack;
	}

	final boolean isTestMode() {
		return testMode;
	}
	
	

}
