package de.mq.iot.authentication.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.authentication.Authentication;

class LoginModelTest {
	
	private static final String PASSWD = "fever";
	private static final String USER = "kminogue";
	private final LoginModel loginModel = new LoginModelImpl();
	
	@Test
	void login() {
		assertNull(loginModel.login());
		
		loginModel.assignLogin(USER);
		
		assertEquals(USER, loginModel.login());
	}
	
	@Test
	void password() {
		assertNull(loginModel.password());
		
		loginModel.assignPassword(PASSWD);
		
		assertEquals(PASSWD, loginModel.password());
	}
	
	
	@Test
	void  authenticate() {
		final Authentication authentication=Mockito.mock(Authentication.class);
		loginModel.assignPassword(PASSWD);
		loginModel.authenticate(authentication);
		
		Mockito.verify(authentication).authenticate(PASSWD);
		
	}

}
