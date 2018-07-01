package de.mq.iot.openweather.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class MeteorologicalDataTest {

	static final LocalDateTime TIME = LocalDateTime.now();
	static final double WIND_VELOCITY_DEGREES = 102.001;
	static final double WIND_VELOCITY_AMOUNT = 7.11d;
	static final double MAX_TEMPERATURE = 27.61d;
	static final double TEMPERATURE = 26.50;
	static final double MIN_TEMPERATURE = 25.6d;
	
	private final MeteorologicalData meteorologicalData = new MeteorologicalData(MIN_TEMPERATURE, TEMPERATURE, MAX_TEMPERATURE, WIND_VELOCITY_AMOUNT, WIND_VELOCITY_DEGREES, TIME);
	
   
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

}
