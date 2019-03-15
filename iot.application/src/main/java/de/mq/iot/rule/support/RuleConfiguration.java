package de.mq.iot.rule.support;

import java.time.LocalTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
@Configuration
class RuleConfiguration {
	@Bean
	ConversionService conversionService() {
	final  DefaultConversionService conversionService= new DefaultConversionService();
			conversionService.addConverter(String.class, LocalTime.class, stringValue -> {
				String[] values = TimeValidatorImpl.splitTimeString(stringValue);
				if( values.length!=2) {
					return null;
				}
				return LocalTime.of(conversionService.convert(values[0], Integer.class), conversionService.convert(values[1], Integer.class));
			});
		return conversionService;
	}
	
	
	
	
	

}
