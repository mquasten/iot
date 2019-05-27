package de.mq.iot.rule.support;

import java.util.Collection;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

@Rule(name = "homematicGateWayFinderRule", priority = 1)
public class CleanupRuleImpl {
	
	@Condition
	public boolean evaluate(@Fact(RulesAggregate.RULE_INPUT) final EndOfDayRuleInput ruleInput) {

		return ruleInput.valid();
	}

	@Action
	public void cleanup(@Fact(RulesAggregate.RULE_INPUT) final EndOfDayRuleInput ruleInput , @Fact(RulesAggregate.RULE_OUTPUT_MAP_FACT) final Collection<String> results   ) {
		
		
		
	   
	}

}
