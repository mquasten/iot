package de.mq.iot.authentication.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.SecurityContext;

class SecurityContextTest {

	private final SecurityContext securityContext = new SecurityContextImpl();
	
	private final Authentication authentication = Mockito.mock(Authentication.class);
	
	@Test
	void authentication() {
		assertEquals(Optional.empty(), securityContext.authentication());
		
		securityContext.assign(authentication);
		assertEquals(Optional.of(authentication), securityContext.authentication());
	}
	
}
