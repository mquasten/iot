package de.mq.iot.openweather.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
		final List<Entry<LocalDate, Double>> results = new ArrayList<>( openWeatherRepository.forecast(resourceIdentifier));
		
		assertEquals(6, results.size());
	
		IntStream.range(0, 6).forEach(i -> assertEquals(LocalDate.now().plusDays(i), results.get(i).getKey()));
		
		
		results.stream().map(entry -> entry.getValue()).forEach(value -> assertTrue(((value >= -10d) && (value <= 35d))));
		
		
		
		results.forEach(result -> System.out.println(result));
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
