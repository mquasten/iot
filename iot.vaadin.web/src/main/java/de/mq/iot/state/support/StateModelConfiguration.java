package de.mq.iot.state.support;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

import com.vaadin.flow.spring.annotation.UIScope;

import de.mq.iot.model.Subject;
import de.mq.iot.model.support.SubjectImpl;

@Configuration
class StateConfiguration {
	
	static final String MESSAGE_SOURCE_ENCODING = "UTF-8";
	static final String[] MESSAGE_SOURCE_BASENAME =  {"i18n/systemVariablesView"};
	
	@Bean
	@UIScope
	Subject<?,?> subject() {
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
	
}
