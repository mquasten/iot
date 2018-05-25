package de.mq.iot.authentication.support;

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
		
		final Authentication authentication = new UserAuthenticationImpl("mquasten", "manfred01", Arrays.asList(Authority.values()));
		
		authenticationRepository.save(authentication).block(duration);
		
	
		final  Optional<Authentication> result =  authenticationRepository.findByUsername("mquasten").blockOptional();
		assertTrue(result.isPresent());
		assertTrue(result.get().authenticate("manfred01"));
		
	}
	

}
