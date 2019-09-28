package de.mq.iot.authentication.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.AuthentificationService;
import de.mq.iot.authentication.Authority;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class AuthentificationServiceTest {

	private static final String NEW_PASSWORD = "fever";

	private static final String USER = "kminogue";

	private static final int TIMEOUT = 500;

	private AuthenticationRepository authenticationRepository = Mockito.mock(AuthenticationRepository.class);

	private AuthentificationService authentificationService = new AuthentificationServiceImpl(authenticationRepository, TIMEOUT);

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

	@Test
	void changePassword() {

		@SuppressWarnings("unchecked")
		final Mono<Authentication> saveMono = Mockito.mock(Mono.class);
		Mockito.doReturn(mono).when(authenticationRepository).findByUsername(USER);
		Mockito.doReturn(USER).when(authentication).username();
		Mockito.doReturn(authentication).when(mono).block(Duration.ofMillis(TIMEOUT));
		Mockito.doReturn(saveMono).when(authenticationRepository).save(Mockito.any(Authentication.class));

		authentificationService.changePassword(USER, NEW_PASSWORD);

		final ArgumentCaptor<Authentication> usercaptor = ArgumentCaptor.forClass(Authentication.class);

		Mockito.verify(authenticationRepository).save(usercaptor.capture());
		Mockito.verify(saveMono).block(Duration.ofMillis(TIMEOUT));
		assertEquals(USER, usercaptor.getValue().username());
		assertTrue(usercaptor.getValue().authenticate(NEW_PASSWORD));

	}

	@Test
	void changePasswordUserNotFound() {
		Mockito.doReturn(mono).when(authenticationRepository).findByUsername(USER);

		Mockito.doReturn(null).when(mono).block(Duration.ofMillis(TIMEOUT));

		assertThrows(IllegalArgumentException.class, () -> authentificationService.changePassword(USER, NEW_PASSWORD));
	}

	@Test
	void changeAuthorities() {

		preparechangeAuthorities(Authority.ModifyUsers);

		assertTrue(authentificationService.changeAuthorities(USER, Arrays.asList(Authority.ModifySystemvariables)));

		ArgumentCaptor<Authentication> authenticationCaptor = ArgumentCaptor.forClass(Authentication.class);

		Mockito.verify(authenticationRepository).save(authenticationCaptor.capture());

		assertEquals(USER, authenticationCaptor.getValue().username());
		assertTrue(authenticationCaptor.getValue().authenticate(NEW_PASSWORD));
		assertEquals(1, authenticationCaptor.getValue().authorities().size());
		assertEquals(Authority.ModifySystemvariables, authenticationCaptor.getValue().authorities().stream().findAny().get());

	}

	private void preparechangeAuthorities(final Authority existingAuthority) {
		final Authentication user = new UserAuthenticationImpl(USER, NEW_PASSWORD, Arrays.asList(existingAuthority));

		@SuppressWarnings("unchecked")
		final Mono<Authentication> saveMono = Mockito.mock(Mono.class);
		Mockito.doReturn(saveMono).when(authenticationRepository).save(Mockito.any(Authentication.class));
		
		Mockito.doReturn(mono).when(authenticationRepository).findByUsername(USER);

		// Mockito.doReturn(Boolean.TRUE).when(user).hasRole(Authority.ModifyUsers);
		Mockito.doReturn(user).when(mono).block(Duration.ofMillis(TIMEOUT));

		

		final Flux<Authentication> flux = Flux.just(Mockito.mock(Authentication.class));
		Mockito.doReturn(flux).when(authenticationRepository).findByUsernameNotAndAuthority(USER, Authority.ModifyUsers);
	}

	@Test
	void changeAuthoritiesLastUserWithoutAdmin() {
		preparechangeAuthorities(Authority.ModifyUsers);

		final Flux<Authentication> flux = Flux.just();
		Mockito.doReturn(flux).when(authenticationRepository).findByUsernameNotAndAuthority(USER, Authority.ModifyUsers);

		assertFalse(authentificationService.changeAuthorities(USER, Arrays.asList(Authority.ModifySystemvariables)));

		final ArgumentCaptor<Authentication> authenticationCaptor = ArgumentCaptor.forClass(Authentication.class);

		Mockito.verify(authenticationRepository, Mockito.never()).save(authenticationCaptor.capture());

	}

	@Test
	void changeAuthoritiesUserNotFound() {

		preparechangeAuthorities(Authority.ModifyUsers);

		Mockito.doReturn(Mono.empty()).when(authenticationRepository).findByUsername(USER);

		assertThrows(IllegalArgumentException.class, () -> authentificationService.changeAuthorities(USER, Arrays.asList(Authority.ModifySystemvariables)));

	}

	@Test
	void changeAuthoritiesNotAdmin() {
		preparechangeAuthorities(Authority.ModifySystemvariables);

		assertTrue(authentificationService.changeAuthorities(USER, Arrays.asList(Authority.ModifySystemvariables)));

		final ArgumentCaptor<Authentication> authenticationCaptor = ArgumentCaptor.forClass(Authentication.class);

		Mockito.verify(authenticationRepository).save(authenticationCaptor.capture());

		assertEquals(USER, authenticationCaptor.getValue().username());
		assertTrue(authenticationCaptor.getValue().authenticate(NEW_PASSWORD));
		assertEquals(1, authenticationCaptor.getValue().authorities().size());
		assertEquals(Authority.ModifySystemvariables, authenticationCaptor.getValue().authorities().stream().findAny().get());

	}

	@Test
	void changeAuthoritiesAdmin() {
		preparechangeAuthorities(Authority.ModifyUsers);

		assertTrue(authentificationService.changeAuthorities(USER, Arrays.asList(Authority.ModifyUsers)));

		final ArgumentCaptor<Authentication> authenticationCaptor = ArgumentCaptor.forClass(Authentication.class);

		Mockito.verify(authenticationRepository).save(authenticationCaptor.capture());

		assertEquals(USER, authenticationCaptor.getValue().username());
		assertTrue(authenticationCaptor.getValue().authenticate(NEW_PASSWORD));
		assertEquals(1, authenticationCaptor.getValue().authorities().size());
		assertEquals(Authority.ModifyUsers, authenticationCaptor.getValue().authorities().stream().findAny().get());

	}
	
	@Test
	void create() {
		
		final Mono<Authentication> saveMono = prepareCreate(Mono.empty());
		
	    assertTrue(authentificationService.create(USER,NEW_PASSWORD));
	    
	    Mockito.verify(saveMono).block(Duration.ofMillis(TIMEOUT));
	    final ArgumentCaptor<Authentication> authenticationCaptor = ArgumentCaptor.forClass(Authentication.class);
	    Mockito.verify(authenticationRepository).save(authenticationCaptor.capture());
	    
	    assertEquals(USER, authenticationCaptor.getValue().username());
	    assertTrue(authenticationCaptor.getValue().authenticate(NEW_PASSWORD));
	}

	private Mono<Authentication> prepareCreate(Mono<Authentication> mono) {
		@SuppressWarnings("unchecked")
		final Mono<Authentication> saveMono = Mockito.mock(Mono.class);
		Mockito.doReturn(saveMono).when(authenticationRepository).save(Mockito.any(Authentication.class));
		Mockito.doReturn(mono).when(authenticationRepository).findByUsername(USER);
		return saveMono;
	}
	
	@Test
	void createExists() {
		prepareCreate(Mono.just(Mockito.mock(Authentication.class)));
		
	    assertFalse(authentificationService.create(USER,NEW_PASSWORD));
	   
	    Mockito.verify(authenticationRepository, Mockito.never()).save(Mockito.any());
	}
	@Test
	void delete() {
	
		final Mono<Authentication> deleteMono = prepareDelete((Mono.just(authentication)));
		
		authentificationService.delete(USER);
		
		Mockito.verify(authenticationRepository).delete(authentication);
		Mockito.verify(deleteMono).block(Mockito.any());
	}

	private Mono<Authentication> prepareDelete(final Mono<Authentication> mono) {
		@SuppressWarnings("unchecked")
		final Mono<Authentication> deleteMono = Mockito.mock(Mono.class);
		Mockito.doReturn(mono).when(authenticationRepository).findByUsername(USER);
		Mockito.doReturn(deleteMono).when(authenticationRepository).delete(Mockito.any(Authentication.class));
		return deleteMono;
	}
	
	@Test
	void deleteNotExists() {
	
		prepareDelete((Mono.empty()));
		
		authentificationService.delete(USER);
		
		Mockito.verify(authenticationRepository, Mockito.never()).delete(authentication);
	}

}
