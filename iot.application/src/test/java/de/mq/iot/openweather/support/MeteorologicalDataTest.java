package de.mq.iot.openweather.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

class MeteorologicalDataTest {

	static final ZonedDateTime TIME = ZonedDateTime.now();
	static final double WIND_VELOCITY_DEGREES = 102.001;
	static final double WIND_VELOCITY_AMOUNT = 7.11d;
	static final double MAX_TEMPERATURE = 27.61d;
	static final double TEMPERATURE = 26.50;
	static final double MIN_TEMPERATURE = 25.6d;
	
	private final MeteorologicalData meteorologicalData = newMeteorologicalData(TIME);


	private  MeteorologicalData newMeteorologicalData(final ZonedDateTime dateTime) {
		return new MeteorologicalData(MIN_TEMPERATURE, TEMPERATURE, MAX_TEMPERATURE, WIND_VELOCITY_AMOUNT, WIND_VELOCITY_DEGREES, dateTime);
	}
	
   
	@Test
	final void lowestTemperature() {
		assertEquals(MIN_TEMPERATURE, meteorologicalData.lowestTemperature());
	}
	@Test
	final void temperature() {
		assertEquals(TEMPERATURE,meteorologicalData.temperature());
	}
	@Test
	final void  highestTemperature() {
		assertEquals(MAX_TEMPERATURE, meteorologicalData.highestTemperature());
	}
	@Test
	final void windVelocityAmount() {
		assertEquals(WIND_VELOCITY_AMOUNT, meteorologicalData.windVelocityAmount());
	}

	@Test
	final void windVelocityAngleInDegrees() {
		assertEquals(WIND_VELOCITY_DEGREES, meteorologicalData.windVelocityAngleInDegrees());
	}
	@Test
	final void dateTime() {
		assertEquals(TIME, meteorologicalData.dateTime());
	}
	
	
	@Test
	final void compareTo() {
		assertEquals(0,meteorologicalData.compareTo(newMeteorologicalData(TIME)));
		assertEquals(1,meteorologicalData.compareTo(newMeteorologicalData(TIME.minusSeconds(1))));
		assertEquals(-1,meteorologicalData.compareTo(newMeteorologicalData(TIME.plusSeconds(1))));
	}
	
	
	

}
