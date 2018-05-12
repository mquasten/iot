package de.mq.iot.model.support;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.util.Assert;

import de.mq.iot.model.Authentication;

public class UserAuthentificationImpl  implements Authentication {

	private final Collection<String> authorities = new ArrayList<>();
	
	
	private final String username;
	public UserAuthentificationImpl(final String username, final Collection<String> authorities) {
		Assert.notNull(username, "Username is mandatory");
		this.username=username;
		this.authorities.addAll(authorities);
		
	
	}	

	@Override
	public Collection<String> authorities() {
		return Collections.unmodifiableCollection(authorities);
	}

	@Override
	public String username() {
		return username;
	}


	
	
	

}
