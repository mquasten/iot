package de.mq.iot.support;

import static org.junit.jupiter.api.Assertions.assertEquals;


import java.time.LocalTime;
import java.time.Month;

import org.junit.jupiter.api.Test;

class SunDownCalculationServiceTest {
	
	private final SunDownCalculationService sunDownCalculationService = new SimpleSunDownCalculationServiceImpl(51.1423399, 6.2815922);

	
	@Test
	
	final void sunDownTime() {
		
		final double result = sunDownCalculationService.sunDownTime(86, 2);
	//	System.out.println(">>>"+(int) result +":"  + (int) Math.round(60 * (result % 1) ));	
		
		
		assertEquals(19, (int) result); 
		assertEquals(57, Math.round(60 *(result %1 )));
	}
	
	
	
@Test
	
	final void sunUpTime() {
		
		final double result = sunDownCalculationService.sunUpTime(86, 2);
		
		
		assertEquals(7, (int) result); 
		assertEquals(23, Math.round(60 *(result %1 )));
	}
	
	@Test
		final void sunDownTimeMonth() {
		LocalTime result = sunDownCalculationService.sunDownTime(Month.MARCH ,2);
		
	//	System.out.println(result);	
		
		assertEquals(LocalTime.of(19, 39), result);
	
		
	}
	
	@Test
	final void sunUpTimeMonth() {
	LocalTime result = sunDownCalculationService.sunUpTime(Month.MARCH ,2);
	
//System.out.println(result);
	
	assertEquals(LocalTime.of(7, 48), result);
	
	

	
}
}
