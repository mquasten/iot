package de.mq.iot.openweather.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.support.ApplicationConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })

class OpenWeatherRepositoryIntegrationTest {
	
	@Autowired
	private WeatherRepository openWeatherRepository;
	
	private final ResourceIdentifier resourceIdentifier = Mockito.mock(ResourceIdentifier.class);
	
	private final Map<String, String> parameters = new HashMap<>();
	@BeforeEach
	void setup() {
		
		Mockito.doReturn("http://api.openweathermap.org/data/{version}/{resource}?q={city},{country}&appid={key}&units=metric").when(resourceIdentifier).uri();
		
		parameters.put("version", "2.5");
		parameters.put("city", "wegberg");
		parameters.put("country", "de");
		parameters.put("key", "607cd43d4d9b17d8a96df387fe4ede62");
		Mockito.doReturn(parameters).when(resourceIdentifier).parameters();
	
	
	
		
	}
	
	@Test
	@Disabled
	void forecast() {
		final List<MeteorologicalData> results = new ArrayList<>( openWeatherRepository.forecast(resourceIdentifier));
		
	
		
		assertEquals(40, results.size());
		final LocalDateTime[] lastdate= {LocalDateTime.now().minusHours(3)};
		Map<LocalDate, Integer> dates =  new HashMap<>();
		results.forEach(result ->{
			
			if( ! dates.containsKey(result.dateTime().toLocalDate())) {
				dates.put(result.dateTime().toLocalDate(), 0);
			}
			assertTrue(lastdate[0].isBefore(result.dateTime()));
			assertEquals(3, ChronoUnit.HOURS.between(lastdate[0].toInstant(ZoneOffset.UTC), result.dateTime().toInstant(ZoneOffset.UTC)));
			
			assertTemperature(result.temperature());
			assertTemperature(result.highestTemperature());
			assertTemperature(result.lowestTemperature());
			assertTrue((result.windVelocityAmount() >= 0.5d) && (result.windVelocityAmount()  <= 30d));
			
			assertTrue((result.windVelocityAngleInDegrees() >= 0d) && (result.windVelocityAngleInDegrees()  < 360d));
			lastdate[0]=(result.dateTime());
			dates.put(result.dateTime().toLocalDate(), dates.get(result.dateTime().toLocalDate())+1);
			
		});
		
		IntStream.range(1, 5).forEach(i -> assertEquals(Integer.valueOf(8), dates.get(LocalDate.now().plusDays(i))));
		assertEquals(Integer.valueOf(8), Integer.valueOf(dates.get(LocalDate.now().plusDays(5)) + dates.get(LocalDate.now()))) ;
		
	}

	private void assertTemperature(double result) {
		assertTrue((result >= -10d) && (result <= 35d));
	}
	
	@Test
	@Disabled
	void weather() {
		final Entry<LocalDateTime, Double> result = openWeatherRepository.weather(resourceIdentifier);
		
		 final long minutes = result.getKey().until(LocalDateTime.now(), ChronoUnit.MINUTES);
		 
		 assertTrue(minutes<= 120);
		 
		 assertTrue((result.getValue() >= -10d) && (result.getValue()  <= 35d));
		 
		 
		 System.out.println(result.getKey());
		 System.out.println(result.getValue());
		 
		
	}	
	
}
