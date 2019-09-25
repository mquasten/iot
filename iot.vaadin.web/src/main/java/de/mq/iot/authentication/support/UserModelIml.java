package de.mq.iot.authentication.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.Authority;
import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;

public class UserModelIml implements UserModel {

	private final Subject<UserModel.Events, UserModel> subject;

	private Optional<Authentication> authentication = Optional.empty();

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
	 * @see de.mq.iot.authentication.support.UserModel#authority()
	 */
	@Override
	public Collection<Authority> authorities() {

		if (authentication.isPresent()) {
			return Collections.unmodifiableCollection(authentication.get().authorities());
		}
		return Collections.emptyList();
	}

}
