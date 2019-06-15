package de.mq.iot.rule.support;

import java.util.Collection;
import java.util.Map.Entry;
import de.mq.iot.model.LocaleAware;
import de.mq.iot.model.Subject;
import de.mq.iot.rule.RulesDefinition;


public interface RuleDefinitionModel extends Subject<RuleDefinitionModel.Events, RuleDefinitionModel> , LocaleAware {
		
		enum Events {
			AssignRuleDefinition,
			AssignInput,
			ChangeLocale;
		}

		void assignSelected(final RulesDefinition rulesDefinition);

	

		Collection<Entry<String, String>> input();

		Collection<Entry<String, String>> parameter();



		boolean isSelected();



		Collection<String> optionalRules();



		void assignSelectedInput(Entry<String, String> value);


		String selectedInputValue();

	
		
		
		

}
