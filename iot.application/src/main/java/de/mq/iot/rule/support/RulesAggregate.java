package de.mq.iot.rule.support;

import de.mq.iot.rule.RulesDefinition;
import de.mq.iot.rule.RulesDefinition.Id;

public interface RulesAggregate<T> {
	static final String RULE_INPUT_MAP_FACT = "ruleInputMap";
	static final String RULE_ENGINE_ID = "ruleEngineId";
	
	static final String RULE_OUTPUT_MAP_FACT = "ruleOutputMap";
	
	static final String RULE_INPUT = "ruleInput";
	
	static final String RULE_CALENDAR = "calendar";
	Id id();
	
	
	RulesAggregate<T> with(final RulesDefinition rulesDefinition);
	RulesAggregateResult<T> fire();

	
}
