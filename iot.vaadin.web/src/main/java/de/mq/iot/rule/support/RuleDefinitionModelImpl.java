package de.mq.iot.rule.support;

import java.util.Locale;

import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;

class RuleDefinitionModelImpl implements RuleDefinitionModel {

	private final Subject<RuleDefinitionModel.Events, RuleDefinitionModel> subject;

	
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
	public Locale locale() {
		return Locale.GERMAN;
	}

}
