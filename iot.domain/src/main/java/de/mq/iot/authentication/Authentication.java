package de.mq.iot.authentication;

import java.util.Collection;



public interface Authentication{
	
	Collection<Authority> 	authorities();
	String username();
	

	boolean authenticate(final String credentials);
	

}
