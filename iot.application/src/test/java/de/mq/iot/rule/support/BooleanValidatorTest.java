package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.Validator;


class BooleanValidatorTest {

	
	
	private final Validator validator = new BooleanValidatorImpl(new DefaultConversionService(), true);

	private final Map<String, String> map = new HashMap<>();

	private final Errors errors = new MapBindingResult(map, "ruleInputData");

	private static final String FIELD = "testMode";

	@Test
	void validate() {
		validator.validate("false", errors);
		assertEquals(0, errors.getAllErrors().size());
		validator.validate("true", errors);
		assertEquals(0, errors.getAllErrors().size());
		validator.validate("0", errors);
		assertEquals(0, errors.getAllErrors().size());
		validator.validate("1", errors);
		assertEquals(0, errors.getAllErrors().size());
	}

	@Test
	void validateInvalid() {
		errors.setNestedPath(FIELD);
		validator.validate("2", errors);
		assertEquals(1, errors.getAllErrors().size());

		final FieldError fieldError = errors.getFieldErrors().stream().findAny().get();

		assertEquals(BooleanValidatorImpl.INVALID, fieldError.getCode());
		assertEquals(FIELD, fieldError.getField());
	}

	@Test
	void validateInvalidMandatory() {
		errors.setNestedPath(FIELD);
		validator.validate(null, errors);
		assertEquals(1, errors.getAllErrors().size());

		final FieldError fieldError = errors.getFieldErrors().stream().findAny().get();

		assertEquals(BooleanValidatorImpl.MANDATORY, fieldError.getCode());
		assertEquals(FIELD, fieldError.getField());
	}

	@Test
	void validateValidMandatory() {

		final Validator validator = new BooleanValidatorImpl(new DefaultConversionService(), false);
		validator.validate(null, errors);
		assertEquals(0, errors.getAllErrors().size());
	}

	@Test
	void supports() {
		assertTrue(validator.supports(String.class));
		assertFalse(validator.supports(Boolean.class));
	}

}
