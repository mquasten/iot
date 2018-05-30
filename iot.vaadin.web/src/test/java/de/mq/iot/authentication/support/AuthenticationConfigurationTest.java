package de.mq.iot.authentication.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterListener;

import de.mq.iot.authentication.SecurityContext;
import de.mq.iot.model.Subject;

public class AuthenticationConfigurationTest {
	
	private final AuthenticationConfiguration authenticationConfiguration = new AuthenticationConfiguration();
	
	@Test
	void ui() throws Exception {
		final MethodInvokingFactoryBean methodInvokingFactoryBean =  Mockito.mock(MethodInvokingFactoryBean.class);
		assertNull(authenticationConfiguration.ui(methodInvokingFactoryBean));
		Mockito.verify(methodInvokingFactoryBean).prepare();
		Mockito.verify(methodInvokingFactoryBean).invoke();
	}
	
	@Test
	void methodInvokingFactoryBean() {
		final MethodInvokingFactoryBean methodInvokingFactoryBean = authenticationConfiguration.methodInvokingFactoryBean();
		assertEquals(UI.class, methodInvokingFactoryBean.getTargetClass());
		assertEquals(AuthenticationConfiguration.CURRENT_UI_METHOD_NAME, ReflectionTestUtils.getField(methodInvokingFactoryBean, "staticMethod"));
		
	}

	@Test
	void securityContext() {
		assertTrue(authenticationConfiguration.securityContext() instanceof SecurityContextImpl);
		
		
	}
	
	@Test
	void loginModel() {
		@SuppressWarnings("unchecked")
		final Subject<LoginModel.Events, LoginModel> subject = Mockito.mock(Subject.class);
		final LoginModel loginModel = authenticationConfiguration.loginModel(subject);
		assertTrue(loginModel instanceof LoginModelImpl);
		
		loginModel.notifyObservers(LoginModel.Events.ChangeLocale);
		
		Mockito.verify(subject).notifyObservers(LoginModel.Events.ChangeLocale);
		
		
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
