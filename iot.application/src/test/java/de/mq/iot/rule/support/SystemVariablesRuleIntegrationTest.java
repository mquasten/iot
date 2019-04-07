package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mq.iot.rule.RulesDefinition.Id;
import de.mq.iot.support.ApplicationConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })
@Disabled
class SystemVariablesRuleIntegrationTest {
	
	@Autowired
	private  RulesService ruleService;
	
	@Test
	final void createRulesEngine() {
		
		final RulesAggregate rulesAggregate = ruleService.rulesAggregate(Id.DefaultDailyIotBatch);
		
		assertNotNull(rulesAggregate);
		
		RulesAggregateResult result = rulesAggregate.fire();
		
		
		System.out.println(result.exception().isPresent());
		
		System.out.println(result.processedRules());
		
		//System.out.println(result.exception().get().getValue());
	}

}
