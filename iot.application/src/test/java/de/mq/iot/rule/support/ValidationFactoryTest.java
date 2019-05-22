package de.mq.iot.rule.support;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

public class ValidationFactoryTest {
	
	private final ConversionService conversionService = new DefaultConversionService();
	
	final ValidationFactory validationFactory= new ValidationFactory(conversionService);
	
	@Test
	void init() {
		validationFactory.init();
		
		
	}

}
