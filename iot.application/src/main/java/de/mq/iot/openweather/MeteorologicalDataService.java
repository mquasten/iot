package de.mq.iot.openweather;

import java.time.LocalDate;
import java.util.Collection;



public interface MeteorologicalDataService {
	
	MeteorologicalData forecastMaxTemperature(final LocalDate localDate);
	
	Collection<MeteorologicalData> forecasts(); 
}