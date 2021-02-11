package de.mq.iot.authentication.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;

class LoginModelTest {
	
	private static final String PASSWD = "fever";
	private static final String USER = "kminogue";
	
	@SuppressWarnings("unchecked")
	private final Subject<LoginModel.Events, LoginModel> subject = Mockito.mock(Subject.class); 
	private final LoginModel loginModel = new LoginModelImpl(subject);
	
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
	
	@Test
	void locale() {
		assertEquals(Locale.GERMAN, loginModel.locale());
	}
	
	@Test
	void register() {
		final Observer observer = Mockito.mock(Observer.class);
		loginModel.register(LoginModel.Events.ChangeLocale, observer);
		
		Mockito.verify(subject).register(LoginModel.Events.ChangeLocale, observer);
	}
	
	
	 @Test
	 void curentUserDefault() {
		assertEquals(Optional.empty(), loginModel.currentUser());
	 }
	 
	 @Test
	 void assign() {
		 loginModel.assign(Locale.GERMAN);
		 Mockito.verify(subject).assign(Locale.GERMAN);
	 }

}
