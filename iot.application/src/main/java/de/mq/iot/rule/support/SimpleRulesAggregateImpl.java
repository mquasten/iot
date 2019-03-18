package de.mq.iot.rule.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Optional;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RuleProxy;
import org.jeasy.rules.core.RulesEngineParameters;
import org.springframework.util.Assert;

import de.mq.iot.rule.RulesDefinition;
import de.mq.iot.rule.RulesDefinition.Id;
import de.mq.iot.state.State;



class SimpleRulesAggregateImpl  implements RulesAggregate {
	
	static final String RULE_INPUT_MAP_FACT = "ruleInputMap";
	
	private final RulesEngine rulesEngine;
	
	private final RulesDefinition.Id id;
	
	private final Rules rules;
	
	private final Facts facts = new Facts();
	
	private final Collection<Object> optionalRules = new HashSet<>(); 
	
	
	
	SimpleRulesAggregateImpl(final Id id, final Rules rules, final Object ... optionalRules) {
		this.rulesEngine = new DefaultRulesEngine(new RulesEngineParameters().skipOnFirstFailedRule(true));
		this.id = id;
		this.rules = rules;
		this.optionalRules.addAll(new HashSet<>(Arrays.asList(optionalRules)));
		facts.put(RulesAggregate.RULE_OUTPUT_MAP_FACT, new ArrayList<State<?>>());		
	}

	@Override
	public final Id id() {
		
		return id;
	}

	@Override
	public final RulesAggregate with(final RulesDefinition rulesDefinition) {
		
		validRulesDefinitionGuard(rulesDefinition);
		
		Assert.isTrue( ! inputDataAware(), "InputData is already assigned.");
		facts.put(RulesAggregate.RULE_INPUT_MAP_FACT, rulesDefinition.inputData());
		
		optionalRules.stream().filter(rule -> rulesDefinition.optionalRules().contains(RuleProxy.asRule(rule).getName())).forEach(rule -> rules.register(rule));
		
		
		
		return this;
	}

	private void validRulesDefinitionGuard(final RulesDefinition rulesDefinition) {
		Assert.notNull(rulesDefinition , "RulesDefinition is required.");
		
		Assert.isTrue(id == rulesDefinition.id(), "Ids should be identical.");
		Assert.notNull(rulesDefinition.optionalRules(), "RulesDefinition.optionalRules() shouldn't be null.");
	}

	private boolean inputDataAware() {
		return facts.asMap().containsKey(RulesAggregate.RULE_INPUT_MAP_FACT);
	}
	

	@Override
	public final RulesAggregateResult fire() {
		
		
		inputDataAwareGuard();
		
	
		final SkipOnExceptionRuleListenerImpl ruleListener =  new SkipOnExceptionRuleListenerImpl();
		((DefaultRulesEngine)rulesEngine).registerRuleListener(ruleListener);
		
		
		
		rulesEngine.fire(rules, facts);
		return new RulesAggregateResult() {

			@Override
			public Collection<State<?>> states() {
				return facts.get(RulesAggregate.RULE_OUTPUT_MAP_FACT);
			}

			@Override
			public Optional<Entry<String, Exception>> exception() {
				return ruleListener.error();
			}

			@Override
			public Collection<String> processedRules() {
				return ruleListener.processedRules();
			}

		};
			
		
	}

	private void inputDataAwareGuard() {
		Assert.isTrue(inputDataAware(), "InputData is required." );
	}

	
}
