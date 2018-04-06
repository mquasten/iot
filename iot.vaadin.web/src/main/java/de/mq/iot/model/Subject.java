package de.mq.iot.model;

public interface Subject<Key,Model> {
	
	Observer register(final Key key, final Observer observer);

	void notifyObservers(final Key key); 
	
	

}
