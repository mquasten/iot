package de.mq.iot.rule.support;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;


import de.mq.iot.state.State;
import de.mq.iot.state.StateService;
import de.mq.iot.state.support.ItemList;

@Rule(name = "systemVariablesRule", priority=2)
public class SystemVariablesRuleImpl {

	static final String TEMPERATURE_STATE_NAME = "Temperature";
	static final String LAST_BATCHRUN_STATE_NAME = "LastBatchrun";
	static final String MONTH_STATE_NAME = "Month";
	static final String WORKINGDAY_STATE_NAME = "Workingday";
	static final String TIME_STATE_NAME = "Time";
	static final String LAST_BATCHRUN_DATE_FORMAT = "dd.MM.yyyy-HH:mm:ss";


	private final StateService stateService;

	SystemVariablesRuleImpl(StateService stateService) {
		this.stateService = stateService;
	}
	
	
	@Condition
	public  boolean evaluate(@Fact(RulesAggregate.RULE_CALENDAR) final Calendar calendar) {
		return calendar.valid();
	}

	@Action
	public void updateSystemVariables(@Fact(RulesAggregate.RULE_CALENDAR) final Calendar calendar, @Fact(RulesAggregate.RULE_OUTPUT_MAP_FACT) final Collection<State<?>> results) {
	
		
		final Map<String, State<?>> states = stateService.states().stream().collect(Collectors.toMap(State::name, state -> state));
		final Consumer<? super State<?>> addState = state -> results.add(state);
		
		
		itemValues(calendar).entrySet().stream().filter(entry -> states.containsKey(entry.getKey())).forEach(entry -> changedState(states.get(entry.getKey()), entry.getValue()).ifPresent(addState));
		
		stateValues(calendar).entrySet().stream().filter(entry -> states.containsKey(entry.getKey())).forEach(entry -> changedState(states.get(entry.getKey()), entry.getValue()).ifPresent(addState));
	
	    Collections.sort((List<State<?>>) results, (s1, s2) -> priority(s1.name()) - priority(s2.name()) );
	}

	private int priority(final String name ) {
		return LAST_BATCHRUN_STATE_NAME.equals(name) ? 1 :0;
	}
	
	private Map<String, Object> stateValues(final Calendar calendar) {
		final Map<String,Object> stateValues = new HashMap<>();
		stateValues.put(WORKINGDAY_STATE_NAME, calendar.workingDay());
		
		calendar.temperature().ifPresent(temperature -> stateValues.put(TEMPERATURE_STATE_NAME, temperature));
		stateValues.put(LAST_BATCHRUN_STATE_NAME, new SimpleDateFormat(LAST_BATCHRUN_DATE_FORMAT).format(new Date(System.currentTimeMillis())));
		return stateValues;
	}

	private Map<String, Enum<?>> itemValues(final Calendar calendar) {
		
		
		final Map<String,Enum<?>> itemValues = new HashMap<>();
		itemValues.put(TIME_STATE_NAME, calendar.time());
		itemValues.put(MONTH_STATE_NAME, calendar.month());
		
		
		return itemValues;
	}

	private Optional<State<?>> changedState(final State<?> state, final Enum<?> value) {
		
		
		final ItemList itemState = (ItemList) state;
		if (itemState.hasLabel(value.name())) {
			return Optional.empty();
		}
		itemState.assign(value.name());
		return Optional.of((State<?>) itemState);
	}

	private Optional<State<?>> changedState(final State<?> state, final Object value) {
		@SuppressWarnings("unchecked")
		State<Object> simpleState = (State<Object>) state;
		if (simpleState.hasValue(value)) {
			return Optional.empty();
		}
		simpleState.assign(value);
		return Optional.of(state);
	}

}
