package de.mq.iot.openweather.support;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import de.mq.iot.resource.ResourceIdentifier;

@Repository
abstract class AbstractOpenWeatherRepository implements WeatherRepository {
	
	
	enum OpenWeatherParameters {
		Forecast,

		Weather;
		static final String RESOURCE_PARAMETER_NAME = "resource";
		
		final Map<String, String> parameters(final ResourceIdentifier uniformResourceIdentifier) {
			final Map<String, String> parameters = new HashMap<>();
			parameters.putAll(uniformResourceIdentifier.parameters());
			parameters.put(RESOURCE_PARAMETER_NAME, name().toLowerCase());
			return parameters;
		}

	}
	
	

	private static final String TEMPERATURE_NODE_NAME = "temp";

	private static final String DATE_NODE_NAME = "dt";

	private static final String MAIN_NODE_NAME = "main";

	private static final String FORECAST_LIST_NODE_NAME = "list";

	private final Duration timeout;

	AbstractOpenWeatherRepository(@Value("${mongo.webclient:500}") final Long timeout) {
		this.timeout = Duration.ofMillis(timeout);
	}

	/* (non-Javadoc)
	 * @see de.mq.iot.openweather.support.WeatherRepository#forecast(de.mq.iot.resource.ResourceIdentifier)
	 */
	@Override
	public Collection<Entry<LocalDate, Double>> forecast(final ResourceIdentifier resourceIdentifier) {

		Assert.notNull(resourceIdentifier, "ResourceIdentifier is mandatory.");

		@SuppressWarnings("unchecked")
		final ResponseEntity<Map<String, Object>> res = (ResponseEntity<Map<String, Object>>) webClientBuilder().build().get().uri(resourceIdentifier.uri(), OpenWeatherParameters.Forecast.parameters(resourceIdentifier)).exchange().block(timeout).toEntity((Class<Map<String, Object>>) (Class<?>) HashMap.class).block(timeout);

		httpStatusGuard(res);

		Assert.isTrue(res.getBody().containsKey(FORECAST_LIST_NODE_NAME), "Invalid JSON: Node list is missing.");

		@SuppressWarnings("unchecked")
		final List<Map<String, Object>> list = (List<Map<String, Object>>) res.getBody().get(FORECAST_LIST_NODE_NAME);

		final Map<LocalDate, List<Double>> temperatures = new HashMap<>();
		list.forEach(map -> {

			final LocalDate date = Instant.ofEpochMilli(1000 * Long.valueOf((int) map.get(DATE_NODE_NAME))).atZone(ZoneOffset.UTC).toLocalDate();
			if (!temperatures.containsKey(date)) {
				temperatures.put(date, new ArrayList<>());
			}
			Assert.notNull(map.get(MAIN_NODE_NAME), "Main node is required.");
			Assert.notNull(((Map<?, ?>) map.get(MAIN_NODE_NAME)).get(TEMPERATURE_NODE_NAME), "Temp node is required.");
			
			temperatures.get(date).add(  ((Number) ((Map<?, ?>) map.get(MAIN_NODE_NAME)).get(TEMPERATURE_NODE_NAME)).doubleValue()) ; 

		});

		return temperatures.entrySet().stream().map(e -> new AbstractMap.SimpleImmutableEntry<>(e.getKey(), e.getValue().stream().mapToDouble(v -> v).max().getAsDouble())).sorted((e1, e2) -> (int) Math.signum((double) (millis(e1.getKey()) - millis(e2.getKey())))).collect(Collectors.toList());

	}

	long millis(LocalDate date) {
		return date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
	}

	private void httpStatusGuard(final ResponseEntity<?> res) {
		if (res.getStatusCode().is2xxSuccessful()) {
			return;
		}
		throw newHttpStatusCodeException(res.getStatusCode(), res.getStatusCode().getReasonPhrase());
	}

	private HttpStatusCodeException newHttpStatusCodeException(final HttpStatus status, final String result) {
		return new HttpStatusCodeException(status, result) {
			private static final long serialVersionUID = 1L;

		};
	}
	
	
	/* (non-Javadoc)
	 * @see de.mq.iot.openweather.support.WeatherRepository#weather(de.mq.iot.resource.ResourceIdentifier)
	 */
	@Override
	public Entry<LocalDateTime, Double>  weather(final ResourceIdentifier resourceIdentifier) {
		Assert.notNull(resourceIdentifier, "ResourceIdentifier is mandatory.");
		@SuppressWarnings("unchecked")
		final ResponseEntity<Map<String, Object>> res = (ResponseEntity<Map<String, Object>>) webClientBuilder().build().get().uri(resourceIdentifier.uri(), OpenWeatherParameters.Weather.parameters(resourceIdentifier)).exchange().block(timeout).toEntity((Class<Map<String, Object>>) (Class<?>) HashMap.class).block(timeout);
		httpStatusGuard(res);
		
		Assert.notNull(res.getBody().get(MAIN_NODE_NAME), "Main node is required.");
		
		Assert.notNull(((Map<?, ?>) res.getBody().get(MAIN_NODE_NAME)).get(TEMPERATURE_NODE_NAME), "Temp node is required.");
		final LocalDateTime date = Instant.ofEpochMilli(1000 * Long.valueOf((int)  res.getBody().get(DATE_NODE_NAME))).atZone(ZoneId.systemDefault()).toLocalDateTime();
		final Number temperature = (Number) ((Map<?, ?>) res.getBody().get(MAIN_NODE_NAME)).get(TEMPERATURE_NODE_NAME);
		
		return new AbstractMap.SimpleImmutableEntry<>(date, temperature.doubleValue());
	}

	@Lookup
	abstract Builder webClientBuilder();

}
