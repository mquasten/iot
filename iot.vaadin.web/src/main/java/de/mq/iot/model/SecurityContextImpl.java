package de.mq.iot.model;

import java.util.Optional;

import org.springframework.util.Assert;

public class SecurityContextImpl implements SecurityContext {
	
	private Optional<Authentication> authentication = Optional.empty();
	
	/* (non-Javadoc)
	 * @see de.mq.iot.model.SecurityContext#authentication()
	 */
	@Override
	public Optional<Authentication> authentication() {
		return authentication;
	}

	/* (non-Javadoc)
	 * @see de.mq.iot.model.SecurityContext#assign(de.mq.iot.model.Authentication)
	 */
	@Override
	public void assign(final Authentication authentication) {
		Assert.notNull(authentication, "Authentication is mandatory.");
		this.authentication=Optional.of(authentication);
	}
}
