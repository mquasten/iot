package de.mq.iot.authentication.support;

import java.util.Locale;
import java.util.Optional;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;

class UserModelIml implements UserModel {

	private final Subject<UserModel.Events, UserModel> subject;

	private Optional<Authentication> authentication = Optional.empty();
	
	private String login;
	
	private String password;

	UserModelIml(final Subject<UserModel.Events, UserModel> subject) {
		this.subject = subject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.model.Subject#register(java.lang.Object,
	 * de.mq.iot.model.Observer)
	 */
	@Override
	public Observer register(final Events key, final Observer observer) {
		return subject.register(key, observer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.model.Subject#notifyObservers(java.lang.Object)
	 */
	@Override
	public void notifyObservers(final Events key) {
		subject.notifyObservers(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.model.LocaleAware#locale()
	 */
	@Override
	public Locale locale() {
		return Locale.GERMAN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.authentication.support.UserModel#assign(de.mq.iot.
	 * authentication.Authentication)
	 */
	@Override
	public void assign(final Authentication authentication) {
		this.authentication = Optional.ofNullable(authentication);
		subject.notifyObservers(Events.SeclectionChanged);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.authentication.support.UserModel#authentication()
	 */
	@Override
	public Optional<Authentication> authentication() {

		 return authentication;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.authentication.support.UserModel#login()
	 */
	@Override
	public String login() {
		return login;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.authentication.support.UserModel#assignLogin(java.lang.String)
	 */
	@Override
	public void assignLogin(final String login) {
		this.login=login;
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.authentication.support.UserModel#password()
	 */
	@Override
	public String password() {
		return password;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.authentication.support.UserModel#assignPassword(java.lang.String)
	 */
	@Override
	public void assignPassword(String password) {
		this.password=password;
		
	}
	
	
	
	

}
