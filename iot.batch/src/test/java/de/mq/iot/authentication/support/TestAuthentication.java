package de.mq.iot.authentication.support;

import java.util.Arrays;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.Authority;





public interface TestAuthentication {
	
	
	
	public static  Authentication authentication() {
		return  new UserAuthenticationImpl("kminogue", "fever", Arrays.asList(Authority.ModifySystemvariables));
	}

}
