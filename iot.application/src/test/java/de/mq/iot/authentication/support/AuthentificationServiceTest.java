package de.mq.iot.authentication.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.authentication.Authentication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class AuthentificationServiceTest {
	
	private static final String USER = "kminogue";

	private static final int TIMEOUT = 500;

	private AuthenticationRepository authenticationRepository = Mockito.mock(AuthenticationRepository.class);
	
	private AuthentificationServiceImpl  authentificationService = new AuthentificationServiceImpl(authenticationRepository, TIMEOUT);
	
	private Authentication authentication = Mockito.mock(Authentication.class);
	
	@SuppressWarnings("unchecked")
	private Mono<Authentication> mono = Mockito.mock(Mono.class);
	
	@Test
	void authentification() {
		
		Mockito.doReturn(mono).when(authenticationRepository).findByUsername(USER);
		Mockito.doReturn(authentication).when(mono).block(Duration.ofMillis(TIMEOUT));
		assertEquals(Optional.of(authentication), authentificationService.authentification(USER));
	}
	
	
	@Test
	void authentifications() {
		Mockito.doReturn(Flux.just(authentication)).when(authenticationRepository).findAll();
		final Collection<Authentication> authentications = authentificationService.authentifications();
		
		assertEquals(1, authentications.size());
		assertEquals(authentication, authentications.stream().findAny().get());
	}
	

}
