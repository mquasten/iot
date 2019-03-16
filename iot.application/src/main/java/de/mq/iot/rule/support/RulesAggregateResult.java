package de.mq.iot.rule.support;


import java.util.Collection;
import java.util.Map.Entry;
import java.util.Optional;

import de.mq.iot.state.State;

public interface RulesAggregateResult {
	
	Collection<State<?>> states();
	
	Collection<String> processedRules();
	
	Optional<Entry<String,Exception>> exception();
	
	

}
