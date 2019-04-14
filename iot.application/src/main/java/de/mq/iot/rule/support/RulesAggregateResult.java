package de.mq.iot.rule.support;


import java.util.Collection;
import java.util.Map.Entry;
import de.mq.iot.state.State;

public interface RulesAggregateResult {
	
	Collection<State<?>> states();
	
	Collection<String> processedRules();
	
	Collection<Entry<String,Exception>> exceptions();
	
	 boolean hasErrors(); 
	

}
