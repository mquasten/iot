package de.mq.iot.rule.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import de.mq.iot.model.Subject;

@Configuration
class RulesDefinitionConfiguration {
	
	@Bean
	//@UIScope
	@Scope("prototype")
	RuleDefinitionModel ruleDefinitionModel(final Subject<RuleDefinitionModel.Events, RuleDefinitionModel> subject) {
		return new RuleDefinitionModelImpl(subject);

	}


}
