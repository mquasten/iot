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
import de.mq.iot.openweather.MeteorologicalDataService;
import de.mq.iot.rule.RulesDefinition;
import de.mq.iot.state.State;
import de.mq.iot.state.StateService;
import de.mq.iot.support.SunDownCalculationService;
@Configuration
class RuleConfiguration {
	
	private final static String DNS = "8.8.8.8";
	
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
	Collection<RulesAggregate<?>> rulesAggregates(final ConversionService conversionService, final ValidationFactory validationFactory,final SpecialdayService specialdayService, final StateService stateService, final MeteorologicalDataService meteorologicalDataService, final SunDownCalculationService sunDownCalculationService) {
		return Arrays.asList(rulesAggregate(conversionService,validationFactory, specialdayService, stateService, meteorologicalDataService, sunDownCalculationService), rulesAggregateEndOfDay(stateService,specialdayService, conversionService,validationFactory));
	}
	
	@Bean()
	ValidationFactory validationFactory(final ConversionService conversionService) {
		return new ValidationFactory(conversionService);
	}
	
	RulesAggregate<?> rulesAggregate(final ConversionService conversionService, final ValidationFactory validationFactory, final SpecialdayService specialdayService, final StateService stateService, final MeteorologicalDataService meteorologicalDataService, final SunDownCalculationService sunDownCalculationService) {
		
		
		
		return new SimpleRulesAggregateImpl<>(RulesDefinition.Id.DefaultDailyIotBatch, factsConsumer(),  new Rules(new InputDataMappingRuleImpl(conversionService, validationFactory) , new CalendarRuleImpl(specialdayService, dateSupplier() ), new TimerEventsRule(sunDownCalculationService), new SystemVariablesRuleImpl(stateService), new SystemVariablesUploadRuleImpl(stateService)), new TemperatureRuleImpl(meteorologicalDataService));
	}
	
	RulesAggregate<?> rulesAggregateEndOfDay(final StateService stateService, final SpecialdayService specialdayService, final ConversionService conversionService, final ValidationFactory validationFactory) {
		return new SimpleRulesAggregateImpl<>(RulesDefinition.Id.EndOfDayBatch, factsConsumerEndOfDay(),  new Rules(new InputDataMappingRuleImpl(conversionService, validationFactory), new HomematicGatewayFinderRuleImpl(stateService,DNS), new CleanupRuleImpl(specialdayService)));
	}
	
	
	final Consumer<Facts> factsConsumer() {
		return facts -> {
			
			facts.put(RulesAggregate.RULE_OUTPUT_MAP_FACT, new ArrayList<State<?>>());	
			facts.put(RulesAggregate.RULE_INPUT, new DefaultRuleInput());
			facts.put(RulesAggregate.RULE_CALENDAR, new Calendar()); 
		};
		
	}
	
	final Consumer<Facts> factsConsumerEndOfDay() {
		return facts -> {
			facts.put(RulesAggregate.RULE_OUTPUT_MAP_FACT, new ArrayList<String>());	
			facts.put(RulesAggregate.RULE_INPUT, new EndOfDayRuleInput());
		};
		
	}
	
	
	
	
	final Supplier<LocalDate> dateSupplier() {
		return  () -> LocalDate.now();
	}

}
