package de.mq.iot.rule.support;

import de.mq.iot.rule.RulesDefinition;

@FunctionalInterface
interface RulesService {

	RulesAggregate rulesAggregate(RulesDefinition.Id id);

}