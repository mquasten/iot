package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
class StateRepositoryIntegrationTest {
	private static final String TYPE_BOOLEAN = "2";
	private static final String NAME_WORKINGDAY = "Workingday";
	@Autowired
	private HomematicXmlApiStateRepositoryImpl stateRepository;

	private final ResourceIdentifier resourceIdentifier = Mockito.mock(ResourceIdentifier.class);

	@BeforeEach
	void setup() {
		Mockito.doReturn("http://{host}:{port}/addons/xmlapi/sysvarlist.cgi").when(resourceIdentifier).uri();
		final Map<String, String> parameters = new HashMap<>();
		//parameters.put("host", "mq65.ddns.net");
		//parameters.put("port", "2000");
		
		parameters.put("host", "192.168.2.103");
		parameters.put("port", "80");
		Mockito.doReturn(parameters).when(resourceIdentifier).parameters();
	}

	@Test

	void findStates() {

		final Collection<Map<String, String>> results = stateRepository.findStates(resourceIdentifier);
		assertFalse(results.isEmpty());

		final Map<String, String> result = singleUniqueResult(results.stream().filter(entry -> entry.get(StateConverter.KEY_NAME).equalsIgnoreCase(NAME_WORKINGDAY)).collect(Collectors.toList()));

		assertEquals(NAME_WORKINGDAY, result.get(StateConverter.KEY_NAME));
		assertEquals(TYPE_BOOLEAN, result.get(StateConverter.KEY_TYPE));
		assertNotNull(result.get(StateConverter.KEY_ID));
		Integer.parseInt(result.get(StateConverter.KEY_ID));
		assertTrue(Arrays.asList("true", "false").contains(result.get(StateConverter.KEY_VALUE)));
		assertTrue(2017 < LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(result.get(StateConverter.KEY_TIMESTAMP))), ZoneOffset.UTC).getYear());
	}

	<T> T singleUniqueResult(final Collection<T> values) {
		assertEquals(1, values.size());
		return values.stream().findAny().orElseThrow(() -> new IllegalStateException("Result is mandatory."));
	}
}

