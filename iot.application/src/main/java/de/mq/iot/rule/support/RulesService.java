package de.mq.iot.rule.support;

import java.util.Collection;
import java.util.Map.Entry;

import de.mq.iot.rule.RulesDefinition;


public interface RulesService {

	RulesAggregate<?> rulesAggregate(final RulesDefinition.Id id,final Collection<Entry<String,String>> parameters);

	RulesAggregate<?> rulesAggregate(final RulesDefinition rulesDefinition);
	
	Collection<RulesDefinition> rulesDefinitions();

	void save(RulesDefinition rulesDefinition);

	

}