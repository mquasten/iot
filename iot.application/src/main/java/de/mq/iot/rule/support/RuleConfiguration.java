package de.mq.iot.rule.support;

import java.time.LocalTime;

import org.jeasy.rules.api.Rules;
import org.jeasy.rules.support.ConditionalRuleGroup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import de.mq.iot.rule.RulesDefinition;
@Configuration
class RuleConfiguration {
	@Bean
	ConversionService conversionService() {
	DefaultConversionService conversionService= new DefaultConversionService();
			conversionService.addConverter(String.class, LocalTime.class, stringValue -> {
				String[] values = TimeValidatorImpl.splitTimeString(stringValue);
				if( values.length!=2) {
					return null;
				}
				return LocalTime.of(conversionService.convert(values[0], Integer.class), conversionService.convert(values[1], Integer.class));
			});
		return conversionService;
	}
	
	@Bean
	@Scope(scopeName="protoType")
	RulesAggregate rulesAggregate(final ConversionService conversionService) {
	
		final ConditionalRuleGroup group = new ConditionalRuleGroup("ConditionalDefaultDailyIotBatchRuleGroup");
		group.addRule(new InputDataMappingRuleImpl(conversionService));
		final Rules rules = new Rules(group);
		return new SimpleRulesAggregateImpl(RulesDefinition.Id.DefaultDailyIotBatch, rules);
	}
	
	

}
