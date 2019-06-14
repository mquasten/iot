package de.mq.iot.rule.support;

import java.util.Optional;

import de.mq.iot.model.LocaleAware;
import de.mq.iot.model.Subject;
import de.mq.iot.rule.RulesDefinition;


public interface RuleDefinitionModel extends Subject<RuleDefinitionModel.Events, RuleDefinitionModel> , LocaleAware {
		
		enum Events {
			AssignRuleDefinition,
			ChangeLocale;
		}

		void assignSelected(RulesDefinition rulesDefinition);

		Optional<RulesDefinition> selectedRuleDefinition();
		
		
		

}
