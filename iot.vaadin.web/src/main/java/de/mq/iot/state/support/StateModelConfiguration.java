package de.mq.iot.state.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

import com.vaadin.flow.spring.annotation.UIScope;

import de.mq.iot.model.Subject;
import de.mq.iot.model.support.SubjectImpl;

@Configuration
class StateConfiguration {
	
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
	
}
