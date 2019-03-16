package de.mq.iot.rule.support;

import java.util.Collection;
import java.util.Map;

public interface RulesDefinition
 {
	enum Id {
		DefaultDailyIotBatch
	}
	
	
	Id id();
	
	Map<String,String> inputData();
	
	Collection<String> optionalRules();
	
}
