package de.mq.iot.authentication.support;

import java.util.Collection;
import java.util.Optional;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.Authority;
import de.mq.iot.model.LocaleAware;
import de.mq.iot.model.Subject;

interface UserModel extends Subject<UserModel.Events, LoginModel>, LocaleAware{
	
	enum Events {
		ChangeLocale,
		SeclectionChanged,
		AuthoritiesChanged;
	}

	
	void assign(final Authentication authentication);


	Optional<Authentication> authentication();
	
	
	String login();

	void assignLogin(final String login);

	String password();

	void assignPassword(final String password);


	Collection<Authority> authorities();


	boolean authorityCanGranted(final Authority authority);


	void assign(final Authority value);


	void delete(final Collection<Authority> authorities);

	

}
