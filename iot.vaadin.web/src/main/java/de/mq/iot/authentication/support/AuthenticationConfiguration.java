package de.mq.iot.authentication.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

import de.mq.iot.authentication.SecurityContext;

@Configuration
class AuthenticationConfiguration {

	@Bean()
	@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, scopeName = "prototype")
	UI ui() {
		return UI.getCurrent();
	}

	@Bean()
	@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, scopeName = "session")
	SecurityContext securityContext() {
		return new SecurityContextImpl();
	}

	@Bean
	@UIScope
	LoginModel loginModel() {
		return new LoginModelImpl();
	}

	@Bean
	BeanPostProcessor beanPostProcessor(final UI ui, final SecurityContext securityContext) {
		return new BeanPostProcessor() {
			public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

				if (bean.getClass().isAnnotationPresent(Route.class)) {
					ui.addBeforeEnterListener(new SimpleBeforeEnterListenerImpl(securityContext));
				}

				return bean;
			}

		};
	}

}
