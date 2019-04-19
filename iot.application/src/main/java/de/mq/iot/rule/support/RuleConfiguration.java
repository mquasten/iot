package de.mq.iot.rule.support;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.rule.RulesDefinition;
import de.mq.iot.state.State;
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
	
	
	
	RulesAggregate rulesAggregate(final ConversionService conversionService, final SpecialdayService specialdayService, final StateService stateService) {
		
		
		
		return new SimpleRulesAggregateImpl(RulesDefinition.Id.DefaultDailyIotBatch, factsConsumer(),  new Rules(new InputDataMappingRuleImpl(conversionService) , new CalendarRuleImpl(specialdayService, dateSupplier() ), new SystemVariablesRuleImpl(stateService), new SystemVariablesUploadRuleImpl(stateService)));
	}
	
	
	final Consumer<Facts> factsConsumer() {
		return facts -> {
			
			facts.put(RulesAggregate.RULE_OUTPUT_MAP_FACT, new ArrayList<State<?>>());	
			facts.put(RulesAggregate.RULE_INPUT, new DefaultRuleInput());
			facts.put(RulesAggregate.RULE_CALENDAR, new Calendar()); 
		};
		
	}
	
	
	final Supplier<LocalDate> dateSupplier() {
		return  () -> LocalDate.now();
	}

}
