package de.mq.iot.rule.support;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class NaturalNumberValidatorImpl implements Validator {

	private final boolean mandatory;
	private final ConversionService conversionService;
	private final int min;
	private final int max;
	;
	public NaturalNumberValidatorImpl(final ConversionService conversionService, final boolean mandatory, final int min, final int max) {
		this.conversionService=conversionService;
		this.mandatory = mandatory;
		this.min=min;
		this.max=max;
	}

	static final String INVALID = "invalid";

	static final String MANDATORY = "mandatory";
	static final String MIN = "min";
	
	static final String MAX = "max";
	@Override
	public boolean supports(final Class<?> clazz) {
		return String.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		
		
		final String numberString = (String) target;
		if (mandatory && !StringUtils.hasText(numberString)) {
			errors.rejectValue(null, MANDATORY);
			return;
		}
		if(! StringUtils.hasText(numberString)) {
			return;
		}
		try {
		final Integer value = conversionService.convert(numberString.trim(), Integer.class);
		if( value < min ) {
			
			errors.rejectValue(null,MIN, new Integer[] {min}, null);
		}
		
		if( value > max) {
			errors.rejectValue(null,MAX, new Integer[] {max}, null);
		}
		
		} catch(final ConversionFailedException ex) {
			errors.rejectValue(null,INVALID);
		}
		 
	}
	
	
	

}
