package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.Validator;

class TimeValidatorTest {

	private static final String FIELD = "alarmtime";

	private final Map<String, String> map = new HashMap<>();

	private final Errors errors = new MapBindingResult(map, "ruleInputData");

	private final Validator validator = new TimeValidatorImpl(true);

	private final String validTimestamp = " 5:15 ";

	@Test
	void validTimeStamp() {

		validator.validate(validTimestamp, errors);

		assertEquals(0, errors.getAllErrors().size());
	}

	@Test
	void invalidTimeStampNullMandatory() {
		errors.setNestedPath(FIELD);
		validator.validate(null, errors);

		assertEquals(1, errors.getAllErrors().size());

		FieldError objectError = errors.getFieldErrors().stream().findAny().get();

		assertEquals(TimeValidatorImpl.MANDATORY, objectError.getCode());
		assertEquals(FIELD, objectError.getField());

	}

	@Test
	void validTimeStampNull() {
		final Validator validator = new TimeValidatorImpl(false);
		validator.validate(null, errors);
		assertEquals(0, errors.getAllErrors().size());
	}

	@Test
	void invalidArraysLength() {
		errors.setNestedPath(FIELD);
		validator.validate("5", errors);

		assertInvalid();
	}

	@Test
	void invalidNotNumber() {
		errors.setNestedPath(FIELD);
		validator.validate("5:xx", errors);

		assertInvalid();
	}

	@Test
	void invalidRange() {
		errors.setNestedPath(FIELD);
		validator.validate("25:00", errors);

		assertInvalid();
	}

	@Test
	void invalidRangeLowerLimit() {
		errors.setNestedPath(FIELD);
		validator.validate("-1:00", errors);

		assertInvalid();
	}

	private void assertInvalid() {
		assertEquals(1, errors.getAllErrors().size());
		final FieldError objectError = errors.getFieldErrors().stream().findAny().get();

		assertEquals(TimeValidatorImpl.INVALID, objectError.getCode());
		assertEquals(FIELD, objectError.getField());
	}

	@Test
	void supports() {
		assertTrue(validator.supports(String.class));
		assertFalse(validator.supports(Date.class));
	}

}
