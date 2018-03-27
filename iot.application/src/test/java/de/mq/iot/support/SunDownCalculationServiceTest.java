package de.mq.iot.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Month;

import org.junit.jupiter.api.Test;

class SunDownCalculationServiceTest {
	
	private final SunDownCalculationService sunDownCalculationService = new SimpleSunDownCalculationServiceImpl(51.1423399, 6.2815922);

	
	@Test
	
	final void sunDownTime() {
		double result = sunDownCalculationService.sunDownTime(86, 2);
		//System.out.println(">>>"+(int) result +":"  + (int) Math.round(60 * (result % 1) ));	
		assertEquals(19, (int) result); 
		assertEquals(57, Math.round(60 *(result %1 )));
	}
	
	@Test
		final void sunDownTimeMonth() {
		double result = sunDownCalculationService.sunDownTime(Month.MARCH ,2);
		//System.out.println(">>>"+(int) result +":"  + (int) Math.round(60 * (result % 1) ));	
		assertEquals(19, (int) result); 
		assertEquals(39, Math.round(60 *(result %1 )));
		
	}
}
