package de.mq.iot.authentication.support;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.Authority;
import de.mq.iot.support.ApplicationConfiguration;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })
@Disabled
class AuthenticationRepositoryIntegrationTest {

	@Autowired
	private AuthenticationRepository authenticationRepository;
	
	private final Duration duration = Duration.ofMillis(500);
	
	
	
	
	@Test
	final void save() {
		assertNotNull(authenticationRepository);
		
		createAuthentication();
		
	
		final  Optional<Authentication> result =  authenticationRepository.findByUsername("mquasten").blockOptional();
		assertTrue(result.isPresent());
		assertTrue(result.get().authenticate("manfred01"));
		
	}

	private void createAuthentication() {
		final Authentication authentication = new UserAuthenticationImpl("mquasten", "manfred01", Arrays.asList(Authority.ModifyUsers));
		
		
		
		authenticationRepository.save(authentication).block(duration);
	}
	
	@Test
	final void findFirstByUsernameNotAndAuthority() {
		
		createAuthentication();
		assertTrue(findFirstByUsernameNotAndAuthority("kminogue", Authority.ModifyUsers).isPresent());
		
		assertFalse(findFirstByUsernameNotAndAuthority("mquasten", Authority.ModifyUsers).isPresent());
		
		assertFalse(findFirstByUsernameNotAndAuthority("kminogue", Authority.ModifySystemvariables).isPresent());
	}

	private Optional<Authentication> findFirstByUsernameNotAndAuthority(final String user, final Authority adminAuthority) {
		final Optional<Authentication>  results = Optional.ofNullable(authenticationRepository.findByUsernameNotAndAuthority( user, adminAuthority).blockFirst(Duration.ofMillis(500)));
		return results;
	}
	

}
