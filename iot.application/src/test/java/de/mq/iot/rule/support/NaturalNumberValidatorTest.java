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
		//errors.setNestedPath("-");
	
		
		validator.validate("42", errors);
		
		assertEquals(0, errors.getAllErrors().size());
		
		
		
	

	}
}
