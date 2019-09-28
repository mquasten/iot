package de.mq.iot.authentication.support;

import java.util.Optional;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.model.LocaleAware;
import de.mq.iot.model.Subject;

interface UserModel extends Subject<UserModel.Events, LoginModel>, LocaleAware{
	
	enum Events {
		ChangeLocale,
		SeclectionChanged;
	}

	
	void assign(final Authentication authentication);


	Optional<Authentication> authentication();
	
	
	String login();

	void assignLogin(final String login);

	String password();

	void assignPassword(final String password);

	

}
