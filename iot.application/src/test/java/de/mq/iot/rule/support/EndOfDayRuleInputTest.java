package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.temporal.ValueRange;

import org.junit.jupiter.api.Test;

class EndOfDayRuleInputTest {
	
	private static final int DAYS_BACK = 60;
	private static final int MAX_IP_COUNT = 99;
	private static final int FIRST_IP = 0;
	private final EndOfDayRuleInput ruleInput = new EndOfDayRuleInput(FIRST_IP, MAX_IP_COUNT, DAYS_BACK,true,true);
	
	@Test
	void testMode() {
		assertTrue(ruleInput.isTestMode());
	}
	
	@Test
	void ipRange() {
		assertEquals(ValueRange.of(FIRST_IP, MAX_IP_COUNT), ruleInput.ipRange());
	}
	
	@Test
	void ipRangeOutOfRange() {
		final EndOfDayRuleInput ruleInput = new EndOfDayRuleInput(256, MAX_IP_COUNT, DAYS_BACK,true, true);
		assertEquals(ValueRange.of(255, 256), ruleInput.ipRange());
	}
	
	@Test
	void defaultValues() {
		final EndOfDayRuleInput ruleInput = new EndOfDayRuleInput();
		assertFalse(ruleInput.isTestMode());
		assertEquals(LocalDate.now().minusDays(EndOfDayRuleInput.DAYS_BACK_DEFAULT), ruleInput.minDeletiondate());
		assertEquals(ValueRange.of(EndOfDayRuleInput.FIRST_IP_DEFAULT, EndOfDayRuleInput.FIRST_IP_DEFAULT+ EndOfDayRuleInput.MAX_IP_COUNT_DEFAULT), ruleInput.ipRange());
		
	}
	
	@Test
	void minDeletiondate() {
		assertEquals(LocalDate.now().minusDays(DAYS_BACK+1), ruleInput.minDeletiondate());
		
		assertEquals(LocalDate.now().minusDays(DAYS_BACK),  new EndOfDayRuleInput(FIRST_IP, MAX_IP_COUNT, DAYS_BACK,false, true).minDeletiondate());
	 
 }

}
