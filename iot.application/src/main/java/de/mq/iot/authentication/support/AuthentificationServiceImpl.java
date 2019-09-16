package de.mq.iot.authentication.support;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.AuthentificationService;
@Service
class AuthentificationServiceImpl implements AuthentificationService {
	
	static final String CREDENTIALS_FIELD_NAME = "credentials";
	private final Duration timeout;
	private final AuthenticationRepository authenticationRepository;
	AuthentificationServiceImpl(final AuthenticationRepository authenticationRepository, @Value("${mongo.timeout:500}") final Integer timeout) {
		this.timeout=Duration.ofMillis(timeout);
		this.authenticationRepository=authenticationRepository;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.authentication.support.AuthentificationService#authentification(java.lang.String)
	 */
	@Override
	public Optional<Authentication> authentification(final String username) {
		return Optional.ofNullable(authenticationRepository.findByUsername(username).block(timeout));
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.authentication.AuthentificationService#authentifications()
	 */
	@Override
	public Collection<Authentication> authentifications() {
		return authenticationRepository.findAll().collectList().block(timeout);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.authentication.AuthentificationService#changePassword(java.lang.String, java.lang.String)
	 */
	@Override
	public void changePassword(final String username, final String newPassword) {
		Assert.hasText(username, "Username is mandatory.");
		Assert.hasText(newPassword, "New Password is mandatory");
		final Authentication authentication = this.authentification(username).orElseThrow(() -> new IllegalArgumentException(String.format("User %s not found in database." ,  username)));
		
		authenticationRepository.save(new UserAuthenticationImpl(authentication.username(), newPassword, authentication.authorities())).block(timeout);
	}

}
