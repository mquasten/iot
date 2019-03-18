package de.mq.iot.rule.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import de.mq.iot.rule.RulesDefinition;

@Document
public class RuleDefinitionImpl implements RulesDefinition {

	@org.springframework.data.annotation.Id
	private Id id;
	
	private final Map<String,String> inputData = new HashMap<>();
	
	
	private Collection<String> optionalRules = new ArrayList<>();
	
	@Transient
	private Map<String,String> parameter = new HashMap<>();
	
	@Override
	public Id id() {
		return id;
	}

	@Override
	public Map<String, String> inputData() {
		final Map<String,String> data = new HashMap<>();
		data.putAll(inputData);
		data.putAll(parameter);
		return Collections.unmodifiableMap(data);
	}
	
	public void assign(final String key, final String value) {
		if ( id.input().contains(key)) {
			inputData.put(key, value);
			return;
		}
		
		if ( id.parameter().contains(key)) {
			parameter.put(key, value);
			return;
		}
		throw new IllegalArgumentException(String.format("Key %s is undefined for %s.", key, id));
	}
	
	public void remove(final String key) {
		inputData.remove(key);
		inputData.remove(key);
	}
	
	
	

	@Override
	public Collection<String> optionalRules() {
		return Collections.unmodifiableCollection(optionalRules);
	}

}
