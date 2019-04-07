package de.mq.iot.rule.support;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Optional;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.RuleListener;



class SkipOnExceptionRuleListenerImpl implements RuleListener {

	private Optional<Entry<String, Exception>> error = Optional.empty();

	private final Collection<String> processedRules = new ArrayList<>();
	
	
	Optional<Entry<String, Exception>> error() {
		return error;
	}
	
	Collection<String> processedRules() {
		return Collections.unmodifiableCollection(processedRules);
	}


	

	
	@Override
	public final  boolean beforeEvaluate(final Rule rule, final Facts facts) {
		return ! error.isPresent();
	}

	@Override
	public final void afterEvaluate(final Rule rule, final Facts facts, final boolean evaluationResult) {
	}

	@Override
	public final void beforeExecute(final Rule rule, final Facts facts) {
	}

	@Override
	public final void onSuccess(final Rule rule, final Facts facts) {
		
		
		processedRules.add(rule.getName());
	}

	@Override
	public void onFailure(final Rule rule, final Facts facts, final Exception exception) {
		if( !error.isPresent()) {
			error=Optional.of(new AbstractMap.SimpleImmutableEntry<>(rule.getName(), exception));
		}

	}

}
