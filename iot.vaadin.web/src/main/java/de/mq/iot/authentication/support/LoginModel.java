package de.mq.iot.authentication.support;

import de.mq.iot.authentication.Authentication;

interface LoginModel {
	
	String login();

	void assignLogin(String login);

	String password();

	void assignPassword(String password);

	boolean authenticate(final Authentication authentication);


}
