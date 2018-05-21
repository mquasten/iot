package de.mq.iot.authentication.support;

import de.mq.iot.authentication.Authentication;

interface LoginModel {
	
	String getLogin();

	void setLogin(String login);

	String getPassword();

	void setPassword(String password);

	boolean authenticate(final Authentication authentication);


}
