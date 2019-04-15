package de.mq.iot.rule.support;

import java.util.Collection;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import de.mq.iot.state.State;
import de.mq.iot.state.StateService;

@Rule(name = "systemVariablesUploadRule", priority=99)
public class SystemVariablesUploadRuleImpl {
	
	private final StateService stateService;
	
	SystemVariablesUploadRuleImpl(final StateService stateService) {
		this.stateService = stateService;
	}

	@Condition
	public  boolean evaluate(@Fact("ruleInput") final DefaultRuleInput ruleInput) {
		return ! ruleInput.isTestMode();
		
	}
	
	@Action
	public void updateSystemVariables( @Fact(RulesAggregate.RULE_OUTPUT_MAP_FACT) final Collection<State<Object>> results) {
		stateService.update(results); 
	}
	

}
