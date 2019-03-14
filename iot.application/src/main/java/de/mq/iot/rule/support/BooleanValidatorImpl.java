package de.mq.iot.rule.support;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

class BooleanValidatorImpl implements Validator {

	static final String INVALID = "invalid";

	static final String MANDATORY = "mandatory";

	private final ConversionService conversionService;

	private final boolean mandatory;

	BooleanValidatorImpl(final ConversionService conversionService, final boolean mandatory) {

		this.mandatory = mandatory;
		this.conversionService=conversionService;
	}

	@Override
	public boolean supports(final Class<?> clazz) {
		return String.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, final Errors errors) {
		final String value = (String) target;
		if ((value == null) && (!mandatory)) {
			return;
		}

		if (value == null) {
			errors.rejectValue(null, MANDATORY);
			return;
		}
		try {
			conversionService.convert(value, Boolean.class);
		} catch (final ConversionFailedException ce) {
			errors.rejectValue(null, INVALID);
		}
	}

}
