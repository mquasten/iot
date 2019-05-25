package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.Validator;

import de.mq.iot.rule.RulesDefinition;

public class ValidationFactoryTest {
	
	private final ConversionService conversionService = new DefaultConversionService();
	
	final ValidationFactory validationFactory= new ValidationFactory(conversionService);
	
	@Test
	void defaultRuleinput() {
		
		
		assertMandatoryValidator(TimeValidatorImpl.class, validationFactory.validator(RulesDefinition.Id.DefaultDailyIotBatch, RulesDefinition.WORKINGDAY_ALARM_TIME_KEY));
		assertMandatoryValidator(TimeValidatorImpl.class, validationFactory.validator(RulesDefinition.Id.DefaultDailyIotBatch, RulesDefinition.HOLIDAY_ALARM_TIME_KEY));
		assertOptionalValidator(TimeValidatorImpl.class, validationFactory.validator(RulesDefinition.Id.DefaultDailyIotBatch, RulesDefinition.MIN_SUN_DOWN_TIME_KEY));
		
		assertMandatoryValidator(BooleanValidatorImpl.class, validationFactory.validator(RulesDefinition.Id.DefaultDailyIotBatch, RulesDefinition.UPDATE_MODE_KEY));
		assertMandatoryValidator(BooleanValidatorImpl.class, validationFactory.validator(RulesDefinition.Id.DefaultDailyIotBatch, RulesDefinition.TEST_MODE_KEY));
			
		
		
	}
	
	@Test
	void endOfDayRuleinput() {
		
		assertMandatoryValidator(NaturalNumberValidatorImpl.class, validationFactory.validator(RulesDefinition.Id.EndOfDayBatch, RulesDefinition.MAX_IP_COUNT_KEY));
		assertMandatoryValidator(NaturalNumberValidatorImpl.class, validationFactory.validator(RulesDefinition.Id.EndOfDayBatch, RulesDefinition.FIRST_IP_KEY));
		assertMandatoryValidator(NaturalNumberValidatorImpl.class, validationFactory.validator(RulesDefinition.Id.EndOfDayBatch, RulesDefinition.DAYS_BACK_KEY));
		assertMandatoryValidator(BooleanValidatorImpl.class, validationFactory.validator(RulesDefinition.Id.EndOfDayBatch, RulesDefinition.TEST_MODE_KEY));
		
	}

	private void assertMandatoryValidator(final Class<? extends Validator> type , final Validator validator) {
		assertTrue(type.isInstance(validator));
		assertTrue((Boolean)ReflectionTestUtils.getField(validator, "mandatory"));
	}
	
	private void assertOptionalValidator(final Class<? extends Validator> type , final Validator validator) {
		assertTrue(type.isInstance(validator));
		assertFalse((Boolean)ReflectionTestUtils.getField(validator, "mandatory"));
	}

}
