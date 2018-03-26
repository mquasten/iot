package de.mq.iot.support;

import org.junit.jupiter.api.Test;

class SunDownCalculationServiceTest {
	
	private final SunDownCalculationService sunDownCalculationService = new SimpleSunDownCalculationServiceImpl(52.5, 13.5);

	
	@Test
	final void sunDownTime() {
		sunDownCalculationService.sunDownTime(30, 1);
	}
}
