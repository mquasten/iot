package de.mq.iot.authentication.support;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.SecurityContext;

public class BeforeEnterListenerTest {
	
	private final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
	
	private final BeforeEnterListener beforeEnterListener = new SimpleBeforeEnterListenerImpl (securityContext);
	
	private final BeforeEnterEvent beforeEnterEvent = Mockito.mock(BeforeEnterEvent.class);
	
	
	@Test
	void beforeEnter() {
		final Class<?> navibationTarget = Mockito.mock(Object.class).getClass();
		Mockito.doReturn(navibationTarget).when(beforeEnterEvent).getNavigationTarget();
		beforeEnterListener.beforeEnter(beforeEnterEvent);
		
		Mockito.verify(beforeEnterEvent).rerouteTo(LoginView.class);
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Test
	void beforeEnterLoginView() {
		Mockito.doReturn(LoginView.class).when(beforeEnterEvent).getNavigationTarget();
		beforeEnterListener.beforeEnter(beforeEnterEvent);
		
		Mockito.verify(beforeEnterEvent, Mockito.never()).rerouteTo(Mockito.any(Class.class));
	}
	
	@Test
	void beforeEnterSecurityContextExists() {
		final Authentication authentication = Mockito.mock(Authentication.class);
		final Class<?> navibationTarget = Mockito.mock(Object.class).getClass();
		Mockito.doReturn(navibationTarget).when(beforeEnterEvent).getNavigationTarget();
		Mockito.doReturn(Optional.of(authentication)).when(securityContext).authentication();
		beforeEnterListener.beforeEnter(beforeEnterEvent);
		
		Mockito.verify(beforeEnterEvent, Mockito.never()).rerouteTo(LoginView.class);
	}
	

}


