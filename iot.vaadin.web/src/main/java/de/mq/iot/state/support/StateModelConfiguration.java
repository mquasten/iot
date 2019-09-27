package de.mq.iot.state.support;

import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.spring.annotation.UIScope;

import de.mq.iot.model.Subject;
import de.mq.iot.model.support.SubjectImpl;
import de.mq.iot.state.State;
import de.mq.iot.support.ButtonBox;

@Configuration
class StateModelConfiguration  {

	static final String USER_VIEW = "i18n/userView";
	static final String SYSTEM_VARIABLES_VIEW = "i18n/systemVariablesView";
	static final String LOGIN_VIEW = "i18n/loginView";
	static final String CALENDAR_VIEW = "i18n/calendarView";
	static final String RULES_VIEW = "i18n/rulesDefinitionView";
	static final String MESSAGE_SOURCE_ENCODING = "UTF-8";
	
	static final String DEVICE_VIEW = "i18n/deviceView";
	static final String[] MESSAGE_SOURCE_BASENAME = { SYSTEM_VARIABLES_VIEW , LOGIN_VIEW, CALENDAR_VIEW, DEVICE_VIEW, RULES_VIEW, USER_VIEW };
	
	
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
	//@UIScope
	@Scope("prototype")
	StateModel stateModel(final Subject<StateModel.Events, StateModel> subject, ConversionService conversionService) {
		return new StateModelImpl(subject, conversionService);

	}
	
	
	@Bean
	//@UIScope
	@Scope("prototype")
	DeviceModel deviceModel(final Subject<DeviceModel.Events, DeviceModel> subject, final ConversionService conversionService) {
		return new DeviceModelImpl(subject, conversionService);

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
	
	@Bean
	@UIScope
	ButtonBox buttonBox() {
		return new ButtonBox();
	}
	
   
	
}
