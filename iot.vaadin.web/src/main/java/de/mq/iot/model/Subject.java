package de.mq.iot.model;

import java.util.Optional;

import de.mq.iot.authentication.Authentication;

public interface Subject<Key,Model>  extends LocaleAware{
	
	Observer register(final Key key, final Observer observer);

	void notifyObservers(final Key key); 
	
	default Optional<Authentication> currentUser() {
		return Optional.empty();
	}
	
	
	

}
