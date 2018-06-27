package de.mq.iot.openweather.support;

import java.util.HashMap;
import java.util.Map;

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
@Disabled
class OpenWeatherRepositoryIntegrationTest {
	
	@Autowired
	private AbstractOpenWeatherRepository openWeatherRepository;
	
	private final ResourceIdentifier resourceIdentifier = Mockito.mock(ResourceIdentifier.class);
	
	@BeforeEach
	void setup() {
		
		
		Mockito.doReturn("http://api.openweathermap.org/data/2.5/forecast?q={city},{country}&appid={key}").when(resourceIdentifier).uri();
		final Map<String, String> parameters = new HashMap<>();
		parameters.put("city", "wegberg");
		parameters.put("country", "de");
		parameters.put("key", "607cd43d4d9b17d8a96df387fe4ede62");
		Mockito.doReturn(parameters).when(resourceIdentifier).parameters();
	}
	
	@Test
	@Disabled
	void forecast() {
		System.out.println(openWeatherRepository);
		
		openWeatherRepository.forecast(resourceIdentifier);
	}
	
}
