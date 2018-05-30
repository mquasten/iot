package de.mq.iot.authentication.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

import de.mq.iot.authentication.SecurityContext;
import de.mq.iot.model.Subject;

@Configuration
class AuthenticationConfiguration {

	static final String CURRENT_UI_METHOD_NAME = UI.class.getName() +".getCurrent";

	@Bean()
	@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, scopeName = "prototype")
	UI ui(final MethodInvokingFactoryBean methodInvokingFactoryBean) throws Exception {
		methodInvokingFactoryBean.prepare();
		return (UI) methodInvokingFactoryBean.invoke();
	}

	@Bean()
	@Scope("prototype")
	MethodInvokingFactoryBean methodInvokingFactoryBean() {
		MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
		methodInvokingFactoryBean.setTargetClass(UI.class);
		methodInvokingFactoryBean.setStaticMethod(CURRENT_UI_METHOD_NAME);
		return methodInvokingFactoryBean;
	}

	@Bean()
	@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, scopeName = "session")
	SecurityContext securityContext() {
		return new SecurityContextImpl();
	}

	@Bean
	@UIScope
	LoginModel loginModel(final Subject<LoginModel.Events, LoginModel> subject) {
		return new LoginModelImpl(subject);
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
