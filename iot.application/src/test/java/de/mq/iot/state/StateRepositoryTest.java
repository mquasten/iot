package de.mq.iot.state;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

import reactor.core.publisher.Mono;

class StateRepositoryTest {
	
	private HomematicXmlApiStateRepositoryImpl stateRepository = Mockito.mock(HomematicXmlApiStateRepositoryImpl.class, Mockito.CALLS_REAL_METHODS);

	 private final WebClient.Builder webClientBuilder = Mockito.mock(WebClient.Builder.class);
	

	@BeforeEach
	void setup() {
		@SuppressWarnings("unchecked")
		final Map<String,String> uriVariables = Mockito.mock(Map.class);
		Mockito.doReturn(webClientBuilder).when(stateRepository).webClientBuilder();
		final WebClient webClient = Mockito.mock(WebClient.class);
		Mockito.doReturn(webClient).when(webClientBuilder).build();
		final RequestHeadersUriSpec<?>  requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
		Mockito.doReturn(requestHeadersUriSpec).when(webClient).get();
		final RequestHeadersSpec<?> requestHeadersSpec = Mockito.mock(RequestHeadersSpec.class);
		Mockito.doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri("uri", uriVariables);
		@SuppressWarnings("unchecked")
		final Mono<ClientResponse>  mono = Mockito.mock(Mono.class);
		Mockito.doReturn(mono).when(requestHeadersSpec).exchange();
		final ClientResponse clientResponse = Mockito.mock(ClientResponse.class);
		Mockito.doReturn(clientResponse).when(mono).block();
		@SuppressWarnings("unchecked")
		final Mono<ResponseEntity<String>> monoResponseEntity = Mockito.mock(Mono.class);
		Mockito.doReturn(monoResponseEntity).when(clientResponse).toEntity(String.class);
		Mockito.doReturn("<xml>").when(monoResponseEntity).block();
		//block();
		
	}
	
	@Test
	final void findStates() {
		
		
	}
}