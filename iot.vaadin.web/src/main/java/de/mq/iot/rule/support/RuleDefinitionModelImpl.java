package de.mq.iot.rule.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;
import de.mq.iot.rule.RulesDefinition;

class RuleDefinitionModelImpl implements RuleDefinitionModel {

	private final Subject<RuleDefinitionModel.Events, RuleDefinitionModel> subject;

	private Optional<RulesDefinition> rulesDefinition = Optional.empty();
	
	private Optional<Entry<String,String>> selectedInput = Optional.empty();

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

		this.rulesDefinition = Optional.ofNullable(rulesDefinition);
		notifyObservers(Events.AssignRuleDefinition);
	}

	@Override
	public boolean isSelected() {
		return rulesDefinition.isPresent();
		
	}

	@Override
	public Collection<Entry<String, String>> input() {
		if (!rulesDefinition.isPresent()) {
			return Arrays.asList();
		}

		return inputData(rulesDefinition.get().id().input(), rulesDefinition.get().inputData().entrySet());
	}

	@Override
	public Collection<Entry<String, String>> parameter() {
		if (!rulesDefinition.isPresent()) {
			return Arrays.asList();
		}

		return inputData(rulesDefinition.get().id().parameter(), rulesDefinition.get().inputData().entrySet());
	}

	private Collection<Entry<String, String>> inputData(final Collection<String> keys, final Collection<Entry<String, String>> entries) {

		final Map<String, String> results = new HashMap<>();

		results.putAll(keys.stream().collect(Collectors.toMap(key -> key, key -> "")));

		results.putAll(entries.stream().filter(entry -> keys.contains(entry.getKey())).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));

		return Collections.unmodifiableSet(results.entrySet());

	}

	@Override
	public Locale locale() {
		return Locale.GERMAN;
	}

	@Override
	public Collection<String> optionalRules() {
		if (!rulesDefinition.isPresent()) {
			return Arrays.asList();
		}
		return rulesDefinition.get().optionalRules();
	}

	@Override
	public void assignSelectedInput(Entry<String, String> input) {
		this.selectedInput = Optional.ofNullable(input);
		notifyObservers(Events.AssignInput);
		
	}
	
	@Override
	public String selectedInputValue() {
		if( !selectedInput.isPresent()) {
			return "";
		}
		
		return selectedInput.get().getValue();
	}
	
	

}
