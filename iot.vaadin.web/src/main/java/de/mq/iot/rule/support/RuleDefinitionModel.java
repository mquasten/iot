package de.mq.iot.rule.support;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Optional;

import de.mq.iot.model.Subject;
import de.mq.iot.rule.RulesDefinition;


public interface RuleDefinitionModel extends Subject<RuleDefinitionModel.Events, RuleDefinitionModel>  {
		
		enum Events {
			AssignRuleDefinition,
			AssignInput,
			AssignOptionalRule,
			ChangeOptionalRules,
			ChangeLocale, AssignArgument;
		}

		void assignSelected(final RulesDefinition rulesDefinition);

	

		Collection<Entry<String, String>> input();

		Collection<Entry<String, String>> parameter();



		boolean isSelected();



		Collection<String> optionalRules();



		void assignSelectedInput(Entry<String, String> value);


		String selectedInputValue();



		Optional<String> selectedInputKey();



		void assignInput(final String value);



		boolean isInputSelected();

		boolean isArgumentSelected();

		Optional<String> validateInput(String value);



		Collection<String> definedOptionalRules();



		void assignSelectedOptionalRule(final String value);



		boolean isOptionalRuleSelected();



		void removeOptionalRule();



		void addOptionalRule(final String optionalRule);



		Optional<RulesDefinition> selected();



		Collection<Entry<String, String>> validateInput();



		void assignSelectedArgument(Entry<String, String> input);



		String selectedArgumentValue();



		void assignArgument(String argumentValue);



		Optional<String> validateArgument(String value);



		public boolean isChangeAndExecuteRules(); 

	
		
		
		

}
