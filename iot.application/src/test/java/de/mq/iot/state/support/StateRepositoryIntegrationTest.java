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
import java.util.Map.Entry;
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
import de.mq.iot.state.StateService.DeviceType;
import de.mq.iot.support.ApplicationConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })
@Disabled
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
	
	
	@Test
	@Disabled
	void changeState() {
		final State<?> state = Mockito.mock(State.class);
		Mockito.when(state.id()).thenReturn(2261L);
		Mockito.doReturn(Boolean.FALSE).when(state).value();
		
		stateRepository.changeState(resourceIdentifier, state);
	}

	
	@Test
	@Disabled
	void findChannelIds() {
	
		final Collection<Entry<Long,String>> results =  stateRepository.findChannelIds(resourceIdentifier);
		assertTrue(results.stream().map(Entry::getKey).collect(Collectors.toList()).containsAll(Arrays.asList(1431L, 1952L, 4669L,5775L)));
		
		assertTrue(results.stream().map(Entry::getValue).collect(Collectors.toList()).containsAll(Arrays.asList("Licht" , "Rolladen")));
	}
	
	
	@Test
	@Disabled
	void findCannelsRooms() {
		final Map<Long,String> results = ((AbstractHomematicXmlApiStateRepository) stateRepository).findCannelsRooms(resourceIdentifier);
		assertEquals(10, results.size());
		
		Arrays.asList(4661L,4665L,4669L).forEach(id -> assertEquals("Eßzimmer (unten)", results.get(id)));
		
		Arrays.asList(1423L, 1427L,1431L,1944L,1948L,1952L).forEach(id -> assertEquals("Schlafzimmer (oben)", results.get(id)));
	}
	
	@Test
	@Disabled
	void findDeviceStates() {
		 final Collection<Map<String,String>> results = ((AbstractHomematicXmlApiStateRepository) stateRepository).findDeviceStates(resourceIdentifier, Arrays.asList(DeviceType.Level));
		 assertEquals(3, results.size());
		
		 results.stream().forEach(map -> {
			 assertEquals("LEVEL", map.get(AbstractStateConverter.KEY_TYPE));
			 
			final double  value = Double.valueOf(map.get(AbstractStateConverter.KEY_VALUE));
			assertTrue(value >= 0d && value <=1d );
			
			assertTrue(Arrays.asList(1431L, 1952L, 4669L).contains(Long.valueOf(map.get(AbstractStateConverter.KEY_ID)))); 
			
			assertTrue(map.get(AbstractStateConverter.KEY_NAME).matches(".*:3.Fenster.*"));
		 });
		 
		
	}
	
	@Test
	@Disabled
	void findVersion() {
		final Map<String, String> parameters = resourceIdentifier.parameters();
		parameters.put("host", "192.168.2.102");
		
		Mockito.doReturn(parameters).when(resourceIdentifier).parameters();
	    final double version =  stateRepository.findVersion(resourceIdentifier);
		 
		assertEquals(1.2 ,version);
		
	}
	
	@Test
	@Disabled
	void changeStates() {
		Map<Long, String> states = new HashMap<>();
		states.put(1431L, "0.00");
		states.put(1952L, "0.00");
		stateRepository.changeState(resourceIdentifier, states.entrySet());
	}
	
	
}

