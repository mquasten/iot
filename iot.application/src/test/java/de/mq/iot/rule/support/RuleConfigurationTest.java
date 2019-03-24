package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.core.RuleProxy;
import org.jeasy.rules.support.ConditionalRuleGroup;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StringUtils;

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
		final RulesAggregate rulesAggregate = ruleConfiguration.rulesAggregate(conversionService);
		
		assertEquals(RulesDefinition.Id.DefaultDailyIotBatch, rulesAggregate.id());
		
		final Rules rules = (Rules) ReflectionTestUtils.getField(rulesAggregate, "rules");
		final Collection<Rule> rulesList = new ArrayList<>();
		rules.forEach(rule -> rulesList.add(rule) );
		
		assertEquals(1, rulesList.size());
		
		ConditionalRuleGroup group = (ConditionalRuleGroup) rulesList.iterator().next();
		
		
		@SuppressWarnings("unchecked")
		final Collection<Rule> results =   (Collection<Rule>) ReflectionTestUtils.getField(group, "rules");
		
		assertEquals(1, results.size());
		
		
		final List<String> ruleNames = results.stream().map(rule -> RuleProxy.asRule(rule).getName()).collect(Collectors.toList());
		
		assertTrue(StringUtils.hasText(InputDataMappingRuleImpl.class.getAnnotation( org.jeasy.rules.annotation.Rule.class).name()));
		
		assertTrue(ruleNames.contains(InputDataMappingRuleImpl.class.getAnnotation( org.jeasy.rules.annotation.Rule.class).name()));
		
	}

}
