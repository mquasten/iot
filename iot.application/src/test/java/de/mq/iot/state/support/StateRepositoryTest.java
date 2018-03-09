package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.state.support.HomematicXmlApiStateRepositoryImpl;
import reactor.core.publisher.Mono;

class StateRepositoryTest {

	private final static String XML = "<systemVariables><systemVariable name=\"$name\" variable=\"0\" value=\"$value\" value_list=\"\" ise_id=\"$ise_id\" min=\"\" max=\"\" unit=\"\" type=\"$type\" subtype=\"2\" logged=\"false\" visible=\"true\" timestamp=\"$timestamp\" value_name_0=\"ist falsch\" value_name_1=\"ist wahr\" /></systemVariables>";
	private static final String ID = "4711";

	private static final String TIMESTAMP = "" + new Date().getTime() /1000;

	private static final String BOOLEAN_TYPE = "2";

	private static final String WORKINGDAY = "Workingday";

	private static final String URI = "uri";

	private HomematicXmlApiStateRepositoryImpl stateRepository = Mockito.mock(HomematicXmlApiStateRepositoryImpl.class, Mockito.CALLS_REAL_METHODS);

	private final WebClient.Builder webClientBuilder = Mockito.mock(WebClient.Builder.class);

	private final ResourceIdentifier resourceIdentifier = Mockito.mock(ResourceIdentifier.class);

	@SuppressWarnings("unchecked")
	private final ResponseEntity<String> resonseEntity = Mockito.mock(ResponseEntity.class);

	@BeforeEach
	void setup() throws IOException {

		final XPath xpath = XPathFactory.newInstance().newXPath();

		Mockito.doReturn(xpath).when(stateRepository).xpath();
		@SuppressWarnings("unchecked")
		final Map<String, String> uriVariables = Mockito.mock(Map.class);

		Mockito.doReturn(URI).when(resourceIdentifier).uri();
		Mockito.doReturn(uriVariables).when(resourceIdentifier).parameters();

		Mockito.doReturn(webClientBuilder).when(stateRepository).webClientBuilder();
		final WebClient webClient = Mockito.mock(WebClient.class);
		Mockito.doReturn(webClient).when(webClientBuilder).build();
		final RequestHeadersUriSpec<?> requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
		Mockito.doReturn(requestHeadersUriSpec).when(webClient).get();
		final RequestHeadersSpec<?> requestHeadersSpec = Mockito.mock(RequestHeadersSpec.class);
		Mockito.doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(URI, uriVariables);
		@SuppressWarnings("unchecked")
		final Mono<ClientResponse> mono = Mockito.mock(Mono.class);
		Mockito.doReturn(mono).when(requestHeadersSpec).exchange();
		final ClientResponse clientResponse = Mockito.mock(ClientResponse.class);
		Mockito.doReturn(clientResponse).when(mono).block();
		@SuppressWarnings("unchecked")
		final Mono<ResponseEntity<String>> monoResponseEntity = Mockito.mock(Mono.class);
		Mockito.doReturn(monoResponseEntity).when(clientResponse).toEntity(String.class);

		Mockito.doReturn(resonseEntity).when(monoResponseEntity).block();
		Mockito.doReturn(HttpStatus.OK).when(resonseEntity).getStatusCode();
		
		String xml = XML.replaceFirst("\\$" + StateConverter.KEY_NAME, WORKINGDAY).replaceFirst("\\$" + StateConverter.KEY_VALUE, "" + true).replaceFirst("\\$" + StateConverter.KEY_TYPE, BOOLEAN_TYPE).replaceFirst("\\$" + StateConverter.KEY_TIMESTAMP, TIMESTAMP).replaceFirst("\\$" + StateConverter.KEY_ID, ID);
		Mockito.doReturn(xml).when(resonseEntity).getBody();
	
	}

	@Test
	final void findStates() {
		final Collection<Map<String, String>> results = stateRepository.findStates(resourceIdentifier);

		assertEquals(1, results.size());
		final Map<String, String> result = results.stream().findAny().get();
		assertEquals(ID, result.get(StateConverter.KEY_ID));
		assertEquals(WORKINGDAY, result.get(StateConverter.KEY_NAME));
		assertEquals(BOOLEAN_TYPE, result.get(StateConverter.KEY_TYPE));
		assertEquals(Boolean.TRUE.toString(), result.get(StateConverter.KEY_VALUE));
		assertEquals(TIMESTAMP, result.get(StateConverter.KEY_TIMESTAMP));
		

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
}