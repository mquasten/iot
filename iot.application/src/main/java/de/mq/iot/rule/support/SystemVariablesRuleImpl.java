package de.mq.iot.rule.support;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import de.mq.iot.state.State;
import de.mq.iot.state.StateService;
import de.mq.iot.state.support.ItemList;

@Rule(name="calendarRule", priority=2)
public class SystemVariablesRuleImpl {
	
	
	static final String TEMPERATURE_STATE_NAME = "Temperature";
	static final String LAST_BATCHRUN_STATE_NAME = "LastBatchrun";
	static final String MONTH_STATE_NAME = "Month";
	static final String WORKINGDAY_STATE_NAME = "Workingday";
	static final String TIME_STATE_NAME = "Time";
	
	private final StateService stateService;
	
	SystemVariablesRuleImpl(StateService stateService) {
		this.stateService = stateService;
	}

	@Condition
	 public void updateSystemVariables(@Fact("calendar") final Calendar calendar, @Fact("states") final Collection<State<?>> results) {
		 final Map<String, State<?>> states = stateService.states().stream().collect(Collectors.toMap(State::name, state -> state));
		 
		 final Consumer<? super State<?>> addState = state -> results.add(state);
		 if( states.containsKey(TIME_STATE_NAME)) {
			
			
			changedState( states.get(TIME_STATE_NAME), calendar.time()).ifPresent( addState);
			 
		 }
		 if( states.containsKey(MONTH_STATE_NAME)) {
			 
			 changedState(states.get(MONTH_STATE_NAME), calendar.month()).ifPresent(addState);
		 }
		
		 
		
	 }

	private Optional<State<?>> changedState(final State<?> state, final Enum<?>  value ) {
		 final ItemList itemState = (ItemList) state;
		if( itemState.hasLabel(value.name())){
			 return Optional.empty();
		 }
		itemState.assign(value.name());
		return Optional.of((State<?>)itemState);
	}
	
	

}
