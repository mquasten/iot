package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RuleListenerTest {
	
	private static final String RULE_NAME = "ruleName";

	private final SkipOnExceptionRuleListenerImpl ruleListener = new SkipOnExceptionRuleListenerImpl(Arrays.asList());
	
	private final Rule rule = Mockito.mock(Rule.class);
	
	private final Facts facts = new Facts();
	
	@BeforeEach
	void setup() {
		Mockito.when(rule.getName()).thenReturn(RULE_NAME);
	}
	
	@Test
	void onFailure() {
		assertFalse(ruleListener.hasErrors());
		assertTrue(ruleListener.beforeEvaluate(rule, facts));
		
		final Exception exception = new Exception();
		ruleListener.onFailure(rule, facts, exception);
		
		assertTrue(ruleListener.hasErrors());
		assertFalse(ruleListener.beforeEvaluate(rule, facts));
		assertEquals(exception, ruleListener.exceptions().iterator().next().getValue());
		assertEquals(RULE_NAME, ruleListener.exceptions().iterator().next().getKey());
		
		
	}
	
	@Test
	void onSuccess() {
		assertEquals(0, ruleListener.processedRules().size());
		
		ruleListener.onSuccess(rule, facts);
		
		assertEquals(1, ruleListener.processedRules().size());
		assertEquals(RULE_NAME, ruleListener.processedRules().iterator().next());
	}

}
