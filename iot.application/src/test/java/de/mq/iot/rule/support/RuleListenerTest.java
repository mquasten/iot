package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RuleListenerTest {
	
	private static final String RULE_NAME = "ruleName";

	private final SkipOnExceptionRuleListenerImpl ruleListener = new SkipOnExceptionRuleListenerImpl();
	
	private final Rule rule = Mockito.mock(Rule.class);
	
	private final Facts facts = new Facts();
	
	@BeforeEach
	void setup() {
		Mockito.when(rule.getName()).thenReturn(RULE_NAME);
	}
	
	@Test
	void onFailure() {
		assertFalse(ruleListener.error().isPresent());
		assertTrue(ruleListener.beforeEvaluate(rule, facts));
		
		final Exception exception = new Exception();
		ruleListener.onFailure(rule, facts, exception);
		
		assertTrue(ruleListener.error().isPresent());
		assertFalse(ruleListener.beforeEvaluate(rule, facts));
		assertEquals(exception, ruleListener.error().get().getValue());
		assertEquals(RULE_NAME, ruleListener.error().get().getKey());
		
		
		ruleListener.onFailure(rule, facts, new Exception());
		assertTrue(ruleListener.error().isPresent());
		assertEquals(exception, ruleListener.error().get().getValue());
	}
	
	@Test
	void onSuccess() {
		assertEquals(0, ruleListener.processedRules().size());
		
		ruleListener.onSuccess(rule, facts);
		
		assertEquals(1, ruleListener.processedRules().size());
		assertEquals(RULE_NAME, ruleListener.processedRules().iterator().next());
	}

}
