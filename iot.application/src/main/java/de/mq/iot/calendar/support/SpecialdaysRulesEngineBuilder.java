package de.mq.iot.calendar.support;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RulesEngineParameters;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import de.mq.iot.calendar.Specialday;

public class SpecialdaysRulesEngineBuilder  {
	
	static final String RESULT = "result";

	static final String SPECIALDAYS_INPUT = "specialdays";

	static final String DATE_INPUT = "date";
	private Rules rules; 
	
	private Collection<Specialday> specialdays = new ArrayList<>();

	
	SpecialdaysRulesEngineBuilder withRules(final Collection<Rule> rules) {
		
		Assert.isNull(this.rules, "Rules already assigned");
		this.rules=new Rules(rules.toArray(new Rule[0]));
		return this;
	}
	
	SpecialdaysRulesEngineBuilder withSpecialdays(final Collection<Specialday> specialdays) {
		Assert.isTrue(CollectionUtils.isEmpty(this.specialdays), "Specialdays already assigned");
		this.specialdays.addAll(specialdays);
		return this;
	}
	
	
	public SpecialdaysRulesEngineResult execute(final LocalDate date) {
		Assert.isTrue( !rules.isEmpty(), "At least  1 rule must be given.");
		Assert.notEmpty(specialdays, "At least  1 specialday must given.");
		Assert.notNull(date, "Date is mandatory");
		final Facts facts = new Facts();
		facts.put(SPECIALDAYS_INPUT, specialdays);
		facts.put(RESULT, new SpecialdaysRulesEngineResultImpl());
		facts.put(DATE_INPUT, date);
		final RulesEngineParameters parameters = new RulesEngineParameters().skipOnFirstAppliedRule(true).skipOnFirstFailedRule(true);
		
		final RulesEngine rulesEngine = new DefaultRulesEngine(parameters);
		SimpleRuleListener ruleListener = new SimpleRuleListener();
		((DefaultRulesEngine)rulesEngine).registerRuleListener(ruleListener);
		
		rulesEngine.fire(rules, facts);
		
		
		return facts.get(RESULT);
		
	}
	
	
	
	
	
	


	
}

	
