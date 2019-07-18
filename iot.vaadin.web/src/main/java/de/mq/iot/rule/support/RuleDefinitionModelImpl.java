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

import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;

import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;
import de.mq.iot.rule.RulesDefinition;

class RuleDefinitionModelImpl implements RuleDefinitionModel {

	private final Subject<RuleDefinitionModel.Events, RuleDefinitionModel> subject;

	private Optional<RulesDefinition> rulesDefinition = Optional.empty();
	
	private Optional<Entry<String,String>> selectedInput = Optional.empty();
	
	
	private Optional<Entry<String,String>> selectedArgument = Optional.empty();
	
	private Optional<String> selectedOptionalRule = Optional.empty();

	private final ValidationFactory validationFactory;
	RuleDefinitionModelImpl(final Subject<Events, RuleDefinitionModel> subject, final ValidationFactory validationFactory) {
		this.subject = subject;
		this.validationFactory=validationFactory;
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
	public Optional<RulesDefinition> selected() {
		return rulesDefinition;
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
	
	@Override
	public Collection<String> definedOptionalRules() {
		if (!rulesDefinition.isPresent()) {
			return Arrays.asList();
		}
		return rulesDefinition.get().id().optionalRules();
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
	public void assignSelectedArgument(Entry<String, String> input) {
		this.selectedArgument = Optional.ofNullable(input);
		notifyObservers(Events.AssignArgument);
		
	}
	
	@Override
	public String selectedInputValue() {
		if( !selectedInput.isPresent()) {
			return "";
		}
		
		return selectedInput.get().getValue();
	}
	
	
	@Override
	public String selectedArgumentValue() {
		if( !selectedArgument.isPresent()) {
			return "";
		}
		
		return selectedArgument.get().getValue();
	}
	
	@Override
	public boolean isInputSelected() {
		return selectedInput.isPresent();
	}
	
	@Override
	public boolean isArgumentSelected() {
		return selectedArgument.isPresent();
	}
	
	@Override
	public Optional<String> selectedInputKey() {
		
		if(! selectedInput.isPresent()) {
			return Optional.empty();
		}
		return Optional.ofNullable(selectedInput.get().getKey());
	}
	
	@Override
	public void assignInput(final String value) {
		selectedInput.ifPresent(entry -> rulesDefinition.get().assign(entry.getKey(), value));
	}
	
	@Override
	public void assignArgument(final String value) {
		selectedArgument.ifPresent(entry -> rulesDefinition.get().assign(entry.getKey(), value));
	}
	
	@Override
	public Optional<String> validateInput(final String value) {
		Assert.isTrue(rulesDefinition.isPresent(), "RuleDefinition not selected.");
		Assert.isTrue(selectedInput.isPresent(), "InputParameter not selected.");
		final Validator validator = validationFactory.validator(rulesDefinition.get().id(), selectedInputKey().get());
		 final Errors errors = new MapBindingResult(new HashMap<>(), RulesAggregate.RULE_INPUT_MAP_FACT);
		 validator.validate(value, errors);
		 final Collection<ObjectError> allErrors =  errors.getAllErrors();
		 if( allErrors.stream().findFirst().isPresent()) {
			 return Optional.of(allErrors.stream().findFirst().get().getCode());
			
		 }
		 return Optional.empty();
	}

	@Override
	public void assignSelectedOptionalRule(final String value) {
		selectedOptionalRule=Optional.ofNullable(value);
		notifyObservers(Events.AssignOptionalRule);
		
	}
	
	
	
	@Override
	public void addOptionalRule(final String optionalRule) {
	
		rulesDefinition.ifPresent(rd ->rd.assignRule(optionalRule));
		notifyObservers(Events.ChangeOptionalRules);
	}
	
	@Override
	public void removeOptionalRule() {
		rulesDefinition.ifPresent(rd -> selectedOptionalRule.ifPresent(rule -> rd.removeOptionalRule(rule)));
		notifyObservers(Events.ChangeOptionalRules);
	}
	
	@Override
	public boolean isOptionalRuleSelected() {
		return selectedOptionalRule.isPresent();
	}
	@Override
	public  Collection<Entry<String,String>> validateInput() {
		final Map<String,String> results = new HashMap<>();
		rulesDefinition.ifPresent(rd -> validate(rd, results));
		
		return Collections.unmodifiableCollection(results.entrySet());
	}

	private void validate(final RulesDefinition rd, final Map<String,String> messages) {
		final Map<String,String> values = rd.inputData();
	
		rd.id().input().forEach(field -> {
			final Errors errors = new MapBindingResult(new HashMap<>(), RulesAggregate.RULE_INPUT_MAP_FACT);
			final Validator validator = validationFactory.validator(rd.id(), field);
			validator.validate(values.get(field), errors);
			errors.getAllErrors().stream().findFirst().ifPresent(error -> messages.put(field, error.getCode()));
			
			});
		
	}

}
