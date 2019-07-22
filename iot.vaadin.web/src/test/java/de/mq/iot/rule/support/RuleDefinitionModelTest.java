package de.mq.iot.rule.support;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.StringUtils;

import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;
import de.mq.iot.rule.RulesDefinition;
import de.mq.iot.rule.support.RuleDefinitionModel.Events;

class RuleDefinitionModelTest {
	
	private static final String EVENT_TIME_VALUE = "11:11";
	private static final String HOLIDAY_VALUE = "7:15";
	private static final String WORKING_DAY_VALUE = "5:15";
	@SuppressWarnings("unchecked")
	private final Subject<Events, RuleDefinitionModel> subject = Mockito.mock(Subject.class);
    private final ValidationFactory validationFactory = new ValidationFactory(new DefaultConversionService());
	
	private final RuleDefinitionModel ruleDefinitionModel = new RuleDefinitionModelImpl(subject, validationFactory);
	
	private final Observer observer = Mockito.mock(Observer.class);
	
	private final RulesDefinition rulesDefinition = Mockito.mock(RulesDefinition.class);
	
	@Test
	void register() {
		ruleDefinitionModel.register(Events.AssignInput, observer);
		
		Mockito.verify(subject).register(Events.AssignInput, observer);
		
		
	}
	
	
	@Test
	void notifyObservers() {
		ruleDefinitionModel.notifyObservers(Events.AssignInput);
		
		Mockito.verify(subject).notifyObservers(Events.AssignInput);
	}
	
	@Test
	void selected() {
		
		assertFalse(ruleDefinitionModel.isSelected());
		assertEquals(Optional.empty(), ruleDefinitionModel.selected());
		
		ruleDefinitionModel.assignSelected(rulesDefinition);
		
		assertTrue(ruleDefinitionModel.isSelected());
		assertEquals(Optional.of(rulesDefinition), ruleDefinitionModel.selected());
	}
	
	@Test
	void  input() {
		assertEquals(0, ruleDefinitionModel.input().size());
		
		final  Map<String,String> inputData= new HashMap<>();
		inputData.put(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY, WORKING_DAY_VALUE);
		inputData.put(RulesDefinition.HOLIDAY_ALARM_TIME_KEY, HOLIDAY_VALUE);
		Mockito.when(rulesDefinition.inputData()).thenReturn(inputData);
		
	
		
		
		ruleDefinitionModel.assignSelected(rulesDefinition);
		
		Mockito.doReturn(RulesDefinition.Id.DefaultDailyIotBatch).when(rulesDefinition).id();
		final Map<String,String> results = ruleDefinitionModel.input().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		
		
		assertEquals(3,ruleDefinitionModel.input().size());
		RulesDefinition.Id.DefaultDailyIotBatch.input().forEach(key -> assertTrue(results.keySet().contains(key)));
		
		assertEquals(WORKING_DAY_VALUE, results.get(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY));
		assertEquals(HOLIDAY_VALUE, results.get(RulesDefinition.HOLIDAY_ALARM_TIME_KEY));
		assertFalse(StringUtils.hasText( results.get(RulesDefinition.MIN_SUN_DOWN_TIME_KEY)));
	    
	}
	
	
	@Test
	void  parameter() {
		assertEquals(0, ruleDefinitionModel.parameter().size());
		
		final  Map<String,String> inputData= new HashMap<>();
		inputData.put(RulesDefinition.UPDATE_MODE_KEY, Boolean.TRUE.toString());
		inputData.put(RulesDefinition.MIN_EVENT_TIME_KEY, EVENT_TIME_VALUE);
		Mockito.when(rulesDefinition.inputData()).thenReturn(inputData);
		
	
		
		
		ruleDefinitionModel.assignSelected(rulesDefinition);
		
		Mockito.doReturn(RulesDefinition.Id.DefaultDailyIotBatch).when(rulesDefinition).id();
		final Map<String,String> results = ruleDefinitionModel.parameter().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		
		
		assertEquals(3,ruleDefinitionModel.parameter().size());
		RulesDefinition.Id.DefaultDailyIotBatch.parameter().forEach(key -> assertTrue(results.keySet().contains(key)));
		
		assertEquals(Boolean.TRUE.toString(), results.get(RulesDefinition.UPDATE_MODE_KEY));
		assertEquals(EVENT_TIME_VALUE, results.get(RulesDefinition.MIN_EVENT_TIME_KEY));
		
		
		assertFalse(StringUtils.hasText( results.get(RulesDefinition.TEST_MODE_KEY)));
	    
	}
	
	@Test
	void definedOptionalRules() {
		assertEquals(0, ruleDefinitionModel.definedOptionalRules().size());
		Mockito.doReturn(Arrays.asList(RulesDefinition.TEMPERATURE_RULE_NAME)).when(rulesDefinition).optionalRules();
		Mockito.doReturn(RulesDefinition.Id.DefaultDailyIotBatch).when(rulesDefinition).id();
		ruleDefinitionModel.assignSelected(rulesDefinition);
		
		
		assertEquals(RulesDefinition.Id.DefaultDailyIotBatch.optionalRules().size(), ruleDefinitionModel.definedOptionalRules().size());
		RulesDefinition.Id.DefaultDailyIotBatch.optionalRules().forEach(rule -> assertTrue(ruleDefinitionModel.definedOptionalRules().contains(rule)));
	}
	
	@Test
	void locale() {
		assertEquals(Locale.GERMAN, ruleDefinitionModel.locale());
	}
	
	@Test
	void optionalRules() {
		assertEquals(0, ruleDefinitionModel.optionalRules().size());
		
		Mockito.doReturn(Arrays.asList(RulesDefinition.TEMPERATURE_RULE_NAME)).when(rulesDefinition).optionalRules();
		ruleDefinitionModel.assignSelected(rulesDefinition);
		
		assertEquals(1, ruleDefinitionModel.optionalRules().size());
		assertEquals(RulesDefinition.TEMPERATURE_RULE_NAME, ruleDefinitionModel.optionalRules().stream().findAny().get());
		
	}
	
	
	@Test
	void selectedInput() {
		assertFalse(ruleDefinitionModel.isInputSelected());
		assertEquals(Optional.empty(), ruleDefinitionModel.selectedInputKey());
		assertEquals(0, ruleDefinitionModel.selectedInputValue().length());
		
		ruleDefinitionModel.assignSelectedInput(new AbstractMap.SimpleImmutableEntry<>(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY, WORKING_DAY_VALUE));
		
		assertTrue(ruleDefinitionModel.isInputSelected());
		assertEquals(Optional.of(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY), ruleDefinitionModel.selectedInputKey());
		assertEquals(WORKING_DAY_VALUE, ruleDefinitionModel.selectedInputValue());
	}
	
	@Test
	void  assignInput() {
		
		ruleDefinitionModel.assignSelected(rulesDefinition);
		ruleDefinitionModel.assignSelectedInput((new AbstractMap.SimpleImmutableEntry<>(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY, "")));
		
		
		ruleDefinitionModel.assignInput(WORKING_DAY_VALUE);
		
		Mockito.verify(rulesDefinition).assign(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY, WORKING_DAY_VALUE);
	}
	
	@Test
	void  assignInputNotSelected() {
		
		ruleDefinitionModel.assignSelected(rulesDefinition);
	
		
		
		ruleDefinitionModel.assignInput(WORKING_DAY_VALUE);
		
		Mockito.verify(rulesDefinition, Mockito.never()).assign(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY, WORKING_DAY_VALUE);
	}
	
	@Test
	void validateInput() {
		
		Mockito.doReturn(RulesDefinition.Id.DefaultDailyIotBatch).when(rulesDefinition).id();
		
		ruleDefinitionModel.assignSelected(rulesDefinition);
		ruleDefinitionModel.assignSelectedInput(new AbstractMap.SimpleImmutableEntry<>(RulesDefinition.HOLIDAY_ALARM_TIME_KEY,null));
		
		
		assertEquals(Optional.of(TimeValidatorImpl.MANDATORY), ruleDefinitionModel.validateInput(""));
		
		assertEquals(Optional.of(TimeValidatorImpl.INVALID) , ruleDefinitionModel.validateInput("x:x"));
		assertEquals(Optional.empty(), ruleDefinitionModel.validateInput(HOLIDAY_VALUE));
	}
	
	
	@Test
	void selectedOptionalRule() {
		
		assertFalse(ruleDefinitionModel.isOptionalRuleSelected());
		ruleDefinitionModel.assignSelectedOptionalRule(RulesDefinition.TEMPERATURE_RULE_NAME);
		
		assertTrue(ruleDefinitionModel.isOptionalRuleSelected());
		
		Mockito.verify(subject).notifyObservers(Events.AssignOptionalRule);
		
		
	}
	
	@Test
	void addOptionalRule() {
	
		
		ruleDefinitionModel.assignSelected(rulesDefinition);
		
		ruleDefinitionModel.addOptionalRule(RulesDefinition.HOLIDAY_ALARM_TIME_KEY);
		
		Mockito.verify(rulesDefinition).assignRule(RulesDefinition.HOLIDAY_ALARM_TIME_KEY);
		
		Mockito.verify(subject).notifyObservers(Events.ChangeOptionalRules);
		
	}
	
	@Test
	void  removeOptionalRule() {
		ruleDefinitionModel.assignSelected(rulesDefinition);
		ruleDefinitionModel.assignSelectedOptionalRule(RulesDefinition.HOLIDAY_ALARM_TIME_KEY);
		
		ruleDefinitionModel.removeOptionalRule();
		
		
		Mockito.verify(rulesDefinition).removeOptionalRule(RulesDefinition.HOLIDAY_ALARM_TIME_KEY);
		
		Mockito.verify(subject).notifyObservers(Events.ChangeOptionalRules);
		
	}
	
	
	@Test
	void  validateInputAll() {
		
		Mockito.doReturn(RulesDefinition.Id.DefaultDailyIotBatch).when(rulesDefinition).id();
		ruleDefinitionModel.assignSelected(rulesDefinition);
		
		final Map<String,String> results = ruleDefinitionModel.validateInput().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		
		assertEquals(2, results.size());
		
		assertTrue(results.containsKey(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY));
		
		assertTrue(results.containsKey(RulesDefinition.HOLIDAY_ALARM_TIME_KEY));
		
		results.values().forEach(value -> assertEquals(TimeValidatorImpl.MANDATORY, value));
		
		
		final Map<String,String> values = new HashMap<>();
		
		values.put(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY, WORKING_DAY_VALUE);
		
		values.put(RulesDefinition.HOLIDAY_ALARM_TIME_KEY, HOLIDAY_VALUE);
		
		Mockito.doReturn(values).when(rulesDefinition).inputData();
		
		assertEquals(0, ruleDefinitionModel.validateInput().size());
		
	}
	
	
	@Test
	void selectedArgumentValue() {
		assertFalse(StringUtils.hasText(ruleDefinitionModel.selectedArgumentValue()));
		
		ruleDefinitionModel.assignSelectedArgument(new AbstractMap.SimpleImmutableEntry<>(RulesDefinition.UPDATE_MODE_KEY, Boolean.TRUE.toString()));
		
		assertEquals(Boolean.TRUE.toString(), ruleDefinitionModel.selectedArgumentValue());
		
		Mockito.verify(subject).notifyObservers(Events.AssignArgument);
		
	}
	
	@Test
	void  isArgumentSelected() {
		assertFalse(ruleDefinitionModel.isArgumentSelected());
		
		ruleDefinitionModel.assignSelectedArgument(new AbstractMap.SimpleImmutableEntry<>(RulesDefinition.UPDATE_MODE_KEY, Boolean.TRUE.toString()));
		
		assertTrue(ruleDefinitionModel.isArgumentSelected());
	}
	
	@Test
	void validateArgument() {
		Mockito.doReturn(RulesDefinition.Id.DefaultDailyIotBatch).when(rulesDefinition).id();
		ruleDefinitionModel.assignSelected(rulesDefinition);

		
		
		ruleDefinitionModel.assignSelectedArgument(new AbstractMap.SimpleImmutableEntry<>(RulesDefinition.UPDATE_MODE_KEY, Boolean.TRUE.toString()));
		
		assertEquals(Optional.of(TimeValidatorImpl.INVALID), ruleDefinitionModel.validateArgument("xxx"));
		
		
		assertEquals(Optional.empty(), ruleDefinitionModel.validateArgument(Boolean.FALSE.toString()));
	}
	
	@Test
	void assignArgument() {
		ruleDefinitionModel.assignSelected(rulesDefinition);
		ruleDefinitionModel.assignSelectedArgument(new AbstractMap.SimpleImmutableEntry<>(RulesDefinition.UPDATE_MODE_KEY, ""));
		
		
		ruleDefinitionModel.assignArgument(Boolean.TRUE.toString());
		
		
		Mockito.verify(rulesDefinition).assign(RulesDefinition.UPDATE_MODE_KEY, Boolean.TRUE.toString());
	
	}
	
}
