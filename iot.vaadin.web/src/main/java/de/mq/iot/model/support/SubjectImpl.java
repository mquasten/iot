package de.mq.iot.model.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.util.Assert;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.SecurityContext;
import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;

public class SubjectImpl<Key, Model> implements Subject<Key, Model> {

	private static final String LOCALE_CHANGED_PATTERN = ".*locale.*";

	final Map<Key, Collection<Observer>> observers = new HashMap<>();
	
	private SecurityContext securityContext;
	
	private final boolean singleEvents;
	
	public SubjectImpl(final SecurityContext securityContext, final boolean singleEvents){
		this.securityContext=securityContext;
		this.singleEvents=singleEvents;
	}

	@Override
	public Observer register(final Key key, final Observer observer) {
		if (!observers.containsKey(key)) {
			observers.put(key, new HashSet<>());
		} 
		if( singleEvents) {
			observers.get(key).clear();
		}
		observers.get(key).add(observer);
		return observers.get(key).stream().filter(existingObjerver -> observer.equals(existingObjerver)).findFirst().get();
	}

	@Override
	public void notifyObservers(final Key key) {
		if (!observers.containsKey(key)) {
			return;
		}
		observers.get(key).forEach(observer -> observer.process());

	}

	@Override
	public Optional<Authentication> currentUser() {
		return securityContext.authentication();
	}
	
	@Override
	public void assign(final Locale locale) {
		Assert.notNull(locale, "Locale is mandatory.");
		securityContext.assign(locale);
		notifyLocaleChangedObservers();
		
	}
	
	private void notifyLocaleChangedObservers() {
		observers.keySet().stream().filter(key -> key.toString().toLowerCase().matches(LOCALE_CHANGED_PATTERN)).forEach(key -> notifyObservers(key));
		
	}

	@Override
	public Locale locale() {
		return securityContext.locale();
	}
	
	
	

}
