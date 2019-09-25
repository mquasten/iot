package de.mq.iot.authentication.support;

import java.util.Collection;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.Authority;
import de.mq.iot.model.LocaleAware;
import de.mq.iot.model.Subject;

interface UserModel extends Subject<UserModel.Events, LoginModel>, LocaleAware{
	
	enum Events {
		ChangeLocale,
		SeclectionChanged;
	}

	
	void assign(final Authentication authentication);


	Collection<Authority> authorities();
	
	

}
