package de.mq.iot.calendar.support;

import org.jeasy.rules.api.Rule;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.RuleListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RuleListenerTest {
	
	private static final String RULE_NAME = "RuleName";

	private final RuleListener ruleListener = new SimpleRuleListener();
	
	private final Rule rule = Mockito.mock(Rule.class);
	private final Facts facts = new Facts();
	private final SpecialdaysRulesEngineResultImpl specialdaysRulesEngineResult = Mockito.mock(SpecialdaysRulesEngineResultImpl.class);
	private final Exception exception = new Exception();
	
	
	@BeforeEach
	void setup() {
		facts.put(SpecialdaysRulesEngineBuilder.RESULT,specialdaysRulesEngineResult );
	}
	
	@Test
	void beforeEvaluate() {
	
		
		Mockito.when(specialdaysRulesEngineResult.finished()).thenReturn(false);
		assertTrue(ruleListener.beforeEvaluate(rule, facts));
		
		Mockito.when(specialdaysRulesEngineResult.finished()).thenReturn(true);
		assertFalse(ruleListener.beforeEvaluate(rule, facts));
	}
	
	@Test
	void onSuccess() {
		Mockito.when(rule.getName()).thenReturn(RULE_NAME);
		
		ruleListener.onSuccess(rule, facts);
		
		Mockito.verify(specialdaysRulesEngineResult).assignSuccessRule(RULE_NAME);
	}
	
	@Test
	void onFailure() {
		
		
		Mockito.when(rule.getName()).thenReturn(RULE_NAME);
		
		ruleListener.onFailure(rule, facts, exception);
		
		Mockito.verify(specialdaysRulesEngineResult).assign(exception, RULE_NAME);
	}
	@Test
	void beforeExecute() {
		final Facts facts = Mockito.mock(Facts.class);
		
		ruleListener.beforeExecute(rule, facts);
		
		Mockito.verifyNoMoreInteractions(rule,facts);
	}
	
	@Test
	void afterEvaluate() {
		final Facts facts = Mockito.mock(Facts.class);
		
		ruleListener.afterEvaluate(rule, facts, false);
		
		Mockito.verifyNoMoreInteractions(rule,facts);
	}

}
