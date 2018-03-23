package de.mq.iot.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import javax.xml.xpath.XPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

class ApplicationConfigurationTest {
	
	private static final String DATABASE_NAME = "iot";
	private  final ApplicationConfiguration configuration = new ApplicationConfiguration();
	@BeforeEach
	void setup() {
		Arrays.asList(configuration.getClass().getDeclaredFields()).stream().filter(field -> field.isAnnotationPresent(Value.class)).forEach(field -> {
			final String[] cols = field.getAnnotation(Value.class).value().replaceFirst("[}]", "").split(":",2);
			assertEquals(2, cols.length);
			ReflectionTestUtils.setField(configuration, field.getName(), cols[1]);
		});
	}
	
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
		final ReactiveMongoTemplate reactiveMongoTemplate = (ReactiveMongoTemplate) configuration.reactiveMongoTemplate();
		assertEquals(DATABASE_NAME, reactiveMongoTemplate.getMongoDatabase().getName());
	}

}
