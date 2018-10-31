package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.state.State;
import de.mq.iot.state.support.AbstractHomematicXmlApiStateRepository.XmlApiParameters;
import reactor.core.publisher.Mono;

class StateRepositoryTest {

	private static final String PORT = "80";
	private static final String HOST = "kylie.com";
	private static final String PORT_PARAMETER = "port";
	private static final String HOST_PARMETER = "host";
	private final static String XML = "<systemVariables><systemVariable name=\"$name\" variable=\"0\" value=\"$value\" value_list=\"\" ise_id=\"$ise_id\" min=\"\" max=\"\" unit=\"\" type=\"$type\" subtype=\"2\" logged=\"false\" visible=\"true\" timestamp=\"$timestamp\" value_name_0=\"ist falsch\" value_name_1=\"ist wahr\" /></systemVariables>";
	private static final String ID = "4711";

	private static final String TIMESTAMP = "" + new Date().getTime() / 1000;

	private static final String BOOLEAN_TYPE = "2";

	private static final String WORKINGDAY = "Workingday";

	private static final String URI = "uri";

	private AbstractHomematicXmlApiStateRepository stateRepository = Mockito.mock(AbstractHomematicXmlApiStateRepository.class, Mockito.CALLS_REAL_METHODS);

	private final WebClient.Builder webClientBuilder = Mockito.mock(WebClient.Builder.class);

	private final ResourceIdentifier resourceIdentifier = Mockito.mock(ResourceIdentifier.class);

	@SuppressWarnings("unchecked")
	private final ResponseEntity<String> resonseEntity = Mockito.mock(ResponseEntity.class);

	private final Duration duration = Duration.ofMillis(500);

	private ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
	@SuppressWarnings("unchecked")
	private ArgumentCaptor<Map<String, String>> parameterCaptor = ArgumentCaptor.forClass(Map.class);
	final Map<Class<?>, Object> dependencies = new HashMap<>();

	@BeforeEach
	void setup() throws IOException {

		dependencies.put(ConversionService.class, new DefaultConversionService());

		dependencies.put(Duration.class, duration);
		Arrays.asList(AbstractHomematicXmlApiStateRepository.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType())).forEach(field -> ReflectionTestUtils.setField(stateRepository, field.getName(), dependencies.get(field.getType())));

		final Map<String, String> parameter = new HashMap<>();
		parameter.put(HOST_PARMETER, HOST);
		parameter.put(PORT_PARAMETER, PORT);

		Mockito.doReturn(parameter).when(resourceIdentifier).parameters();

		final XPath xpath = XPathFactory.newInstance().newXPath();

		Mockito.doReturn(xpath).when(stateRepository).xpath();

		Mockito.doReturn(URI).when(resourceIdentifier).uri();

		Mockito.doReturn(webClientBuilder).when(stateRepository).webClientBuilder();
		final WebClient webClient = Mockito.mock(WebClient.class);
		Mockito.doReturn(webClient).when(webClientBuilder).build();
		final RequestHeadersUriSpec<?> requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
		RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(RequestBodyUriSpec.class);
		Mockito.doReturn(requestBodyUriSpec).when(webClient).put();
		Mockito.doReturn(requestHeadersUriSpec).when(webClient).get();
		
		final RequestBodySpec requestBodySpec = Mockito.mock(RequestBodySpec.class);
		final RequestHeadersSpec<?> requestHeadersSpec = Mockito.mock(RequestHeadersSpec.class);
		Mockito.doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(uriCaptor.capture(), parameterCaptor.capture());
		
		Mockito.doReturn(requestBodySpec).when(requestBodyUriSpec).uri(uriCaptor.capture(), parameterCaptor.capture());
		@SuppressWarnings("unchecked")
		final Mono<ClientResponse> mono = Mockito.mock(Mono.class);
		Mockito.doReturn(mono).when(requestHeadersSpec).exchange();
		
		Mockito.doReturn(mono).when(requestBodySpec).exchange();
		final ClientResponse clientResponse = Mockito.mock(ClientResponse.class);
		Mockito.doReturn(clientResponse).when(mono).block(duration);
		@SuppressWarnings("unchecked")
		final Mono<ResponseEntity<String>> monoResponseEntity = Mockito.mock(Mono.class);
		Mockito.doReturn(monoResponseEntity).when(clientResponse).toEntity(String.class);

		Mockito.doReturn(resonseEntity).when(monoResponseEntity).block(duration);
		Mockito.doReturn(HttpStatus.OK).when(resonseEntity).getStatusCode();

		
	}

	@Test
	final void findStates() {
		
		String xml = XML.replaceFirst("\\$" + StateConverter.KEY_NAME, WORKINGDAY).replaceFirst("\\$" + StateConverter.KEY_VALUE, "" + true).replaceFirst("\\$" + StateConverter.KEY_TYPE, BOOLEAN_TYPE).replaceFirst("\\$" + StateConverter.KEY_TIMESTAMP, TIMESTAMP)
				.replaceFirst("\\$" + StateConverter.KEY_ID, ID);
		Mockito.doReturn(xml).when(resonseEntity).getBody();

		
		final Collection<Map<String, String>> results = stateRepository.findStates(resourceIdentifier);

		assertEquals(1, results.size());
		final Map<String, String> result = results.stream().findAny().get();
		assertEquals(ID, result.get(StateConverter.KEY_ID));
		assertEquals(WORKINGDAY, result.get(StateConverter.KEY_NAME));
		assertEquals(BOOLEAN_TYPE, result.get(StateConverter.KEY_TYPE));
		assertEquals(Boolean.TRUE.toString(), result.get(StateConverter.KEY_VALUE));
		assertEquals(TIMESTAMP, result.get(StateConverter.KEY_TIMESTAMP));

		assertEquals(URI, uriCaptor.getValue());
		assertEquals(HOST, parameterCaptor.getValue().get(HOST_PARMETER));
		assertEquals(PORT, parameterCaptor.getValue().get(PORT_PARAMETER));
		assertEquals(XmlApiParameters.Sysvarlist.resource(), parameterCaptor.getValue().get(XmlApiParameters.RESOURCE_PARAMETER_NAME));

	}

	@Test
	final void findStatesInternalServerError() {
		Mockito.doReturn(HttpStatus.INTERNAL_SERVER_ERROR).when(resonseEntity).getStatusCode();

		assertThrows(HttpStatusCodeException.class, () -> stateRepository.findStates(resourceIdentifier));
	}

	@Test
	final void findStatesBadXml() {
		Mockito.doReturn("<xml>").when(resonseEntity).getBody();

		assertThrows(IllegalStateException.class, () -> stateRepository.findStates(resourceIdentifier));
	}

	@Test
	final void create() throws NoSuchMethodException, SecurityException {

		final Constructor<?> con = stateRepository.getClass().getDeclaredConstructor(ConversionService.class, Long.class);
		final Object stateRepository = BeanUtils.instantiateClass(con, dependencies.get(ConversionService.class), Long.valueOf(((Duration) dependencies.get(Duration.class)).toMillis()));

		final Map<Class<?>, ?> dependencyMap = Arrays.asList(AbstractHomematicXmlApiStateRepository.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType()))
				.collect(Collectors.toMap(field -> field.getType(), field -> ReflectionTestUtils.getField(stateRepository, field.getName())));
		assertEquals(2, dependencyMap.size());
		assertEquals(dependencies, dependencyMap);
	}
	
	@Test
	final void changeState() {
		
		final String xml = "<result><changed/></result>";
		Mockito.doReturn(xml).when(resonseEntity).getBody();

		
		final State<?> state = Mockito.mock(State.class);
		Mockito.doReturn(Boolean.TRUE).when(state).value();
		
		stateRepository.changeState(resourceIdentifier, state);
		
		assertEquals(URI+AbstractHomematicXmlApiStateRepository.STATE_CHANGE_URL_PARAMETER, uriCaptor.getValue());
		assertEquals(HOST, parameterCaptor.getValue().get(HOST_PARMETER));
		assertEquals(PORT, parameterCaptor.getValue().get(PORT_PARAMETER));
		assertEquals(XmlApiParameters.ChangeSysvar.resource(), parameterCaptor.getValue().get(XmlApiParameters.RESOURCE_PARAMETER_NAME));
	}
	
	@Test
	final void changeStateNotFound() {
		final String xml = "<result><not_found/></result>";
		Mockito.doReturn(xml).when(resonseEntity).getBody();
		
		final State<?> state = Mockito.mock(State.class);
		Mockito.doReturn(Boolean.TRUE).when(state).value();
	
		assertHttpExceptionIsThrown(state, HttpStatus.NOT_FOUND);
	
		
	}

	private void assertHttpExceptionIsThrown(final State<?> state, HttpStatus expectedHttpStatusCode) {
		try {
		
	       stateRepository.changeState(resourceIdentifier, state);
	   	fail(HttpStatusCodeException.class.getName() + " should be thrown.");
		} catch (final HttpStatusCodeException e) {
			assertEquals(expectedHttpStatusCode, e.getStatusCode());
		}
	}
	
	@Test
	final void changeStateUnkownResult() {
		final String xml = "<result><unkown/></result>";
		Mockito.doReturn(xml).when(resonseEntity).getBody();
		
		final State<?> state = Mockito.mock(State.class);
		Mockito.doReturn(Boolean.TRUE).when(state).value();
	
		assertHttpExceptionIsThrown(state, HttpStatus.BAD_REQUEST);
	
		
	}
	
	@Test
	final void changeStateMissingResult() {
		final State<?> state = Mockito.mock(State.class);
		Mockito.doReturn(Boolean.TRUE).when(state).value();
		assertHttpExceptionIsThrown(state, HttpStatus.BAD_REQUEST);
	}
	@Test
	final void changeStateEmptyResult() {
		final String xml = "<result/>";
		Mockito.doReturn(xml).when(resonseEntity).getBody();
		
		final State<?> state = Mockito.mock(State.class);
		Mockito.doReturn(Boolean.TRUE).when(state).value();
		assertHttpExceptionIsThrown(state, HttpStatus.BAD_REQUEST);
	}

}