package de.mq.iot.openweather.support;

import java.time.ZonedDateTime;

class MeteorologicalData  implements Comparable<MeteorologicalData>{
	
	private final double lowestTemperature;
	private final double temperature;
	private final double highestTemperature;
	private final double 	windVelocityAmount;
	private final ZonedDateTime dateTime;
	

	/*
	 * Degrees from north, right rotating over east (90°)  , south (180°) , west(270°).
	 */
	private final double windVelocityAngleInDegrees;
	
	MeteorologicalData(final double lowestTemperature, final double temperature, final double highestTemperature, final double windVelocityAmount, final double windVelocityAngleInDegrees, final ZonedDateTime dateTime) {
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
	
	public final ZonedDateTime dateTime() {
		return dateTime;
	}

	@Override
	public int compareTo(final MeteorologicalData other) {
		return dateTime.compareTo(other.dateTime);
	}

}
