package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.rule.RulesDefinition;


class RuleConfigurationTest {
	
	private static final int HOUR = 5;
	private static final int MINUTES = 15;
	private final RuleConfiguration ruleConfiguration = new RuleConfiguration();
	
	@Test
	void conversionService() {
		final ConversionService conversionService = ruleConfiguration.conversionService();
		final LocalTime result = conversionService.convert(String.format("%s:%s",HOUR,MINUTES), LocalTime.class);
		
		assertEquals(HOUR, result.getHour());
		assertEquals(MINUTES, result.getMinute());
		
		
		assertNull(conversionService.convert(String.format("%s", HOUR), LocalTime.class));
	}
	
	
	@Test
	void  rulesAggregate() throws Exception {
		final ConversionService conversionService = Mockito.mock(ConversionService.class);
		final Collection<RulesAggregate> aggregates = ruleConfiguration.rulesAggregates(conversionService, null,null,null);
		assertEquals(1, aggregates.size());
		final RulesAggregate rulesAggregate = aggregates.iterator().next();
		
		assertEquals(RulesDefinition.Id.DefaultDailyIotBatch, rulesAggregate.id());
		
		final Rules rules = (Rules) ReflectionTestUtils.getField(rulesAggregate, "rules");
		final List<org.jeasy.rules.api.Rule> rulesList = new ArrayList<>();
		rules.forEach(rule -> rulesList.add(rule) );
		
		assertEquals(4, rulesList.size());
		
		final List<String> ruleNames = rulesList.stream().map(rule -> rule.getName()).collect(Collectors.toList());
		
		assertEquals(InputDataMappingRuleImpl.class.getAnnotation(org.jeasy.rules.annotation.Rule.class).name(), ruleNames.get(0));
		assertEquals(CalendarRuleImpl.class.getAnnotation(org.jeasy.rules.annotation.Rule.class).name(), ruleNames.get(1));
		assertEquals(SystemVariablesRuleImpl.class.getAnnotation(org.jeasy.rules.annotation.Rule.class).name(), ruleNames.get(2));
		assertEquals(SystemVariablesUploadRuleImpl.class.getAnnotation(org.jeasy.rules.annotation.Rule.class).name(), ruleNames.get(3));
	
		final Collection<?> optionalRules =  (Collection<?>) ReflectionTestUtils.getField(rulesAggregate, "optionalRules");
		assertEquals(1, optionalRules.size());
		
		assertTrue(optionalRules.iterator().next() instanceof TemperatureRuleImpl);
		

	
	}
	@Test
	void dateSupplier() {
		assertEquals(LocalDate.now(), ruleConfiguration.dateSupplier().get());
	}
	
	@Test
	void factsConsumer() {
		final Facts facts = new Facts();
		ruleConfiguration.factsConsumer().accept(facts);
		
		assertTrue(facts.get(RulesAggregate.RULE_OUTPUT_MAP_FACT) instanceof Collection);
		assertTrue(facts.get(RulesAggregate.RULE_INPUT) instanceof DefaultRuleInput);
		assertTrue(facts.get(RulesAggregate.RULE_CALENDAR) instanceof Calendar);
	}

}
