package de.mq.iot.rule.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import com.vaadin.flow.component.dialog.Dialog;

import de.mq.iot.model.Subject;

@Configuration
class RulesDefinitionConfiguration {
	
	@Bean
	//@UIScope
	@Scope("prototype")
	RuleDefinitionModel ruleDefinitionModel(final Subject<RuleDefinitionModel.Events, RuleDefinitionModel> subject, final ValidationFactory validationFactory) {
		return new RuleDefinitionModelImpl(subject,validationFactory);

	}
	
	@Bean()
	@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, scopeName = "prototype")
	SimpleAggrgationResultsDialog simpleAggrgationResultsDialog(final Dialog dialog) {
		return new SimpleAggrgationResultsDialog(new Dialog());
	}
	


}
