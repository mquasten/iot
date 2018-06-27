package de.mq.iot.openweather.support;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import de.mq.iot.resource.ResourceIdentifier;



@Repository
abstract class AbstractOpenWeatherRepository {
	
	private static final String FORECAST_LIST_NODE_NAME = "list";

	private final ConversionService conversionService;
	
	private final Duration timeout; 
	
	AbstractOpenWeatherRepository(final ConversionService conversionService, @Value("${mongo.webclient:500}") final Long timeout) {
		this.conversionService=conversionService;
		this.timeout=Duration.ofMillis(timeout);
	}
	
	public Collection<?> forecast(final ResourceIdentifier resourceIdentifier) {
		
		Assert.notNull(resourceIdentifier, "ResourceIdentifier is mandatory.");

	
		@SuppressWarnings("unchecked")
		final ResponseEntity<Map<String,Object>> res =(ResponseEntity<Map<String, Object>>) webClientBuilder().build().get().uri(resourceIdentifier.uri(), resourceIdentifier.parameters()).exchange().block(timeout).toEntity((Class<Map<String,Object>>) (Class<?>) HashMap.class).block(timeout);
		
		httpStatusGuard(res);
	
		Assert.isTrue( res.getBody().containsKey(FORECAST_LIST_NODE_NAME), "Invalid JSON: Node list is missing.");
		
		@SuppressWarnings("unchecked")
		final List<Map<String,Object>> list =  (List<Map<String, Object>>) res.getBody().get(FORECAST_LIST_NODE_NAME);
		System.out.println(list);
		
		return Arrays.asList();
	}
	
	
	
	private void httpStatusGuard(final ResponseEntity<?> res) {
		if (res.getStatusCode().is2xxSuccessful()) {
			return;
		}
		throw newHttpStatusCodeException(res.getStatusCode(), res.getStatusCode().getReasonPhrase());
	}
	
	private  HttpStatusCodeException newHttpStatusCodeException(final HttpStatus status, final String result) {
		return new HttpStatusCodeException(status, result) {
			private static final long serialVersionUID = 1L;

		};
	}
	
	@Lookup
	abstract Builder webClientBuilder();
	
	
	@Lookup
	abstract XPath xpath();

}
