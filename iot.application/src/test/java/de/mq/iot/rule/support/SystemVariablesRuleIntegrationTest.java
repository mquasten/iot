package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.AbstractMap;
import java.util.Arrays;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mq.iot.rule.RulesDefinition;
import de.mq.iot.rule.RulesDefinition.Id;
import de.mq.iot.state.State;
import de.mq.iot.support.ApplicationConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })
@Disabled
class SystemVariablesRuleIntegrationTest {
	
	@Autowired
	private  RulesService ruleService;
	
	@Test
	@Disabled
	final void createRulesEngine() {
		
		@SuppressWarnings("unchecked")
		final RulesAggregate<State<?>> rulesAggregate = (RulesAggregate<State<?>>) ruleService.rulesAggregate(Id.DefaultDailyIotBatch, Arrays.asList(new AbstractMap.SimpleImmutableEntry<>(RulesDefinition.UPDATE_MODE_KEY,"false"),new AbstractMap.SimpleImmutableEntry<>(RulesDefinition.TEST_MODE_KEY,"false")));
		
		assertNotNull(rulesAggregate);
		
		RulesAggregateResult<State<?>> result = rulesAggregate.fire();
		
		
		System.out.println("Errors: " +result.hasErrors());
		
		System.out.println("Verarbeitete Regeln: " + result.processedRules());
		
		
		result.exceptions().forEach(exception ->  {
		System.out.println(exception.getKey() +":");
		exception.getValue().printStackTrace();
		});
		
		result.states().forEach(state -> System.out.println(state.name()+":" +state.value()));
		
	}
	
	@Test
	@Disabled
	final void endOfDay() {
		final RulesAggregate<?> rulesAggregate = ruleService.rulesAggregate(Id.EndOfDayBatch, Arrays.asList());
	
	    System.out.println(rulesAggregate);
	    rulesAggregate.fire();
	}

}
