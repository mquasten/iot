package de.mq.iot.calendar.support;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.RuleListener;

class SimpleRuleListener implements RuleListener {
	
	@Override
	public boolean beforeEvaluate(final Rule rule, final Facts facts) {
		final SpecialdaysRulesEngineResultImpl result = facts.get(SpecialdaysRulesEngineBuilder.RESULT);
		return ! result.finished();
		
	}

	@Override
	public void afterEvaluate(final Rule rule, Facts facts, final boolean evaluationResult) {
		
		
	}

	@Override
	public void beforeExecute(final Rule rule, final Facts facts) {
		
		
	}

	@Override
	public void onSuccess(final Rule rule, final Facts facts) {
		final SpecialdaysRulesEngineResultImpl result = facts.get(SpecialdaysRulesEngineBuilder.RESULT);
		result.assignSuccessRule(rule.getName());
		
	}

	@Override
	public void onFailure(final Rule rule, final Facts facts, final Exception exception) {
		final SpecialdaysRulesEngineResultImpl result = facts.get(SpecialdaysRulesEngineBuilder.RESULT);
		result.assign(exception, rule.getName());
		
	}
	
	
	
}
