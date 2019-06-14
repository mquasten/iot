package de.mq.iot.rule.support;

import java.util.Locale;
import java.util.Optional;


import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;
import de.mq.iot.rule.RulesDefinition;

class RuleDefinitionModelImpl implements RuleDefinitionModel {

	private final Subject<RuleDefinitionModel.Events, RuleDefinitionModel> subject;

	private Optional<RulesDefinition> rulesDefinition =  Optional.empty();
	
	RuleDefinitionModelImpl(final Subject<Events, RuleDefinitionModel> subject) {
		this.subject = subject;
		
	}

	@Override
	public final Observer register(final Events key, final Observer observer) {
		return subject.register(key, observer);
	}

	@Override
	public final void notifyObservers(final Events key) {
		subject.notifyObservers(key);

	}

	
	@Override
	public void assignSelected(final RulesDefinition rulesDefinition) {
		
		this.rulesDefinition=Optional.ofNullable(rulesDefinition);
		notifyObservers(Events.AssignRuleDefinition);
	}
	@Override
	public
	Optional<RulesDefinition>  selectedRuleDefinition() {
		return this.rulesDefinition;
	}

	@Override
	public Locale locale() {
		return Locale.GERMAN;
	}

}
