package de.mq.iot.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class })
class StateRepositoryTest {

	private static final String ATTRIBUTE_VALUE = "value";
	private static final String TYPE_BOOLEAN = "2";
	private static final String ATTRIBUTE_ID = "ise_id";
	private static final String ATTRIBUTE_TYPE = "type";
	private static final String ATTRIBUTE_NAME = "name";
	private static final String NAME_WORKINGDAY = "Workingday";
	@Autowired
	private HomematicXmlApiStateRepositoryImpl stateRepository;

	@Test

	void findStates() {
		final Collection<Map<String, String>> results = stateRepository.findStates();
		assertFalse(results.isEmpty());

		final Map<String, String> result = singleUniqueResult(
				results.stream().filter(entry -> entry.get(ATTRIBUTE_NAME).equalsIgnoreCase(NAME_WORKINGDAY))
						.collect(Collectors.toList()));

		assertEquals(NAME_WORKINGDAY, result.get(ATTRIBUTE_NAME));
		assertEquals(TYPE_BOOLEAN, result.get(ATTRIBUTE_TYPE));
		assertNotNull(result.get(ATTRIBUTE_ID));
		Integer.parseInt(result.get(ATTRIBUTE_ID));
		assertTrue(Arrays.asList("true", "false").contains(result.get(ATTRIBUTE_VALUE)));
		assertTrue(2017 < LocalDateTime
				.ofInstant(Instant.ofEpochSecond(Long.parseLong(result.get("timestamp"))), ZoneOffset.UTC).getYear());
	}

	<T> T singleUniqueResult(final Collection<T> values) {
		assertEquals(1, values.size());
		return values.stream().findAny().orElseThrow(() -> new IllegalStateException("Result is mandatory."));
	}
}

@Configuration
@ComponentScan(basePackages = "de.mq.iot.state")
class TestConfiguration {
	@Bean
	@Scope(scopeName = "prototype")
	WebClient.Builder webClientBuilder() {
		return WebClient.builder();
	}

	@Bean
	@Scope(scopeName = "prototype")
	XPath xpath() {
		return XPathFactory.newInstance().newXPath();
	}

}