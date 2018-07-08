package de.mq.iot.openweather.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.openweather.MeteorologicalData;
import de.mq.iot.openweather.MeteorologicalDataService;
import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.resource.ResourceIdentifier.ResourceType;
import de.mq.iot.resource.support.ResourceIdentifierRepository;
import reactor.core.publisher.Mono;

class MeteorologicalDataServiceTest {
	
	private static final Duration DURATION = Duration.ofMillis(500);
	
	private WeatherRepository weatherRepository = Mockito.mock(WeatherRepository.class);
	
	private ResourceIdentifierRepository resourceIdentifierRepository = Mockito.mock(ResourceIdentifierRepository.class);
	
	private final   MeteorologicalDataService meteorologicalDataService= new MeteorologicalDataServiceImpl(weatherRepository, resourceIdentifierRepository, DURATION.toMillis());

	private final  ResourceIdentifier resourceIdentifier = Mockito.mock(ResourceIdentifier.class);
	
	private MeteorologicalData meteorologicalDataForecast = Mockito.mock(MeteorologicalData.class);
	private MeteorologicalData meteorologicalDataWeather = Mockito.mock(MeteorologicalData.class);
	
	private final LocalDate localDate = LocalDate.now();
	
	@BeforeEach
	void setup() {
		
		Mockito.when(meteorologicalDataForecast.hasDate(localDate)).thenReturn(true);
		Mockito.when(meteorologicalDataWeather.hasDate(localDate)).thenReturn(true);
		
		Mockito.when(meteorologicalDataForecast.temperature()).thenReturn(25.0D);
		Mockito.when(meteorologicalDataWeather.temperature()).thenReturn(27.0D);
		
		Mockito.when(resourceIdentifierRepository.findById(ResourceType.OpenWeather)).thenReturn(Mono.just(resourceIdentifier));
		
		Mockito.when(weatherRepository.forecast(resourceIdentifier)).thenReturn(Arrays.asList(meteorologicalDataForecast));
		
		Mockito.when(weatherRepository.weather(resourceIdentifier)).thenReturn(meteorologicalDataWeather);
	}

	@Test
	void forecastMaxTemperature() {
	
		assertEquals(meteorologicalDataWeather, meteorologicalDataService.forecastMaxTemperature(localDate));
	}

	
	@Test
	void forecastMaxTemperatureOnlyForecasts() {
	
		final LocalDate date = localDate.plusDays(1);
		Mockito.when(meteorologicalDataForecast.hasDate(date)).thenReturn(true);
		Mockito.when(meteorologicalDataWeather.hasDate(date)).thenReturn(true);
	
		assertEquals(meteorologicalDataForecast, meteorologicalDataService.forecastMaxTemperature(date));
	}
	
	@Test
	void forecastMaxTemperatureMissingData() {
		assertThrows(IllegalArgumentException.class, () -> meteorologicalDataService.forecastMaxTemperature(localDate.plusDays(1)));
	}

}
