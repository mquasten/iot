package de.mq.iot.openweather.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

import de.mq.iot.resource.ResourceIdentifier;
import reactor.core.publisher.Mono;

class OpenWeatherRepositoryTest {

	private static final String URI = "uri";
	private final AbstractOpenWeatherRepository abstractOpenWeatherRepository = Mockito.mock(AbstractOpenWeatherRepository.class, Mockito.CALLS_REAL_METHODS);
	private final WebClient.Builder webClientBuilder = Mockito.mock(WebClient.Builder.class);
	private ResourceIdentifier resourceIdentifier = Mockito.mock(ResourceIdentifier.class);

	final Map<String, Object> map = MeteorologicalDataMapBuilder.builder().withDateTime(MapToMeteorologicalDataConverterImplTest.TIME).withLowestTemperature(MapToMeteorologicalDataConverterImplTest.MIN_TEMPERATURE).withTemperature(MapToMeteorologicalDataConverterImplTest.TEMPERATURE)
			.withHighestTemperature(MapToMeteorologicalDataConverterImplTest.MAX_TEMPERATURE).withWindVelocityAmount(MapToMeteorologicalDataConverterImplTest.WIND_VELOCITY_AMOUNT).withWindVelocityAngle(MapToMeteorologicalDataConverterImplTest.WIND_VELOCITY_DEGREES).build();

	@SuppressWarnings("unchecked")
	private final ResponseEntity<Map<String, Object>> resonseEntity = Mockito.mock(ResponseEntity.class);
	
	
	private ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
	
	@SuppressWarnings({ "unchecked" })
	private ArgumentCaptor<Map<String, Object>> parametersCaptor = ArgumentCaptor.forClass(Map.class);

	@BeforeEach
	void setup() {
		final Duration duration = Duration.ofMillis(500);
		final Map<Class<?>, Object> dependencies = new HashMap<>();
		dependencies.put(Duration.class, duration);
		dependencies.put(Converter.class, new MapToMeteorologicalDataConverterImpl());

		Arrays.asList(AbstractOpenWeatherRepository.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType())).forEach(field -> ReflectionTestUtils.setField(abstractOpenWeatherRepository, field.getName(), dependencies.get(field.getType())));
		final Map<String, String> parameter = new HashMap<>();

		Mockito.doReturn(parameter).when(resourceIdentifier).parameters();

		Mockito.doReturn(URI).when(resourceIdentifier).uri();

		final WebClient webClient = Mockito.mock(WebClient.class);
		Mockito.doReturn(webClient).when(webClientBuilder).build();

		Mockito.doReturn(webClientBuilder).when(abstractOpenWeatherRepository).webClientBuilder();
		final RequestHeadersUriSpec<?> requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
		Mockito.doReturn(requestHeadersUriSpec).when(webClient).get();
		final RequestHeadersSpec<?> requestHeadersSpec = Mockito.mock(RequestHeadersSpec.class);
		Mockito.doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(uriCaptor.capture(), parametersCaptor.capture());

		@SuppressWarnings("unchecked")
		final Mono<ClientResponse> mono = Mockito.mock(Mono.class);
		Mockito.doReturn(mono).when(requestHeadersSpec).exchange();

		final ClientResponse clientResponse = Mockito.mock(ClientResponse.class);
		Mockito.doReturn(clientResponse).when(mono).block(duration);

		@SuppressWarnings("unchecked")
		final Mono<ResponseEntity<String>> monoResponseEntity = Mockito.mock(Mono.class);
		Mockito.doReturn(monoResponseEntity).when(clientResponse).toEntity(HashMap.class);

		Mockito.doReturn(monoResponseEntity).when(clientResponse).toEntity(Map.class);

		Mockito.doReturn(resonseEntity).when(monoResponseEntity).block(duration);
		Mockito.doReturn(HttpStatus.OK).when(resonseEntity).getStatusCode();

		final Map<String, Object> data = new HashMap<>();
		data.put(AbstractOpenWeatherRepository.FORECAST_LIST_NODE_NAME, Arrays.asList(map));
		Mockito.doReturn(data).when(resonseEntity).getBody();
	}

	@Test
	void forecast() {
		final Collection<MeteorologicalData> results = abstractOpenWeatherRepository.forecast(resourceIdentifier);
		assertEquals(1, results.size());
		assertTrue(results.stream().findAny().isPresent());

		final MeteorologicalData result = results.stream().findAny().get();

		assertEquals(MapToMeteorologicalDataConverterImplTest.TIME.withNano(0), result.dateTime());

		assertEquals((double) MapToMeteorologicalDataConverterImplTest.TEMPERATURE, result.temperature());
		assertEquals((double) MapToMeteorologicalDataConverterImplTest.MAX_TEMPERATURE, result.highestTemperature());
		assertEquals((double) MapToMeteorologicalDataConverterImplTest.MIN_TEMPERATURE, result.lowestTemperature());

		assertEquals((double) MapToMeteorologicalDataConverterImplTest.WIND_VELOCITY_AMOUNT, result.windVelocityAmount());
		assertEquals((double) MapToMeteorologicalDataConverterImplTest.WIND_VELOCITY_DEGREES, result.windVelocityAngleInDegrees());
		
		assertEquals(URI, uriCaptor.getValue());
		
		assertEquals(AbstractOpenWeatherRepository.OpenWeatherParameters.Forecast.name().toLowerCase(), parametersCaptor.getValue().get(AbstractOpenWeatherRepository.OpenWeatherParameters.RESOURCE_PARAMETER_NAME));
	}

}
