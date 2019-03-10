package de.mq.iot.rule.support;


import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import org.springframework.validation.Validator;

public class TimeValidatorImpl implements  Validator {
	
	private ConversionService conversionService = new DefaultConversionService();
	
	private boolean mandatory = true;

	@Override
	public boolean supports(final Class<?> clazz) {
		return String.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		
		
		final String timeString = (String) target;
		if( mandatory && !StringUtils.hasText(timeString)) {
			errors.rejectValue(null,"mandatory");
			return;
		}
		
		if(! validTimeString(splitTimeString(timeString)) ) {
			errors.rejectValue(null, "invalid");
		}
	}
	
	private boolean validTimeString(final String[] values) {
		if( values.length != 2) {
			return false;
		}
		
		return validInt(values[0], 24) && validInt(values[1], 60);
	}




	private boolean validInt(String value, final int limit ) {
		try {
		int intValue = 	conversionService.convert(value, int.class);
		return  intValue >= 0 && intValue < limit;
		} catch (IllegalArgumentException ia) {
			return false;
		}
	}
	
	private String[] splitTimeString(final String stringValue) {
		return stringValue.split("[:.| ,\t]");
	}
		

	

}
