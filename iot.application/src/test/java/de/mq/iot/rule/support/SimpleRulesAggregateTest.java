package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


import de.mq.iot.rule.support.RulesDefinition.Id;
import de.mq.iot.state.State;

class SimpleRulesAggregateTest {
	
	private static final String USED_OPTIONAL_RULE_NAME = "usedOptionalRule";

	private static final String MANDATORY_RULE_NAME = "mandatoryRule";

	private final Rule mandatoryRule = Mockito.mock(Rule.class);
	
	private final Rule usedOptionalRule = Mockito.mock(Rule.class);
	
	private final Rule notUsedOptionalRule = Mockito.mock(Rule.class);
	
	private final Rules rules = new Rules(mandatoryRule);
	private final RulesAggregate rulesAggregate = new SimpleRulesAggregateImpl(RulesDefinition.Id.DefaultDailyIotBatch, rules, usedOptionalRule, notUsedOptionalRule);

    
	private final RulesDefinition rulesDefinition = Mockito.mock(RulesDefinition.class);
	
	private final Map<String,String>  inputData = new HashMap<>();
	
	
	private final State<?> state = Mockito.mock(State.class);
	
	@BeforeEach
	void setup() throws Exception {
		
		inputData.put("useTestMode", "true");
	
		Mockito.when(mandatoryRule.getName()).thenReturn(MANDATORY_RULE_NAME);
		Mockito.when(mandatoryRule.getPriority()).thenReturn(0);
		Mockito.when(usedOptionalRule.getName()).thenReturn(USED_OPTIONAL_RULE_NAME);
		Mockito.when(notUsedOptionalRule.getName()).thenReturn("notUsedOptionalRule");
		Arrays.asList(mandatoryRule,usedOptionalRule,notUsedOptionalRule).forEach(rule -> Mockito.when(rule.evaluate(Mockito.any())).thenReturn(true));
		
		
		Arrays.asList(mandatoryRule,usedOptionalRule,notUsedOptionalRule).forEach(rule -> Mockito.when(rule.compareTo(Mockito.any())).thenReturn(-1));
		
		Mockito.when(rulesDefinition.inputData()).thenReturn(inputData);
		Mockito.when(rulesDefinition.optionalRules()).thenReturn(Arrays.asList(USED_OPTIONAL_RULE_NAME));
		Mockito.doReturn(Id.DefaultDailyIotBatch).when(rulesDefinition).id();
		
		
		
		
		Mockito.doAnswer(answer -> {
			final Facts facts = answer.getArgument(0);
			final Collection<State<?>> states = facts.get(RulesAggregate.RULE_OUTPUT_MAP_FACT);
			states.add(state);
			return null;
			
		}).when(mandatoryRule).execute(Mockito.any());;
		
		
		
	}
	
	
	@Test
	void id() {
		assertEquals(RulesDefinition.Id.DefaultDailyIotBatch, rulesAggregate.id());
		
		rules.register(notUsedOptionalRule);
		
	}
	

	@Test
	void fire() throws Exception {
		rulesAggregate.with(rulesDefinition);
		final RulesAggregateResult result = rulesAggregate.fire();
		
		assertFalse(result.exception().isPresent());
		assertEquals(1, result.states().size());
		assertEquals(state, result.states().iterator().next());
		
		assertEquals(2, result.processedRules().size());
		assertTrue(result.processedRules().contains(MANDATORY_RULE_NAME));
		assertTrue(result.processedRules().contains(USED_OPTIONAL_RULE_NAME));
		
		
		ArgumentCaptor<Facts> factsCapture = ArgumentCaptor.forClass(Facts.class);
		
		Mockito.verify(mandatoryRule, Mockito.times(1)).evaluate(factsCapture.capture());
		Mockito.verify(usedOptionalRule, Mockito.times(1)).evaluate(factsCapture.capture());
		
		Mockito.verify(mandatoryRule, Mockito.times(1)).execute(factsCapture.capture());
		Mockito.verify(usedOptionalRule, Mockito.times(1)).execute(factsCapture.capture());
		
		
		
		factsCapture.getAllValues().forEach(facts -> assertEquals(inputData, facts.get(RulesAggregate.RULE_INPUT_MAP_FACT)));
				
	}
	
	@Test
	void fireInputDataAware()  {
		rulesAggregate.with(rulesDefinition);
		assertThrows(IllegalArgumentException.class,() -> rulesAggregate.with(rulesDefinition));
	}
	
	@Test
	void fireIdsNotIdentical()  {
		Mockito.when(rulesDefinition.id()).thenReturn(null);
		
		assertThrows(IllegalArgumentException.class, () -> rulesAggregate.with(rulesDefinition));
		
	}
}
