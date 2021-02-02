package de.mq.iot.model;

import java.util.Locale;
import java.util.Optional;

import de.mq.iot.authentication.Authentication;

public interface Subject<Key,Model> {
	
	Observer register(final Key key, final Observer observer);

	void notifyObservers(final Key key); 
	
	default Optional<Authentication> currentUser() {
		return Optional.empty();
	}
	
	Locale locale();
	

}
