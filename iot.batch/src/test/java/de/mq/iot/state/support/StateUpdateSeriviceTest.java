package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.jeasy.rules.annotation.Rule;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import de.mq.iot.rule.RulesDefinition;
import de.mq.iot.rule.support.RulesAggregate;
import de.mq.iot.rule.support.RulesAggregateResult;
import de.mq.iot.rule.support.RulesService;
import de.mq.iot.rule.support.TemperatureRuleImpl;
import de.mq.iot.state.State;

public class StateUpdateSeriviceTest {





	private static final String MIN_EVENT_TIME = "11:11";

	private final  RulesService rulesService = Mockito.mock(RulesService.class);
	
	private final StateUpdateServiceImpl stateUpdateService = new StateUpdateServiceImpl(rulesService);
	
	
	static final String LAST_BATCHRUN_STATE_NAME = "LastBatchrun";
	
	

	
	

	
	
	

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	final void processRules() {
		
		final State<String> state = Mockito.mock(State.class);
		Mockito.when(state.name()).thenReturn(LAST_BATCHRUN_STATE_NAME);
		Mockito.when(state.value()).thenReturn("22:30");
	
		final RulesAggregate<State<?>> rulesAggregate = Mockito.mock(RulesAggregate.class);
		
		final RulesAggregateResult<State<?>> rulesAggregateResult = Mockito.mock(RulesAggregateResult.class);
		Mockito.when(rulesAggregateResult.exceptions()).thenReturn(Arrays.asList(new AbstractMap.SimpleImmutableEntry<>(TemperatureRuleImpl.class.getAnnotation(Rule.class).name(), Mockito.mock(Exception.class))));
		Mockito.when(rulesAggregateResult.states()).thenReturn(Arrays.asList(state));
		ArgumentCaptor<RulesDefinition.Id> idCapture = ArgumentCaptor.forClass(RulesDefinition.Id.class);
		Mockito.when(rulesAggregate.fire()).thenReturn(rulesAggregateResult);
		
		ArgumentCaptor<Collection<Entry<String,String>>> parametersCapture = ArgumentCaptor.forClass(Collection.class);
		Mockito.when(rulesService.rulesAggregate(idCapture.capture(), parametersCapture.capture())).thenReturn( ((RulesAggregate) rulesAggregate));
		
		stateUpdateService.processRules(RulesDefinition.Id.DefaultDailyIotBatch.name(), true, true, MIN_EVENT_TIME);
		
		Mockito.verify(rulesAggregate).fire();
		
		assertEquals(RulesDefinition.Id.DefaultDailyIotBatch, idCapture.getValue());
		
		assertEquals(3, parametersCapture.getValue().size());
		
		assertTrue(parametersCapture.getValue().stream().map(Entry::getKey).collect(Collectors.toList()).containsAll(Arrays.asList(RulesDefinition.TEST_MODE_KEY, RulesDefinition.UPDATE_MODE_KEY, RulesDefinition.MIN_EVENT_TIME_KEY )));
		parametersCapture.getValue().stream().filter(entry  -> ! entry.getKey().equals(RulesDefinition.MIN_EVENT_TIME_KEY) ).map(Entry::getValue).forEach(value -> assertTrue(Boolean.valueOf(value)));
		
		parametersCapture.getValue().stream().filter(entry  -> entry.getKey().equals(RulesDefinition.MIN_EVENT_TIME_KEY) ).map(Entry::getValue).forEach(value -> assertEquals(MIN_EVENT_TIME, value));
	}
	
}
