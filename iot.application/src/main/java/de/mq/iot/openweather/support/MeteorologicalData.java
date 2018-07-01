package de.mq.iot.openweather.support;

import java.time.LocalDateTime;

class MeteorologicalData {
	
	private final double lowestTemperature;
	private final double temperature;
	private final double highestTemperature;
	private final double 	windVelocityAmount;
	private final LocalDateTime dateTime;
	

	/*
	 * Degrees from north, right rotating over east (90°)  , south (180°) , west(270°).
	 */
	private final double windVelocityAngleInDegrees;
	
	MeteorologicalData(final double lowestTemperature, final double temperature, final double highestTemperature, final double windVelocityAmount, final double windVelocityAngleInDegrees, final LocalDateTime dateTime) {
		this.lowestTemperature = lowestTemperature;
		this.temperature = temperature;
		this.highestTemperature = highestTemperature;
		this.windVelocityAmount = windVelocityAmount;
		this.windVelocityAngleInDegrees = windVelocityAngleInDegrees;
		this.dateTime=dateTime;
	}
	
	public final double lowestTemperature() {
		return lowestTemperature;
	}

	public final double temperature() {
		return temperature;
	}

	public final double highestTemperature() {
		return highestTemperature;
	}

	public final double windVelocityAmount() {
		return windVelocityAmount;
	}

	public final double windVelocityAngleInDegrees() {
		return windVelocityAngleInDegrees;
	}
	
	public final LocalDateTime dateTime() {
		return dateTime;
	}

}
