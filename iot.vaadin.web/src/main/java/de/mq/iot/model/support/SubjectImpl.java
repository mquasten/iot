package de.mq.iot.model.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.SecurityContext;
import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;

public class SubjectImpl<Key, Model> implements Subject<Key, Model> {

	final Map<Key, Collection<Observer>> observers = new HashMap<>();
	
	private SecurityContext securityContext;
	
	public SubjectImpl(final SecurityContext securityContext){
		this.securityContext=securityContext;
	}

	@Override
	public Observer register(final Key key, final Observer observer) {
		if (!observers.containsKey(key)) {
			observers.put(key, new HashSet<>());
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

}
