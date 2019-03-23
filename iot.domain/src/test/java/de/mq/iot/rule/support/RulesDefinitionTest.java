package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
	private final RulesDefinition rulesDefinition = new RulesDefinitionImpl(RulesDefinition.Id.DefaultDailyIotBatch);
	
	
	@Test
	void idValues() {
		assertEquals(2, RulesDefinition.Id.values().length);
		
		Arrays.asList(RulesDefinition.Id.values()).forEach(value -> assertEquals(value, RulesDefinition.Id.valueOf(value.name())));
	}
	
	@Test
	void id() {
		assertEquals(Id.DefaultDailyIotBatch, rulesDefinition.id());
	}
	
	@Test
	void inputData() {
	   assertEquals(0, rulesDefinition.inputData().size());	
	   
	   rulesDefinition.assign(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY, WORKING_DAY_ALARM_TIME_VALUE);
	   
	   rulesDefinition.assign(RulesDefinition.UPDATE_MODE_KEY, UPDATE_MODE_VALUE);
	   
	   assertEquals(2, rulesDefinition.inputData().size());	
	   
	   assertEquals(WORKING_DAY_ALARM_TIME_VALUE, rulesDefinition.inputData().get(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY));
	   
	   assertEquals(UPDATE_MODE_VALUE, rulesDefinition.inputData().get(RulesDefinition.UPDATE_MODE_KEY));
	   
	   
	   final Map<?,?> input =  (Map<?, ?>) ReflectionTestUtils.getField(rulesDefinition, "inputData");
	   assertEquals(1, input.size());
	   assertEquals(WORKING_DAY_ALARM_TIME_VALUE, input.get(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY));
	   
	   final Map<?,?> parameter  =  (Map<?, ?>) ReflectionTestUtils.getField(rulesDefinition, "parameter");
	   assertEquals(1, parameter.size());
	   assertEquals(UPDATE_MODE_VALUE, parameter.get(RulesDefinition.UPDATE_MODE_KEY));
	  
	   assertEquals(WORKING_DAY_ALARM_TIME_VALUE, rulesDefinition.value(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY));
	   assertEquals(UPDATE_MODE_VALUE, rulesDefinition.value(RulesDefinition.UPDATE_MODE_KEY));
	   
	   rulesDefinition.remove(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY);
	   rulesDefinition.remove(RulesDefinition.UPDATE_MODE_KEY);
	   
	   assertEquals(0, rulesDefinition.inputData().size());	
	   assertEquals(0, parameter.size());
	   assertEquals(0, input.size());
	}
	
	@Test
	void assignInvalidKey() {
		assertThrows(IllegalArgumentException.class , () -> rulesDefinition.assign("?", WORKING_DAY_ALARM_TIME_VALUE));
	}
	
	@Test
	void  optionalRules() {
		assertEquals(0, rulesDefinition.optionalRules().size());
		
		rulesDefinition.assignRule(OPTIONAL_RULE_NAME);
		
		assertEquals(1, rulesDefinition.optionalRules().size());
		assertEquals(OPTIONAL_RULE_NAME, rulesDefinition.optionalRules().iterator().next());
		
		rulesDefinition.removeOptionalRule(OPTIONAL_RULE_NAME);
		
		assertEquals(0, rulesDefinition.optionalRules().size());
	}
	
	@Test
	void hash() {
		assertTrue(rulesDefinition.hashCode() == RulesDefinition.Id.DefaultDailyIotBatch.hashCode());
		
		setInvalid(rulesDefinition);
		
		
		assertEquals(System.identityHashCode(rulesDefinition), rulesDefinition.hashCode());
	}

	private void setInvalid(RulesDefinition rulesDefinition) {
		ReflectionTestUtils.setField(rulesDefinition, "id", null);
	}
	
	@Test
	void equals() {
		final RulesDefinition other = new RulesDefinitionImpl(RulesDefinition.Id.DefaultDailyIotBatch);
		assertTrue(rulesDefinition.equals(other));
		
		setInvalid(rulesDefinition);
		
		assertFalse(other.equals(rulesDefinition));
		
		assertFalse(rulesDefinition.equals(other));
		
		setInvalid(other);
		
		assertTrue(other.equals(other));
		
		assertTrue(rulesDefinition.equals(rulesDefinition));
		
		assertFalse(other.equals(rulesDefinition));
		
		assertFalse(rulesDefinition.equals(other));
		
		assertFalse(rulesDefinition.equals(null));
		
		
		assertFalse(new RulesDefinitionImpl(RulesDefinition.Id.DefaultDailyIotBatch).equals(rulesDefinition));
	}

}
