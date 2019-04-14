package de.mq.iot.rule.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.RuleListener;



class SkipOnExceptionRuleListenerImpl implements RuleListener {

	private Map<String, Exception> exceptions = new HashMap<>();

	private final Collection<String> processedRules = new ArrayList<>();
	
	private final Collection<String> optionalRules = new ArrayList<>();
	
	SkipOnExceptionRuleListenerImpl(final Collection<String> optionalRules ) {
		this.optionalRules.addAll(optionalRules);
	}
	
	
	boolean hasErrors() {
		return exceptions.keySet().stream().filter(key -> ! optionalRules.contains(key)).findAny().isPresent();
	}
	
	Collection<String> processedRules() {
		return Collections.unmodifiableCollection(processedRules);
	}


	Collection<Entry<String, Exception>> exceptions() { 
	   return Collections.unmodifiableCollection(this.exceptions.entrySet());	
	}
	

	
	@Override
	public final  boolean beforeEvaluate(final Rule rule, final Facts facts) {
		return !hasErrors() ;
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
			this.exceptions.put(rule.getName(), exception);
	}

}
