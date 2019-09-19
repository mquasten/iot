package de.mq.iot.authentication.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.Authority;

class AuthenticationTest {
	
	private static final String PASSWORD = "fever";
	private static final String USERNAME = "kminogue";
	private final Authentication authentication = new UserAuthenticationImpl(USERNAME, PASSWORD, Arrays.asList(Authority.ModifySystemvariables));
	
	@Test
	void authorities() {
		assertEquals(1, authentication.authorities().size());
		assertEquals(Optional.of(Authority.ModifySystemvariables), authentication.authorities().stream().findAny());
	}
	@Test
	void authoritiesNull() {
		assertEquals(0, new UserAuthenticationImpl(USERNAME, PASSWORD, null).authorities().size());
	}
	
	@Test
	void defaultConstructor() {
		final Authentication authentication = BeanUtils.instantiateClass(UserAuthenticationImpl.class);
		assertTrue(authentication.username().isEmpty());
		assertTrue(authentication.authorities().isEmpty());
	}
	
	@Test
	void username() {
		assertEquals(USERNAME, authentication.username());
	}
	
	@Test
	void authenticate() {
		assertTrue(authentication.authenticate(PASSWORD));
	}
	
	@Test
	void authenticateNullString() {
		final Authentication authentication = BeanUtils.instantiateClass(UserAuthenticationImpl.class);
		assertFalse(authentication.authenticate(PASSWORD));
	}
	@Test
	void hash() {
		assertEquals(USERNAME.hashCode(), authentication.hashCode());
	}
	
	@Test
	void equals() {
		assertTrue(authentication.equals(authentication));
		assertTrue(authentication.equals(new UserAuthenticationImpl(USERNAME, "?", Arrays.asList())));
		assertFalse(authentication.equals(new UserAuthenticationImpl(PASSWORD, PASSWORD, Arrays.asList())));
		assertFalse(authentication.equals(new Object()));
	}
	
	@Test
	void  string() {
		assertEquals(USERNAME, authentication.toString());
	}
	
	@Test
	void hasRole() {
		assertTrue(authentication.hasRole(Authority.ModifySystemvariables));
		
		assertFalse(authentication.hasRole(Authority.ModifyUsers));
	}

}
