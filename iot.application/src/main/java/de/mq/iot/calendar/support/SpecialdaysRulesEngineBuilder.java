package de.mq.iot.calendar.support;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.function.Function;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.RuleListener;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RulesEngineParameters;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;



import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService.DayType;

public class SpecialdaysRulesEngineBuilder implements  Function<LocalDate, Entry<DayType,String>> {
	
	private static final String SPECIALDAYS_INPUT = "specialdays";

	private static final String DATE_INPUT = "date";
	private Rules rules; 
	
	private Collection<Specialday> specialdays = new ArrayList<>();

	
	SpecialdaysRulesEngineBuilder withRules(final Collection<Object> rules) {
		
		Assert.isNull(this.rules, "Rules already assigned");
		this.rules=new Rules(rules);
		return this;
	}
	
	SpecialdaysRulesEngineBuilder withSpecialDays(final Collection<Specialday> specialdays) {
		Assert.isNull(this.rules, "Rules already assigned");
		this.specialdays.addAll(specialdays);
		return this;
	}
	
	@Override
	public Entry<DayType, String> apply(final LocalDate date) {
		Assert.isTrue( !rules.isEmpty(), "At least  1 rule must be given.");
		Assert.notEmpty(specialdays, "At least  1 specialday must given.");
		Assert.notNull(date, "Date is mandatory");
		final Facts facts = new Facts();
		facts.put(SPECIALDAYS_INPUT, specialdays);
		facts.put(DATE_INPUT, date);
		final RulesEngineParameters parameters = new RulesEngineParameters().skipOnFirstAppliedRule(true).skipOnFirstFailedRule(true);
		
		final RulesEngine rulesEngine = new DefaultRulesEngine(parameters);
		SimpleRuleListener ruleListener = new SimpleRuleListener();
		((DefaultRulesEngine)rulesEngine).registerRuleListener(ruleListener);
		
		rulesEngine.fire(rules, facts);
		if(! CollectionUtils.isEmpty(ruleListener.errors)) {
			throw ruleListener.errors.stream().findFirst().get(); 
		}
		
		final Entry<DayType,String> result =  facts.get("result");
		Assert.notNull(result, "Result is nor aware.");
		return result;
	}
	
	
	
	
	class SimpleRuleListener implements RuleListener {
		private final Collection<IllegalStateException> errors = new HashSet<>();
		@Override
		public boolean beforeEvaluate(final Rule rule, final Facts facts) {
			return true;
		}

		@Override
		public void afterEvaluate(final Rule rule, Facts facts, final boolean evaluationResult) {
			
			
		}

		@Override
		public void beforeExecute(final Rule rule, final Facts facts) {
			
			
		}

		@Override
		public void onSuccess(final Rule rule, final Facts facts) {
			
			
		}

		@Override
		public void onFailure(final Rule rule, final Facts facts, final Exception exception) {
			errors.add(new IllegalStateException("Error executing rule " + rule.getName(), exception));
			
		}
		
		
		
	}

	


	
}

	
