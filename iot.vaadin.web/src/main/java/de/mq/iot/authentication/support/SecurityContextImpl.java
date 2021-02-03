package de.mq.iot.authentication.support;

import java.util.Locale;
import java.util.Optional;

import org.springframework.util.Assert;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.SecurityContext;
import de.mq.iot.model.LocaleAware;

public class SecurityContextImpl implements SecurityContext, LocaleAware {
	
	private Optional<Authentication> authentication = Optional.empty();
	
	private Locale locale =Locale.GERMAN;
	
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

	
	@Override
	public void assign(final Locale locale) {
		Assert.notNull(locale, "Locale is mandatory.");
		this.locale=locale;
		
	}

	@Override
	public Locale locale() {
		return locale;
	}
}
