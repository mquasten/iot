package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.openweather.MeteorologicalData;
import de.mq.iot.openweather.MeteorologicalDataService;
import de.mq.iot.rule.support.Calendar.Time;

class TemperatureRuleTest {
	
	private static final double TEMPERATURE = 25.0d;

	private final MeteorologicalDataService meteorologicalDataService = Mockito.mock(MeteorologicalDataService.class);
	
	private final TemperatureRuleImpl temperatureRule = new TemperatureRuleImpl(meteorologicalDataService);
	
	private final MeteorologicalData meteorologicalData = Mockito.mock(MeteorologicalData.class);
	
	@BeforeEach
	void setup() {
		Mockito.when(meteorologicalData.temperature()).thenReturn(TEMPERATURE);
		Mockito.when(meteorologicalDataService.forecastMaxTemperature(LocalDate.now())).thenReturn(meteorologicalData);
	}
	
	@Test
	void evaluate() {
		final Calendar calendar = new Calendar();
		
		assertFalse(temperatureRule.evaluate(calendar));
		calendar.assignTime(Time.Summer);
		assertFalse(temperatureRule.evaluate(calendar));
		
		
		calendar.assignDate(LocalDate.now());
		calendar.assignTime(Time.Summer);
		calendar.assignWorkingDay(true);
		assertTrue(temperatureRule.evaluate(calendar));
		
		calendar.assignTime(Time.Winter);
		assertFalse(temperatureRule.evaluate(calendar));
		
	}
	
	@Test
	void forecast() {
		final Calendar calendar = new Calendar();
		
		calendar.assignDate(LocalDate.now());
		calendar.assignTime(Time.Summer);
		calendar.assignWorkingDay(true);
		
	temperatureRule.forecast(calendar);
	
	assertEquals(Optional.of(TEMPERATURE), calendar.temperature());
		
	}

}
