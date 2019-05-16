package de.mq.iot.rule.support;


import java.util.Collection;
import java.util.Map.Entry;


public interface RulesAggregateResult<T> {
	
	Collection<T> states();
	
	Collection<String> processedRules();
	
	Collection<Entry<String,Exception>> exceptions();
	
	 boolean hasErrors(); 
	

}
