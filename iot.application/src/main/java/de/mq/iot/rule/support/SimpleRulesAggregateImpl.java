package de.mq.iot.rule.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RuleProxy;
import org.jeasy.rules.core.RulesEngineParameters;
import org.springframework.util.Assert;

import de.mq.iot.rule.RulesDefinition;
import de.mq.iot.rule.RulesDefinition.Id;
;



class SimpleRulesAggregateImpl<T>  implements RulesAggregate<T> {
	
	static final String RULE_INPUT_MAP_FACT = "ruleInputMap";
	
	private final RulesEngine rulesEngine;
	
	private final RulesDefinition.Id id;
	
	private final Rules rules;
	
	private final Facts facts = new Facts();
	
	private final Collection<Object> optionalRules = new HashSet<>(); 
	
	
	
	SimpleRulesAggregateImpl(final Id id, final Consumer<Facts> factsConsumer, final Rules rules,  final Object ... optionalRules) {
		this.rulesEngine = new DefaultRulesEngine(new RulesEngineParameters().skipOnFirstFailedRule(false));
		this.id = id;
		factsConsumer.accept(facts);
		this.rules = rules;
		this.optionalRules.addAll(new HashSet<>(Arrays.asList(optionalRules)));
		
	}

	

	@Override
	public final Id id() {
		
		return id;
	}

	@Override
	public final RulesAggregate<T> with(final RulesDefinition rulesDefinition) {
		
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
	public final RulesAggregateResult<T> fire() {
		
		
		inputDataAwareGuard();
		final Collection<String> optionalRules = this.optionalRules.stream().map(rule -> RuleProxy.asRule(rule).getName()).collect(Collectors.toList());
	
		
		final SkipOnExceptionRuleListenerImpl ruleListener =  new SkipOnExceptionRuleListenerImpl(optionalRules);
		((DefaultRulesEngine)rulesEngine).registerRuleListener(ruleListener);
		
		
		
		rulesEngine.fire(rules, facts);
		return new RulesAggregateResult<T>() {

			@Override
			public Collection<T> states() {
				return facts.get(RulesAggregate.RULE_OUTPUT_MAP_FACT);
			}

			@Override
			public Collection<Entry<String, Exception>> exceptions() {
				return ruleListener.exceptions();
			}

			@Override
			public Collection<String> processedRules() {
				return ruleListener.processedRules();
			}

			@Override
			public boolean hasErrors() {
				return ruleListener.hasErrors();
			}

		};
			
		
	}

	private void inputDataAwareGuard() {
		Assert.isTrue(inputDataAware(), "InputData is required." );
	}

	
}
