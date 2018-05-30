package de.mq.iot.authentication.support;

import java.util.Locale;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;


class LoginModelImpl  implements LoginModel{
	
	private String login;

	private String password;
	
	private final Subject<LoginModel.Events, LoginModel> subject;
	
	LoginModelImpl(Subject<Events, LoginModel> subject) {
		this.subject = subject;
	}


	@Override
	public String login() {
		return login;
	}

	
	@Override
	public void assignLogin(String login) {
		this.login = login;
	}

	
	@Override
	public String password() {
		return password;
	}

	
	@Override
	public void assignPassword(String password) {
		this.password = password;
	}


	@Override
	public boolean  authenticate(final Authentication authentication) {
		 return authentication.authenticate(password);
	}


	@Override
	public Observer register(final Events key, final Observer observer) {
		
		return subject.register(key, observer);
	}


	@Override
	public void notifyObservers(final Events key) {
		subject.notifyObservers(key);
		
	}


	@Override
	public Locale locale() {
		return Locale.GERMAN;
	}

}
