package de.mq.iot.authentication;

import java.util.Locale;
import java.util.Optional;

import de.mq.iot.model.LocaleAware;


public interface SecurityContext extends LocaleAware {

	Optional<Authentication> authentication();

	void assign(final Authentication authentication);
	
	void assign(final Locale locale);

}