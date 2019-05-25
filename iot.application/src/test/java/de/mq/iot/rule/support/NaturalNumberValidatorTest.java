package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.validation.Errors;

import org.springframework.validation.MapBindingResult;

import org.springframework.validation.Validator;


class NaturalNumberValidatorTest {
	
	private static final int MAX = 999;

	private static final int MIN = 0;
	
	private final Map<String, String> map = new HashMap<>();
	private final Errors errors = new MapBindingResult(map, "ruleInputData");

	private ConversionService conversionService = new DefaultConversionService();
	
	private final Validator validator = new NaturalNumberValidatorImpl(conversionService, true, MIN, MAX);
	
	@Test
	void supports() {
		assertTrue(validator.supports(String.class));
		assertFalse(validator.supports(Number.class));
	}
	
	@Test
	void validate() {
		
		validator.validate("42", errors);
		
		assertEquals(0, errors.getAllErrors().size());
		
		
	}
	
	@Test
	void validateInvalid() {
		
		validator.validate("42x", errors);
		
		assertEquals(1, errors.getAllErrors().size());
		
		assertEquals(NaturalNumberValidatorImpl.INVALID, errors.getAllErrors().get(0).getCode());
		
		
	}
	
	@Test
	void validateMin() {
		validator.validate("-1", errors);
		
		assertEquals(1, errors.getAllErrors().size());
		
		assertEquals(NaturalNumberValidatorImpl.MIN, errors.getAllErrors().get(0).getCode());
		assertEquals(1, errors.getAllErrors().get(0).getArguments().length);
		assertEquals(MIN, errors.getAllErrors().get(0).getArguments()[0]);
	}
	
	@Test
	void validateMax() {
		validator.validate("1000", errors);
		
		assertEquals(1, errors.getAllErrors().size());
		
		assertEquals(NaturalNumberValidatorImpl.MAX, errors.getAllErrors().get(0).getCode());
		assertEquals(1, errors.getAllErrors().get(0).getArguments().length);
		assertEquals(MAX, errors.getAllErrors().get(0).getArguments()[0]);
	}
	
	@Test
	void validateMandatory() {
		validator.validate(" ", errors);
		
		assertEquals(1, errors.getAllErrors().size());
		assertEquals(NaturalNumberValidatorImpl.MANDATORY, errors.getAllErrors().get(0).getCode());
	}
	
	@Test
	void validateNotMandatory() {
		Validator validator = new NaturalNumberValidatorImpl(conversionService, false, MIN, MAX);
		validator.validate(" ", errors);
		
		assertEquals(0, errors.getAllErrors().size());
		
	}
}
