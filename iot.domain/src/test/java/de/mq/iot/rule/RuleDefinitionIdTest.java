package de.mq.iot.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class RuleDefinitionIdTest {
	
	@Test
	void ids() {
		assertEquals(2, RulesDefinition.Id.values().length);
		assertTrue(Arrays.asList(RulesDefinition.Id.values()).contains(RulesDefinition.Id.DefaultDailyIotBatch));
		assertTrue(Arrays.asList(RulesDefinition.Id.values()).contains(RulesDefinition.Id.EndOfDayBatch));
	}
	
	@Test
	void inputDefaultDailyIotBatch() {
		assertEquals(3, RulesDefinition.Id.DefaultDailyIotBatch.input().size());
		
		
		assertTrue(RulesDefinition.Id.DefaultDailyIotBatch.input().contains(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY));
		assertTrue(RulesDefinition.Id.DefaultDailyIotBatch.input().contains(RulesDefinition.HOLIDAY_ALARM_TIME_KEY));
		assertTrue(RulesDefinition.Id.DefaultDailyIotBatch.input().contains(RulesDefinition.MIN_SUN_DOWN_TIME_KEY));
	}
	
	@Test
	void parameterDailyIotBatch() {
		assertEquals(3, RulesDefinition.Id.DefaultDailyIotBatch.parameter().size());
		assertTrue(RulesDefinition.Id.DefaultDailyIotBatch.parameter().contains(RulesDefinition.UPDATE_MODE_KEY));
		assertTrue(RulesDefinition.Id.DefaultDailyIotBatch.parameter().contains(RulesDefinition.TEST_MODE_KEY));
		assertTrue(RulesDefinition.Id.DefaultDailyIotBatch.parameter().contains(RulesDefinition.MIN_EVENT_TIME_KEY));
		
	}
	
	@Test
	void inputEndOfDayBatch() {
		assertEquals(3, RulesDefinition.Id.EndOfDayBatch.input().size());
		
		assertTrue(RulesDefinition.Id.EndOfDayBatch.input().contains(RulesDefinition.MAX_IP_COUNT_KEY));
		assertTrue(RulesDefinition.Id.EndOfDayBatch.input().contains(RulesDefinition.FIRST_IP_KEY));
		assertTrue(RulesDefinition.Id.EndOfDayBatch.input().contains(RulesDefinition.DAYS_BACK_KEY));
		
		
	}
	
	@Test
	void parameterEndOfDayBatch() {
		assertEquals(2, RulesDefinition.Id.EndOfDayBatch.parameter().size());
		
		assertTrue(RulesDefinition.Id.EndOfDayBatch.parameter().contains(RulesDefinition.UPDATE_MODE_KEY));
		assertTrue(RulesDefinition.Id.EndOfDayBatch.parameter().contains(RulesDefinition.TEST_MODE_KEY));
	}
	
}
