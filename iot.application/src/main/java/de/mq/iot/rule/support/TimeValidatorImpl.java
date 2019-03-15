package de.mq.iot.rule.support;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import org.springframework.validation.Validator;

class TimeValidatorImpl implements Validator {

	static final String INVALID = "invalid";

	static final String MANDATORY = "mandatory";

	static final String DELIMITER = "[:.|,]";

	private ConversionService conversionService;

	private final boolean mandatory;

	TimeValidatorImpl(final ConversionService conversionService, final boolean mandatory) {
		this.conversionService=conversionService;
		this.mandatory = mandatory;
	}

	@Override
	public boolean supports(final Class<?> clazz) {
		return String.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {

		final String timeString = (String) target;
		if (mandatory && timeString == null) {
			errors.rejectValue(null, MANDATORY);
			return;
		}

		if (timeString == null) {
			return;
		}

		if (!validTimeString(splitTimeString(timeString))) {
			errors.rejectValue(null, INVALID);
		}
	}

	private boolean validTimeString(final String[] values) {
		if (values.length != 2) {
			return false;
		}

		return validInt(values[0], 24) && validInt(values[1], 60);
	}

	private boolean validInt(String value, final int limit) {
		try {
			int intValue = conversionService.convert(StringUtils.trimWhitespace(value), int.class);
			return intValue >= 0 && intValue < limit;
		} catch (ConversionFailedException ce) {
			return false;
		}
	}

	static String[] splitTimeString(final String stringValue) {
		return stringValue.split(DELIMITER);
	}

}
