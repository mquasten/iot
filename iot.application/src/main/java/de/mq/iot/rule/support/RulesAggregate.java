package de.mq.iot.rule.support;

import de.mq.iot.rule.support.RulesDefinition.Id;

public interface RulesAggregate {
	static final String RULE_INPUT_MAP_FACT = "ruleInputMap";
	
	static final String RULE_OUTPUT_MAP_FACT = "ruleOutputMap";
	Id id();
	
	
	RulesAggregate with(final RulesDefinition rulesDefinition);
	RulesAggregateResult fire();

	
}
