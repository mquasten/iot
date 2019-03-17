package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class RulesDefinitionTest {
	
	@Test
	void id() {
		assertEquals(1, RulesDefinition.Id.values().length);
		
		Arrays.asList(RulesDefinition.Id.values()).forEach(value -> assertEquals(value, RulesDefinition.Id.valueOf(value.name())));
	}

}
