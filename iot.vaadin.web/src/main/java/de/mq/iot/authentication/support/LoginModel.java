package de.mq.iot.authentication.support;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.model.Subject;


interface LoginModel  extends Subject<LoginModel.Events, LoginModel> {
	
	enum Events {
		ChangeLocale;
	}
	
	
	String login();

	void assignLogin(String login);

	String password();

	void assignPassword(String password);

	boolean authenticate(final Authentication authentication);


}
