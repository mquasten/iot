package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.calendar.DayGroup;
import de.mq.iot.rule.RulesDefinition;

class InputDataMappingRuleTest {
	

	
	private static final Integer DAYS_BACK = 60;


	private static final Integer MAX_IPS = 200;


	private static final Integer FIRST_IP = 0;


	private final RuleConfiguration configuration = new RuleConfiguration();
	
	
	private InputDataMappingRuleImpl inputDataMappingRule=  new InputDataMappingRuleImpl(configuration.conversionService(Collections.emptyList()), configuration.validationFactory(configuration.conversionService(Collections.emptyList())));;
	
	
	private final int workingdayAlarmHour = 5;
	
	private final int workingdayAlarmMin = 15;
	
	
	private final int holidayAlarmHour = 7;
	
	private final int holidayAlarmMin = 30;
	
	private final int specialWorkingdayAlarmHour = 6;
	
	private final int specialWorkingdayAlarmMin = 10;
	
	
	private final int minSunDownHour = 17;
	
	private final int minSunDownMin = 15;
	
	
	private final DefaultRuleInput ruleInput = new DefaultRuleInput();
	
	private final DayGroup dayGroup =  Mockito.mock(DayGroup.class);
	
	
	
	@Test
	void evaluateDefaultDailyIotBatch() {
		assertTrue(inputDataMappingRule.evaluate(RulesDefinition.Id.DefaultDailyIotBatch, newValidMapDefaultDailyIotBatch(true)));
		assertTrue(inputDataMappingRule.evaluate(RulesDefinition.Id.DefaultDailyIotBatch, newValidMapDefaultDailyIotBatch(false)));
		
		final Map<String,String> map = newValidMapDefaultDailyIotBatch(true);
		map.put(RulesDefinition.UPDATE_MODE_KEY, "2");
		assertFalse(inputDataMappingRule.evaluate(RulesDefinition.Id.DefaultDailyIotBatch, map));
	}
	
	@Test
	void mappingDefaultDailyIotBatch() {
		inputDataMappingRule.mapping(RulesDefinition.Id.DefaultDailyIotBatch, newValidMapDefaultDailyIotBatch(true), ruleInput);
		
		Mockito.when(dayGroup.name()).thenReturn(DayGroup.WORKINGDAY_GROUP_NAME);
		assertEquals(LocalTime.of(workingdayAlarmHour, workingdayAlarmMin), ruleInput.alarmTime(dayGroup));	

		Mockito.when(dayGroup.name()).thenReturn(DayGroup.NON_WORKINGDAY_GROUP_NAME);
		assertEquals(LocalTime.of(holidayAlarmHour, holidayAlarmMin), ruleInput.alarmTime(dayGroup));
		
		Mockito.when(dayGroup.name()).thenReturn(DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME);
		assertEquals(LocalTime.of(specialWorkingdayAlarmHour, specialWorkingdayAlarmMin), ruleInput.alarmTime(dayGroup));
		
		assertEquals(LocalTime.of(minSunDownHour, minSunDownMin), ruleInput.minSunDownTime());
		assertTrue(ruleInput.isTestMode());
		assertTrue(ruleInput.isUpdateMode());
	}
	
	
	@Test
	void mappingWithoutOptionalDefaultDailyIotBatch() {
		final Map<String, String> newValidMap = newValidMapDefaultDailyIotBatch(false);
		
		newValidMap.remove(RulesDefinition.MIN_SUN_DOWN_TIME_KEY);
		inputDataMappingRule.mapping(RulesDefinition.Id.DefaultDailyIotBatch, newValidMap, ruleInput);
		
		Mockito.when(dayGroup.name()).thenReturn(DayGroup.WORKINGDAY_GROUP_NAME);
		assertEquals(LocalTime.of(workingdayAlarmHour, workingdayAlarmMin),ruleInput.alarmTime(dayGroup));
		
		Mockito.when(dayGroup.name()).thenReturn(DayGroup.NON_WORKINGDAY_GROUP_NAME);
		assertEquals(LocalTime.of(holidayAlarmHour, holidayAlarmMin), ruleInput.alarmTime(dayGroup));
		
		Mockito.when(dayGroup.name()).thenReturn(DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME);
		assertEquals(LocalTime.of(specialWorkingdayAlarmHour, specialWorkingdayAlarmMin), ruleInput.alarmTime(dayGroup));
		
		assertEquals(LocalTime.MIDNIGHT, ruleInput.minSunDownTime());
		
	}
	
	private  Map<String, String> newValidMapDefaultDailyIotBatch(final boolean withFlags) {
		final Map<String,String> ruleInputMap = new HashMap<>();
		
		ruleInputMap.put(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY, String.format("%s:%s", workingdayAlarmHour, workingdayAlarmMin));
		
		ruleInputMap.put(RulesDefinition.HOLIDAY_ALARM_TIME_KEY, String.format("%s:%s", holidayAlarmHour, holidayAlarmMin));
		
		ruleInputMap.put(RulesDefinition.SPECIAL_WORKINGDAY_ALARM_TIME_KEY, String.format("%s:%s", specialWorkingdayAlarmHour, specialWorkingdayAlarmMin));
		
		
		ruleInputMap.put(RulesDefinition.MIN_SUN_DOWN_TIME_KEY, String.format("%s:%s", minSunDownHour, minSunDownMin));
		
		
		
		if( withFlags) {
		ruleInputMap.put(RulesDefinition.UPDATE_MODE_KEY, "1");
		ruleInputMap.put(RulesDefinition.TEST_MODE_KEY, "true");
		}
		
		return ruleInputMap;
	}
	
	@Test
	void mappingWithoutBooleans() {
		inputDataMappingRule.mapping(RulesDefinition.Id.DefaultDailyIotBatch, newValidMapDefaultDailyIotBatch(false), ruleInput);
		
		Mockito.when(dayGroup.name()).thenReturn(DayGroup.WORKINGDAY_GROUP_NAME);
		assertEquals(LocalTime.of(workingdayAlarmHour, workingdayAlarmMin), ruleInput.alarmTime(dayGroup));
		
		Mockito.when(dayGroup.name()).thenReturn(DayGroup.NON_WORKINGDAY_GROUP_NAME);
		assertEquals(LocalTime.of(holidayAlarmHour, holidayAlarmMin), ruleInput.alarmTime(dayGroup));
		
		Mockito.when(dayGroup.name()).thenReturn(DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME);
		assertEquals(LocalTime.of(specialWorkingdayAlarmHour, specialWorkingdayAlarmMin), ruleInput.alarmTime(dayGroup));
		
		assertFalse(ruleInput.isTestMode());
		assertFalse(ruleInput.isUpdateMode());
	}
	
	
	@Test
	void evaluateEndOfDayBatch() {
		assertTrue(inputDataMappingRule.evaluate(RulesDefinition.Id.EndOfDayBatch, mapEndOfDayBatch()));
		
	}
	
	@Test
	void evaluateEndOfDayBatchInvalidValue() {
		final Map<String,String> values = new HashMap<>();
		values.put(RulesDefinition.FIRST_IP_KEY, "x");
		assertFalse(inputDataMappingRule.evaluate(RulesDefinition.Id.EndOfDayBatch, values));
		
	}
	
	private Map<String,String> mapEndOfDayBatch() {
		final Map<String,String> ruleInputMap = new HashMap<>();
		
		ruleInputMap.put(RulesDefinition.FIRST_IP_KEY, FIRST_IP.toString());
		ruleInputMap.put(RulesDefinition.MAX_IP_COUNT_KEY, MAX_IPS.toString());
		ruleInputMap.put(RulesDefinition.DAYS_BACK_KEY, DAYS_BACK.toString());
		ruleInputMap.put(RulesDefinition.UPDATE_MODE_KEY, Boolean.TRUE.toString());
		ruleInputMap.put(RulesDefinition.TEST_MODE_KEY, Boolean.TRUE.toString());
		
		return ruleInputMap;
	}
	
	@Test
	void mapping() {
		final Map<String,String> ruleInputMap = mapEndOfDayBatch();
		final EndOfDayRuleInput ruleInput = new EndOfDayRuleInput();
		
		inputDataMappingRule.mapping(RulesDefinition.Id.EndOfDayBatch, ruleInputMap, ruleInput);
		
		assertEquals((int) FIRST_IP, ruleInput.ipRange().getMinimum());
		assertEquals((int) FIRST_IP+ MAX_IPS, ruleInput.ipRange().getMaximum());
	
		assertEquals(LocalDate.now().minusDays(DAYS_BACK+1), ruleInput.minDeletiondate());
		
		//assertEquals(DAYS_BACK, ruleInput.daysBack());
		assertTrue(ruleInput.isTestMode());
	
		
		
	}

}
