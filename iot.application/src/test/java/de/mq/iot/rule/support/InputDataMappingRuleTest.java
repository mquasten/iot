package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class InputDataMappingRuleTest {
	
	private final InputDataMappingRuleImpl inputDataMappingRule = new InputDataMappingRuleImpl();
	
	
	private final int workingdayAlarmHour = 5;
	
	private final int workingdayAlarmMin = 15;
	
	
	private final int holidayAlarmHour = 7;
	
	private final int holidayAlarmMin = 30;
	
	private final DefaultRuleInput ruleInput = new DefaultRuleInput();
	
	@Test
	void evaluate() {
		assertTrue(inputDataMappingRule.evaluate(newValidMap()));
	}
	
	@Test
	void mapping() {
		inputDataMappingRule.mapping(newValidMap(), ruleInput);
		
		
		assertEquals(LocalTime.of(workingdayAlarmHour, workingdayAlarmMin), ruleInput.workingdayAlarmTime());
		assertEquals(LocalTime.of(holidayAlarmHour, holidayAlarmMin), ruleInput.holidayAlarmTime());
	}
	private  Map<String, String> newValidMap() {
		final Map<String,String> ruleInputMap = new HashMap<>();
		
		ruleInputMap.put(InputDataMappingRuleImpl.WORKINGDAY_ALARM_TIME_KEY, String.format("%s:%s", workingdayAlarmHour, workingdayAlarmMin));
		
		ruleInputMap.put(InputDataMappingRuleImpl.HOLIDAY_ALARM_TIME_KEY, String.format("%s:%s", holidayAlarmHour, holidayAlarmMin));
		return ruleInputMap;
	}

}
