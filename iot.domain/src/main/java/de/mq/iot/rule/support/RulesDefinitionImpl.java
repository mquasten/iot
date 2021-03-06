package de.mq.iot.rule.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import de.mq.iot.rule.RulesDefinition;

@Document(collection="RulesDefinition")
public class RulesDefinitionImpl implements RulesDefinition {

	RulesDefinitionImpl(final Id id) {
		Assert.notNull(id, "Id is requiers.");
		this.id = id;
	}

	@SuppressWarnings("unused")
	private RulesDefinitionImpl() {
		this.id =null;
	}


	@org.springframework.data.annotation.Id
	private final Id id;
	
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
	@Override
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
	
	@Override
	public String value(final String key ) {
		if ( inputData.containsKey(key)) {
			return  inputData.get(key);
		}
		return parameter.get(key);
	}

	@Override
	public void remove(final String key) {
		inputData.remove(key);
		parameter.remove(key);
	}
	
	@Override
	public void assignRule(final String rule) {
		optionalRules.add(rule);
	}

	@Override
	public Collection<String> optionalRules() {
		return Collections.unmodifiableCollection(optionalRules);
	}
	
	@Override
	public void removeOptionalRule(final String rule) {
		optionalRules.remove(rule);
	}

	@Override
	public int hashCode() {
		if (invalid(this)) {
			return super.hashCode();
		}
		return id.hashCode();
	}
	
	private boolean invalid(RulesDefinition rulesDefinition) {
		return rulesDefinition.id() == null;
	}

	@Override
	public boolean equals(final Object obj) {
		if(invalid(this)) {
			return super.equals(obj);
		}
		if (!(obj instanceof RulesDefinition)) {
			return super.equals(obj);
			
		}
		final RulesDefinition other = (RulesDefinition) obj;
		if (invalid(other)) {
			return super.equals(obj);
		}
		return id == other.id();
	}
	
	
	

}
