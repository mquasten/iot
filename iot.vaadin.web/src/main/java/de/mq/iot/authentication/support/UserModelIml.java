package de.mq.iot.authentication.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

import org.springframework.util.Assert;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.Authority;
import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;

class UserModelIml implements UserModel {

	private final Subject<UserModel.Events, UserModel> subject;

	private Optional<Authentication> authentication = Optional.empty();

	private String login;

	private String password;

	private Collection<Authority> authorities = new ArrayList<>();

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
		authorities.clear();
		if (authentication != null) {
			authorities.addAll(authentication.authorities());
		}
		subject.notifyObservers(Events.SeclectionChanged);
		subject.notifyObservers(Events.AuthoritiesChanged);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.authentication.support.UserModel#authentication()
	 */
	@Override
	public Optional<Authentication> authentication() {

		return authentication;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.authentication.support.UserModel#login()
	 */
	@Override
	public String login() {
		return login;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.authentication.support.UserModel#assignLogin(java.lang.String)
	 */
	@Override
	public void assignLogin(final String login) {
		this.login = login;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.authentication.support.UserModel#password()
	 */
	@Override
	public String password() {
		return password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.iot.authentication.support.UserModel#assignPassword(java.lang.String)
	 */
	@Override
	public void assignPassword(String password) {
		this.password = password;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.authentication.support.UserModel#authorities()
	 */
	@Override
	public Collection<Authority> authorities() {
		return authorities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.iot.authentication.support.UserModel#authorityCanGranted(de.mq.iot.
	 * authentication.Authority)
	 */
	@Override
	public boolean authorityCanGranted(Authority authority) {

		if (authority == null) {
			return false;
		}

		if (authorities.contains(authority)) {
			return false;

		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.iot.authentication.support.UserModel#assign(de.mq.iot.authentication.
	 * Authority)
	 */
	@Override
	public void assign(final Authority authority) {

		if (authority == null) {
			return;
		}

		authorities.add(authority);
		notifyObservers(Events.AuthoritiesChanged);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.authentication.support.UserModel#delete(java.util.Collection)
	 */
	@Override
	public void delete(Collection<Authority> authorities) {
		if (authorities == null) {
			return;
		}
		this.authorities.removeAll(authorities);

		notifyObservers(Events.AuthoritiesChanged);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.authentication.support.UserModel#isAdmin()
	 */

	@Override
	public boolean isAdmin() {
		final Optional<Authentication> currentUser = subject.currentUser();
		if (!currentUser.isPresent()) {
			return false;
		}
		return currentUser.get().hasRole(Authority.Users);

	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.authentication.support.UserModel#isPasswordChangeAllowed()
	 */

	@Override
	public boolean isPasswordChangeAllowed() {

		if (isAdmin()) {
			return true;
		}
		final Optional<Authentication> currentUser = subject.currentUser();
		if (!currentUser.isPresent()) {
			return false;
		}
		Assert.isTrue(authentication.isPresent(), "Authentication should be aware.");
		return currentUser.get().equals(authentication.get());
	}

}
