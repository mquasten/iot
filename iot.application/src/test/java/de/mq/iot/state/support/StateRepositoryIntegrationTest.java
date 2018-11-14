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
import de.mq.iot.state.State;
import de.mq.iot.support.ApplicationConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })

class StateRepositoryIntegrationTest {
	private static final String TYPE_BOOLEAN = "2";
	private static final String NAME_WORKINGDAY = "Workingday";
	@Autowired
	private AbstractHomematicXmlApiStateRepository stateRepository;

	private final ResourceIdentifier resourceIdentifier = Mockito.mock(ResourceIdentifier.class);

	@BeforeEach
	void setup() {
		Mockito.doReturn("http://{host}:{port}/addons/xmlapi/{resource}").when(resourceIdentifier).uri();
		final Map<String, String> parameters = new HashMap<>();
		parameters.put("host", "homematic-ccu2");
		parameters.put("port", "80");
		Mockito.doReturn(parameters).when(resourceIdentifier).parameters();
	}

	
	@Disabled
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
	
	@Disabled
	@Test
	void changeState() {
		final State<?> state = Mockito.mock(State.class);
		Mockito.when(state.id()).thenReturn(2261L);
		Mockito.doReturn(Boolean.FALSE).when(state).value();
		
		stateRepository.changeState(resourceIdentifier, state);
	}
	@Disabled
	@Test
	void findChannelIds() {
		
		
		final Collection<Long> ids = ((AbstractHomematicXmlApiStateRepository) stateRepository).findChannelIds(resourceIdentifier, "Rolladen");
		
		assertEquals(Arrays.asList(1431L, 1952L, 4669L), ids);
		
	}
	@Disabled
	@Test
	void findCannelsRooms() {
		final Map<Long,String> results = ((AbstractHomematicXmlApiStateRepository) stateRepository).findCannelsRooms(resourceIdentifier);
		assertEquals(9, results.size());
		
		Arrays.asList(4661L,4665L,4669L).forEach(id -> assertEquals("Eßzimmer (unten)", results.get(id)));
		
		Arrays.asList(1423L, 1427L,1431L,1944L,1948L,1952L).forEach(id -> assertEquals("Schlafzimmer (oben)", results.get(id)));
	}
	
	@Test
	@Disabled
	void findDeviceStates() {
		 final Collection<State<Double>> results = ((AbstractHomematicXmlApiStateRepository) stateRepository).findDeviceStates(resourceIdentifier);
		 assertEquals(3, results.size());
		 results.stream().map(result  -> result.value()).forEach(value -> assertTrue(value >= 0d && value <=1d ));
		 assertEquals(Arrays.asList(1431L, 1952L, 4669L), results.stream().map(result  -> new Long(result.id())).collect(Collectors.toList()));
		 results.stream().map(result  -> result.name()).forEach(name -> assertTrue(name.matches(".*:3.Fenster.*")));
	}
	
	@Test
	@Disabled
	void findVersion() {
		final Map<String, String> parameters = resourceIdentifier.parameters();
		parameters.put("host", "192.168.2.101");
		
		Mockito.doReturn(parameters).when(resourceIdentifier).parameters();
	    final double version =  stateRepository.findVersion(resourceIdentifier);
		 
		assertEquals(1.15 ,version);
		
	}
}

