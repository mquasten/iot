package de.mq.iot.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.xml.xpath.XPath;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.web.reactive.function.client.WebClient;

class ApplicationConfigurationTest {
	
	private static final String DATABASE_NAME = "iot";
	private  final ApplicationConfiguration configuration = new ApplicationConfiguration();

	
	@Test
	final void xpath() {
		assertTrue(configuration.xpath() instanceof XPath);
	}
	@Test
	final void webClientBuilder() {
		assertTrue( configuration.webClientBuilder() instanceof WebClient.Builder);
	}
	
	@Test
	final void reactiveMongoTemplate() {
		final ReactiveMongoTemplate reactiveMongoTemplate = (ReactiveMongoTemplate) configuration.reactiveMongoTemplate("mongodb://localhost:27017", DATABASE_NAME);
		assertEquals(DATABASE_NAME, reactiveMongoTemplate.getMongoDatabase().getName());
	}

	@Test
	final void conversionService() {
		assertTrue(configuration.conversionService()  instanceof DefaultConversionService);
	}
	
	
}
