package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionService;

class RuleConfigurationTest {
	
	private static final int HOUR = 5;
	private static final int MINUTES = 15;
	private final RuleConfiguration ruleConfiguration = new RuleConfiguration();
	
	@Test
	void conversionService() {
		final ConversionService conversionService = ruleConfiguration.conversionService();
		final LocalTime result = conversionService.convert(String.format("%s:%s",HOUR,MINUTES), LocalTime.class);
		
		assertEquals(HOUR, result.getHour());
		assertEquals(MINUTES, result.getMinute());
		
		
		assertNull(conversionService.convert(String.format("%s", HOUR), LocalTime.class));
	}

}
