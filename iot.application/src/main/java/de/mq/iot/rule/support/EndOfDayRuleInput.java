package de.mq.iot.rule.support;

import java.util.stream.IntStream;

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
	
	
	
	final IntStream ipRange() {
		
		final int end = firstIp+maxIpCount <= 254 ?  firstIp+maxIpCount : 254;
		
		return IntStream.range(firstIp, end);
		
	}

	final Integer daysBack() {
		return daysBack;
	}

	final boolean isTestMode() {
		return testMode;
	}
	
	

}
