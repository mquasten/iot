package de.mq.iot.authentication.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterListener;

import de.mq.iot.authentication.SecurityContext;

public class AuthenticationConfigurationTest {
	
	private final AuthenticationConfiguration authenticationConfiguration = new AuthenticationConfiguration();
	
	@Test
	void ui() {
		assertNull(authenticationConfiguration.ui());
	}

	@Test
	void securityContext() {
		assertTrue(authenticationConfiguration.securityContext() instanceof SecurityContextImpl);
		
		
	}
	
	@Test
	void loginModel() {
		assertTrue(authenticationConfiguration.loginModel() instanceof LoginModelImpl);
	}
	
	@Test
	void beanPostProcessor() {
		final UI ui = Mockito.mock(UI.class);
		final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		final BeanPostProcessor beanPostProcessor = authenticationConfiguration.beanPostProcessor(ui, securityContext);
		
		final LoginView loginView = Mockito.mock(LoginView.class);
		
		beanPostProcessor.postProcessAfterInitialization(loginView, "aName");
		final ArgumentCaptor<BeforeEnterListener>listenerCaptorCaptor = ArgumentCaptor.forClass(BeforeEnterListener.class);
		Mockito.verify(ui).addBeforeEnterListener(listenerCaptorCaptor.capture());
		
		assertTrue(listenerCaptorCaptor.getValue() instanceof SimpleBeforeEnterListenerImpl);
		
		
		assertEquals(securityContext, Arrays.asList(SimpleBeforeEnterListenerImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(SecurityContext.class) ).map(field -> ReflectionTestUtils.getField(listenerCaptorCaptor.getValue(), field.getName())).findAny().orElseThrow(() -> new IllegalArgumentException("Field with SecurityContext not found")));
	}
	
	@Test
	void beanPostProcessorNoUI() {
		final UI ui = Mockito.mock(UI.class);
		final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		final BeanPostProcessor beanPostProcessor = authenticationConfiguration.beanPostProcessor(ui, securityContext);
		
		final Object bean = Mockito.mock(Object.class);
		
		beanPostProcessor.postProcessAfterInitialization(bean, "aName");
		
		Mockito.verify(ui, Mockito.never()).addBeforeEnterListener(Mockito.any());
		
	}	
	
}
