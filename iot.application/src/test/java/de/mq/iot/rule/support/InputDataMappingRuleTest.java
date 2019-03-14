package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.mq.iot.support.ApplicationConfiguration;
public class InputDataMappingRuleTest {
	
	
	private final ApplicationConfiguration configuration = new ApplicationConfiguration();
	private final InputDataMappingRuleImpl inputDataMappingRule = new InputDataMappingRuleImpl(configuration.conversionService());
	
	
	private final int workingdayAlarmHour = 5;
	
	private final int workingdayAlarmMin = 15;
	
	
	private final int holidayAlarmHour = 7;
	
	private final int holidayAlarmMin = 30;
	
	private final DefaultRuleInput ruleInput = new DefaultRuleInput();
	
	@Test
	void evaluate() {
		assertTrue(inputDataMappingRule.evaluate(newValidMap(true)));
		assertTrue(inputDataMappingRule.evaluate(newValidMap(false)));
		
		final Map<String,String> map = newValidMap(true);
		map.put(InputDataMappingRuleImpl.UPDATE_MODE_KEY, "2");
		assertFalse(inputDataMappingRule.evaluate(map));
	}
	
	@Test
	void mapping() {
		inputDataMappingRule.mapping(newValidMap(true), ruleInput);
		
		
		assertEquals(LocalTime.of(workingdayAlarmHour, workingdayAlarmMin), ruleInput.workingdayAlarmTime());
		assertEquals(LocalTime.of(holidayAlarmHour, holidayAlarmMin), ruleInput.holidayAlarmTime());
		assertTrue(ruleInput.isTestMode());
		assertTrue(ruleInput.isUpdateMode());
	}
	private  Map<String, String> newValidMap(final boolean withFlags) {
		final Map<String,String> ruleInputMap = new HashMap<>();
		
		ruleInputMap.put(InputDataMappingRuleImpl.WORKINGDAY_ALARM_TIME_KEY, String.format("%s:%s", workingdayAlarmHour, workingdayAlarmMin));
		
		ruleInputMap.put(InputDataMappingRuleImpl.HOLIDAY_ALARM_TIME_KEY, String.format("%s:%s", holidayAlarmHour, holidayAlarmMin));
		if( withFlags) {
		ruleInputMap.put(InputDataMappingRuleImpl.UPDATE_MODE_KEY, "1");
		ruleInputMap.put(InputDataMappingRuleImpl.TEST_MODE_KEY, "true");
		}
		
		return ruleInputMap;
	}
	
	@Test
	void mappingWithoutBooleans() {
		inputDataMappingRule.mapping(newValidMap(false), ruleInput);
		
		
		assertEquals(LocalTime.of(workingdayAlarmHour, workingdayAlarmMin), ruleInput.workingdayAlarmTime());
		assertEquals(LocalTime.of(holidayAlarmHour, holidayAlarmMin), ruleInput.holidayAlarmTime());
		assertFalse(ruleInput.isTestMode());
		assertFalse(ruleInput.isUpdateMode());
	}

}
