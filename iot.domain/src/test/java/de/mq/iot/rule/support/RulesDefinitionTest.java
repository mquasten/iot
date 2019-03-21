package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import de.mq.iot.rule.RulesDefinition;
import de.mq.iot.rule.RulesDefinition.Id;

class RulesDefinitionTest {
	
	
	private final RulesDefinition ruleDefinition = new RuleDefinitionImpl(RulesDefinition.Id.DefaultDailyIotBatch);
	
	
	@Test
	void idValues() {
		assertEquals(1, RulesDefinition.Id.values().length);
		
		Arrays.asList(RulesDefinition.Id.values()).forEach(value -> assertEquals(value, RulesDefinition.Id.valueOf(value.name())));
	}
	
	@Test
	void id() {
		assertEquals(Id.DefaultDailyIotBatch, ruleDefinition.id());
	}
	

}
