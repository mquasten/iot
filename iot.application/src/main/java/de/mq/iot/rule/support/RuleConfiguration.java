package de.mq.iot.rule.support;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;

import org.jeasy.rules.api.Rules;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.rule.RulesDefinition;
import de.mq.iot.state.StateService;
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
	
	@Bean()
	@Scope(value = "prototype")
	Collection<RulesAggregate> rulesAggregates(final ConversionService conversionService,final SpecialdayService specialdayService, final StateService stateService) {
		return Arrays.asList(rulesAggregate(conversionService,specialdayService, stateService));
	}
	
	
	
	private RulesAggregate rulesAggregate(final ConversionService conversionService, final SpecialdayService specialdayService, final StateService stateService) {
		return new SimpleRulesAggregateImpl(RulesDefinition.Id.DefaultDailyIotBatch, new Rules(new InputDataMappingRuleImpl(conversionService) , new CalendarRuleImpl(specialdayService, () -> LocalDate.now()), new SystemVariablesRuleImpl(stateService)));
	}
	
	

}
