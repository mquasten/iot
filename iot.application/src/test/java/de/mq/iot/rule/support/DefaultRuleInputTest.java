package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

class DefaultRuleInputTest {
	
	private final LocalTime workingdayAlarmTime = LocalTime.of(5, 15);
	
	private final  LocalTime holidayAlarmTime = LocalTime.of(7, 15);
	
	private final DefaultRuleInput ruleInput = new DefaultRuleInput(workingdayAlarmTime, holidayAlarmTime);
	
	@Test
	void workingdayAlarmTime() {
		assertEquals(workingdayAlarmTime, ruleInput.workingdayAlarmTime());
	}
	
	
	@Test
	void holidayAlarmTime() {
		assertEquals(holidayAlarmTime, ruleInput.holidayAlarmTime());
	}
	
	@Test
	void updateMode() {
		assertFalse(ruleInput.isUpdateMode());
		ruleInput.useUpdateMode();
		assertTrue(ruleInput.isUpdateMode());
	}
	
	@Test
	void testMode() {
		assertFalse(ruleInput.isTestMode());
		ruleInput.useTestMode();
		assertTrue(ruleInput.isTestMode());
	}
	@Test
	void defaultConstructor() {
		final DefaultRuleInput ruleInput = new DefaultRuleInput();
		assertFalse(ruleInput.isUpdateMode());
		assertEquals(LocalTime.MIDNIGHT, ruleInput.workingdayAlarmTime());
		assertEquals(LocalTime.MIDNIGHT, ruleInput.holidayAlarmTime());
	}
}
