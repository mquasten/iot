package de.mq.iot.openweather.support;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import de.mq.iot.openweather.MeteorologicalData;
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

	private static final String MAIN_NODE_NAME = "main";

	static final String FORECAST_LIST_NODE_NAME = "list";

	private final Duration timeout;
	private Converter<Map<String,Object>, MeteorologicalDataImpl> converter;

	AbstractOpenWeatherRepository( Converter<Map<String,Object>, MeteorologicalDataImpl> converter, @Value("${http.timeout:500}") final Long timeout) {
		this.converter=converter;
		this.timeout = Duration.ofMillis(timeout);
	}

	/* (non-Javadoc)
	 * @see de.mq.iot.openweather.support.WeatherRepository#forecast(de.mq.iot.resource.ResourceIdentifier)
	 */
	@Override
	public  Collection<MeteorologicalData> forecast(final ResourceIdentifier resourceIdentifier) {

		Assert.notNull(resourceIdentifier, "ResourceIdentifier is mandatory.");

		
		@SuppressWarnings("unchecked")
		final ResponseEntity<Map<String, Object>> res = (ResponseEntity<Map<String, Object>>) webClientBuilder().build().get().uri(resourceIdentifier.uri(), OpenWeatherParameters.Forecast.parameters(resourceIdentifier)).exchange().block(timeout).toEntity((Class<Map<String, Object>>) (Class<?>) HashMap.class).block(timeout);

		httpStatusGuard(res);

		Assert.isTrue(res.getBody().containsKey(FORECAST_LIST_NODE_NAME), "Invalid JSON: Node list is missing.");

		@SuppressWarnings("unchecked")
		final List<Map<String, Object>> list = (List<Map<String, Object>>) res.getBody().get(FORECAST_LIST_NODE_NAME);

		return list.stream().map(map -> converter.convert(map)).sorted().collect(Collectors.toList()); 

	
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
	public MeteorologicalData  weather(final ResourceIdentifier resourceIdentifier) {
		Assert.notNull(resourceIdentifier, "ResourceIdentifier is mandatory.");
		@SuppressWarnings("unchecked")
		final ResponseEntity<Map<String, Object>> res = (ResponseEntity<Map<String, Object>>) webClientBuilder().build().get().uri(resourceIdentifier.uri(), OpenWeatherParameters.Weather.parameters(resourceIdentifier)).exchange().block(timeout).toEntity((Class<Map<String, Object>>) (Class<?>) HashMap.class).block(timeout);
		httpStatusGuard(res);
		
		Assert.notNull(res.getBody().get(MAIN_NODE_NAME), "Main node is required.");
		
		return converter.convert(res.getBody());
		
	}

	@Lookup
	abstract Builder webClientBuilder();

}
