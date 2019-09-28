package de.mq.iot.authentication.support;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.AuthentificationService;
import de.mq.iot.authentication.Authority;

@Service
class AuthentificationServiceImpl implements AuthentificationService {

	private static final String CREDENTIALS = "credentials";
	static final String CREDENTIALS_FIELD_NAME = CREDENTIALS;
	private final Duration timeout;
	private final AuthenticationRepository authenticationRepository;

	AuthentificationServiceImpl(final AuthenticationRepository authenticationRepository, @Value("${mongo.timeout:500}") final Integer timeout) {
		this.timeout = Duration.ofMillis(timeout);
		this.authenticationRepository = authenticationRepository;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.iot.authentication.support.AuthentificationService#authentification
	 * (java.lang.String)
	 */
	@Override
	public Optional<Authentication> authentification(final String username) {
		return Optional.ofNullable(authenticationRepository.findByUsername(username).block(timeout));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.authentication.AuthentificationService#authentifications()
	 */
	@Override
	public Collection<Authentication> authentifications() {
		return authenticationRepository.findAll().collectList().block(timeout);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.iot.authentication.AuthentificationService#changePassword(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public void changePassword(final String username, final String newPassword) {
		Assert.hasText(username, "Username is mandatory.");
		Assert.hasText(newPassword, "New Password is mandatory");
		final Authentication authentication = this.authentification(username).orElseThrow(() -> new IllegalArgumentException(String.format("User %s not found in database.", username)));
		
		authenticationRepository.save(new UserAuthenticationImpl(authentication.username(), newPassword, authentication.authorities())).block(timeout);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.iot.authentication.AuthentificationService#changeAuthorities(java.
	 * lang.String, java.util.Collection)
	 */
	@Override
	public boolean changeAuthorities(final String username, final Collection<Authority> authorities) {
		Assert.hasText(username, "Username is mandatory.");

		final Authentication authentication = this.authentification(username).orElseThrow(() -> new IllegalArgumentException(String.format("User %s not found in database.", username)));

		if (!modifyUsersRoleCanBeRemoved(authentication, authorities)) {
			return false;
		}

		final Authentication changedAAuthentication = new UserAuthenticationImpl(authentication.username(), "?", authorities);

		copyPassword(authentication, changedAAuthentication);

		authenticationRepository.save(changedAAuthentication).block(timeout);

		return true;

	}

	private void copyPassword(final Authentication authentication, final Authentication changedAAuthentication) {
		ReflectionUtils.doWithFields(UserAuthenticationImpl.class, field -> {
			field.setAccessible(true);
			field.set(changedAAuthentication, field.get(authentication));

		}, field -> field.getName().equalsIgnoreCase(CREDENTIALS));
	}

	private boolean modifyUsersRoleCanBeRemoved(final Authentication authentication, final Collection<Authority> authorities) {
		if (authentication.hasRole(Authority.ModifyUsers) && !authorities.contains(Authority.ModifyUsers)) {

			return Optional.ofNullable(authenticationRepository.findByUsernameNotAndAuthority(authentication.username(), Authority.ModifyUsers).blockFirst(timeout)).isPresent();
		}
		return true;
	}

}
