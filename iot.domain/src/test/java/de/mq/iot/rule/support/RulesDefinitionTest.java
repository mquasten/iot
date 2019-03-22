package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.rule.RulesDefinition;
import de.mq.iot.rule.RulesDefinition.Id;

class RulesDefinitionTest {
	
	
	private static final String OPTIONAL_RULE_NAME = "optionalRuleName";
	private static final String UPDATE_MODE_VALUE = "true";
	private static final String WORKING_DAY_ALARM_TIME_VALUE = "5:15";
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
	
	@Test
	void inputData() {
	   assertEquals(0, ruleDefinition.inputData().size());	
	   
	   ruleDefinition.assign(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY, WORKING_DAY_ALARM_TIME_VALUE);
	   
	   ruleDefinition.assign(RulesDefinition.UPDATE_MODE_KEY, UPDATE_MODE_VALUE);
	   
	   assertEquals(2, ruleDefinition.inputData().size());	
	   
	   assertEquals(WORKING_DAY_ALARM_TIME_VALUE, ruleDefinition.inputData().get(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY));
	   
	   assertEquals(UPDATE_MODE_VALUE, ruleDefinition.inputData().get(RulesDefinition.UPDATE_MODE_KEY));
	   
	   
	   final Map<?,?> input =  (Map<?, ?>) ReflectionTestUtils.getField(ruleDefinition, "inputData");
	   assertEquals(1, input.size());
	   assertEquals(WORKING_DAY_ALARM_TIME_VALUE, input.get(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY));
	   
	   final Map<?,?> parameter  =  (Map<?, ?>) ReflectionTestUtils.getField(ruleDefinition, "parameter");
	   assertEquals(1, parameter.size());
	   assertEquals(UPDATE_MODE_VALUE, parameter.get(RulesDefinition.UPDATE_MODE_KEY));
	  
	   assertEquals(WORKING_DAY_ALARM_TIME_VALUE, ruleDefinition.value(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY));
	   assertEquals(UPDATE_MODE_VALUE, ruleDefinition.value(RulesDefinition.UPDATE_MODE_KEY));
	   
	   ruleDefinition.remove(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY);
	   ruleDefinition.remove(RulesDefinition.UPDATE_MODE_KEY);
	   
	   assertEquals(0, ruleDefinition.inputData().size());	
	   assertEquals(0, parameter.size());
	   assertEquals(0, input.size());
	}
	
	@Test
	void assignInvalidKey() {
		assertThrows(IllegalArgumentException.class , () -> ruleDefinition.assign("?", WORKING_DAY_ALARM_TIME_VALUE));
	}
	
	@Test
	void  optionalRules() {
		assertEquals(0, ruleDefinition.optionalRules().size());
		
		ruleDefinition.assignRule(OPTIONAL_RULE_NAME);
		
		assertEquals(1, ruleDefinition.optionalRules().size());
		assertEquals(OPTIONAL_RULE_NAME, ruleDefinition.optionalRules().iterator().next());
		
		ruleDefinition.removeOptionalRule(OPTIONAL_RULE_NAME);
		
		assertEquals(0, ruleDefinition.optionalRules().size());
	}
	
	

}
