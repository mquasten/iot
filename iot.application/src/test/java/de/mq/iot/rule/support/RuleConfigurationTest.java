package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.Validator;

import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.openweather.MeteorologicalDataService;
import de.mq.iot.rule.RulesDefinition;
import de.mq.iot.state.StateService;
import de.mq.iot.support.SunDownCalculationService;

class RuleConfigurationTest {

	private static final int HOUR = 5;
	private static final int MINUTES = 15;
	private final RuleConfiguration ruleConfiguration = new RuleConfiguration();

	@Test
	void conversionService() {
		final ConversionService conversionService = ruleConfiguration.conversionService();
		final LocalTime result = conversionService.convert(String.format("%s:%s", HOUR, MINUTES), LocalTime.class);

		assertEquals(HOUR, result.getHour());
		assertEquals(MINUTES, result.getMinute());

		assertNull(conversionService.convert(String.format("%s", HOUR), LocalTime.class));
	}

	@Test
	void rulesAggregate() throws Exception {
		final ConversionService conversionService = Mockito.mock(ConversionService.class);
		final SpecialdayService specialdayService = Mockito.mock(SpecialdayService.class);
		final StateService stateService = Mockito.mock(StateService.class);
		final MeteorologicalDataService meteorologicalDataService = Mockito.mock(MeteorologicalDataService.class);
		final SunDownCalculationService sunDownCalculationService = Mockito.mock(SunDownCalculationService.class);

		final ValidationFactory validationFactory = Mockito.mock(ValidationFactory.class);

		final Collection<RulesAggregate<?>> aggregates = ruleConfiguration.rulesAggregates(conversionService, validationFactory, specialdayService, stateService, meteorologicalDataService, sunDownCalculationService);
		assertEquals(2, aggregates.size());
		final RulesAggregate<?> rulesAggregate = new ArrayList<>(aggregates).get(0);

		assertEquals(RulesDefinition.Id.DefaultDailyIotBatch, rulesAggregate.id());

		final Rules rules = (Rules) ReflectionTestUtils.getField(rulesAggregate, "rules");
		final List<org.jeasy.rules.api.Rule> rulesList = new ArrayList<>();
		rules.forEach(rule -> rulesList.add(rule));

		assertEquals(5, rulesList.size());

		final List<String> ruleNames = rulesList.stream().map(rule -> rule.getName()).collect(Collectors.toList());

		assertEquals(InputDataMappingRuleImpl.class.getAnnotation(org.jeasy.rules.annotation.Rule.class).name(), ruleNames.get(0));
		assertEquals(CalendarRuleImpl.class.getAnnotation(org.jeasy.rules.annotation.Rule.class).name(), ruleNames.get(1));

		assertEquals(TimerEventsRule.class.getAnnotation(org.jeasy.rules.annotation.Rule.class).name(), ruleNames.get(2));
		assertEquals(SystemVariablesRuleImpl.class.getAnnotation(org.jeasy.rules.annotation.Rule.class).name(), ruleNames.get(3));
		assertEquals(SystemVariablesUploadRuleImpl.class.getAnnotation(org.jeasy.rules.annotation.Rule.class).name(), ruleNames.get(4));

		final Collection<?> optionalRules = (Collection<?>) ReflectionTestUtils.getField(rulesAggregate, "optionalRules");
		assertEquals(1, optionalRules.size());

		assertTrue(optionalRules.iterator().next() instanceof TemperatureRuleImpl);

		final RulesAggregate<?> endOfDay = new ArrayList<>(aggregates).get(1);
		assertEquals(RulesDefinition.Id.EndOfDayBatch, endOfDay.id());

		final Rules rulesEndOfDay = (Rules) ReflectionTestUtils.getField(endOfDay, "rules");
		final List<org.jeasy.rules.api.Rule> rulesListEndOdDay = new ArrayList<>();
		rulesEndOfDay.forEach(rule -> rulesListEndOdDay.add(rule));

		assertEquals(1, rulesListEndOdDay.size());

		final List<String> ruleNamesEndOfDay = rulesListEndOdDay.stream().map(rule -> rule.getName()).collect(Collectors.toList());
		assertEquals(1, ruleNamesEndOfDay.size());
		assertEquals(HomematicGatewayFinderRuleImpl.class.getAnnotation(org.jeasy.rules.annotation.Rule.class).name(), ruleNamesEndOfDay.get(0));

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

	@Test
	void validationFactory() {
		final ValidationFactory validationFactory = ruleConfiguration.validationFactory(new DefaultConversionService());
		final Validator validator = validationFactory.validator(RulesDefinition.Id.DefaultDailyIotBatch, RulesDefinition.WORKINGDAY_ALARM_TIME_KEY);

		final Errors errors = new MapBindingResult(new HashMap<>(), RulesAggregate.RULE_INPUT_MAP_FACT);
		validator.validate("12:00", errors);

		assertEquals(0, errors.getAllErrors().size());
	}

}
