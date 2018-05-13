package de.mq.iot.authentication;

import java.util.Collection;



public interface Authentication{
	
	Collection<String> 	authorities();
	String username();
	

	
	

}
