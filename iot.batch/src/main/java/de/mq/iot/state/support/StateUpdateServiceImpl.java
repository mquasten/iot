package de.mq.iot.state.support;



import java.util.AbstractMap;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mq.iot.rule.RulesDefinition;
import de.mq.iot.rule.RulesDefinition.Id;
import de.mq.iot.rule.support.RulesAggregate;
import de.mq.iot.rule.support.RulesAggregateResult;
import de.mq.iot.rule.support.RulesService;
import de.mq.iot.state.Command;
import de.mq.iot.state.Commands;

@Service
public class StateUpdateServiceImpl  {

	
	private final  RulesService rulesService;
	@Autowired
	StateUpdateServiceImpl(final  RulesService rulesService) {

		
		this.rulesService=rulesService;
	}
	
	
	@Commands(commands = {  @Command(arguments = {"n", "u", "t" }, name = "processRules" ) })
	public void processRules(final String name, final boolean update, final boolean test) {
		final Id id = Id.valueOf(name);
		
		final RulesAggregate<?> rulesAggregate = rulesService.rulesAggregate(id, Arrays.asList(new AbstractMap.SimpleImmutableEntry<>(RulesDefinition.UPDATE_MODE_KEY, String.valueOf(update)), new AbstractMap.SimpleImmutableEntry<>(RulesDefinition.TEST_MODE_KEY, String.valueOf(test))));
	
		System.out.println("Name: " +id.name());
		
		System.out.println("UpdateMode: "  + update);
		System.out.println("TestMode: "  + test);
		RulesAggregateResult<?> result = rulesAggregate.fire();
		
		
		System.out.println("Errors: " +result.hasErrors());
		
		System.out.println("Verarbeitete Regeln: " + result.processedRules());
		
		
		result.exceptions().forEach(exception ->  {
		System.out.println(exception.getKey() +":");
		exception.getValue().printStackTrace();
		});
		
		System.out.println("Ergebnisse:");
		result.states().forEach(state -> System.out.println(state));
		
	}

	
	

}
