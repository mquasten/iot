package de.mq.iot.state.support;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

import de.mq.iot.model.Subject;
import de.mq.iot.model.support.SubjectImpl;

@Configuration
class StateModelConfiguration  {

	static final String SYSTEM_VARIABLES_VIEW = "i18n/systemVariablesView";
	static final String MESSAGE_SOURCE_ENCODING = "UTF-8";
	static final String[] MESSAGE_SOURCE_BASENAME = { SYSTEM_VARIABLES_VIEW };
	
	
	private final Class<? extends Dialog> dialogClass = Dialog.class; 

	@Bean
	@UIScope
	Subject<?, ?> subject() {
		return new SubjectImpl<>();
	}
	
	

	@Bean
	Converter<State<?>, String> stateValueConverter(final ConversionService conversionService) {
		return new StateValueConverterImpl(conversionService);

	}

	@Bean
	@UIScope
	StateModel stateModel(final Subject<StateModel.Events, StateModel> subject, ConversionService conversionService) {
		return new StateModelImpl(subject, conversionService);

	}

	@Bean
	MessageSource messageSource() {
		final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasenames(MESSAGE_SOURCE_BASENAME);
		messageSource.setDefaultEncoding(MESSAGE_SOURCE_ENCODING);
		return messageSource;
	}
	
	@Bean()
	@Scope(scopeName = "prototype")
	Dialog dialog()  {
		return BeanUtils.instantiateClass(dialogClass);
	}

	@Bean()
	@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, scopeName = "prototype")
	SimpleNotificationDialog notificationDialog(final Dialog dialog ) {
		return new SimpleNotificationDialog(dialog);
	}
	
	
	@Bean()
	@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, scopeName = "prototype")
	UI ui () {
		return UI.getCurrent();
	}
	
	@Bean
	@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, scopeName = "session")
	SecurityContext securityContext() {
		
		System.out.println("new SecurityContext ...");
		SecurityContextHolder.setContext(new SecurityContextImpl(new UsernamePasswordAuthenticationToken("", "")));
		return SecurityContextHolder.getContext();
	}
	
	
	@Bean
	BeanPostProcessor beanPostProcessor(final UI ui, SecurityContext securityContext) {
		return new BeanPostProcessor() {
			public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
				
				if( bean.getClass().isAnnotationPresent(Route.class)) {	
					ui.addBeforeEnterListener(new SimpleBeforeEnterListenerImpl(securityContext));
				}
				
				return bean;
			}
			
		};
	}

}
