package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.rule.RulesDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class RulesServiceTest {
	
	private static final Integer TIMEOUT = 500;
	private static final String TIMEOUT_FIELD = "timeout";
	private final RulesServiceImpl rulesService = Mockito.mock(RulesServiceImpl.class, Mockito.CALLS_REAL_METHODS); 
	private final RulesAggregate<?> defaultDailyIotBatchRulesAggregate = Mockito.mock(RulesAggregate.class);
	
	private final RulesAggregate<?> endOfDayBatchRulesAggregate = Mockito.mock(RulesAggregate.class);
	private final  RulesDefinitionRepository rulesDefinitionRepository = Mockito.mock(RulesDefinitionRepository.class);
	
	private RulesDefinition rulesDefinition = Mockito.mock(RulesDefinition.class);
	
	@BeforeEach
	void setup() {
		Mockito.doReturn(Mono.just(rulesDefinition)).when(rulesDefinitionRepository).findById(RulesDefinition.Id.DefaultDailyIotBatch);
		Mockito.doReturn(RulesDefinition.Id.DefaultDailyIotBatch).when(rulesDefinition).id();
		
		Mockito.doReturn(RulesDefinition.Id.DefaultDailyIotBatch).when(defaultDailyIotBatchRulesAggregate).id();
		Mockito.doReturn(RulesDefinition.Id.EndOfDayBatch).when(endOfDayBatchRulesAggregate).id();
		Mockito.doReturn(Arrays.asList(endOfDayBatchRulesAggregate, defaultDailyIotBatchRulesAggregate)).when(rulesService).rulesAggregates();
		
		Arrays.asList(RulesServiceImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(RulesDefinitionRepository.class)).forEach(field -> ReflectionTestUtils.setField(rulesService, field.getName(), rulesDefinitionRepository));
		
		ReflectionTestUtils.setField(rulesService, TIMEOUT_FIELD, Duration.ofMillis(TIMEOUT));
	}
	
	@Test
	void rulesAggregate() {
		
		final RulesAggregate<?> result 	= rulesService.rulesAggregate(RulesDefinition.Id.DefaultDailyIotBatch, Arrays.asList(new AbstractMap.SimpleImmutableEntry<>(RulesDefinition.UPDATE_MODE_KEY, Boolean.TRUE.toString())));
		
		assertEquals(defaultDailyIotBatchRulesAggregate, result);
		
		Mockito.verify(defaultDailyIotBatchRulesAggregate).with(rulesDefinition);
		
		Mockito.verify(rulesDefinition).assign(RulesDefinition.UPDATE_MODE_KEY, Boolean.TRUE.toString());
		
	}
	
	@Test
	void rulesAggregateObject() {
		final RulesAggregate<?> result 	= rulesService.rulesAggregate(rulesDefinition);
		
		Mockito.verify(defaultDailyIotBatchRulesAggregate).with(rulesDefinition);
		assertEquals(defaultDailyIotBatchRulesAggregate, result);
		
	}
	
	@Test
	void rulesDefinitions() {
		
		final Flux<RulesDefinition> flux= Flux.fromStream(Arrays.asList(rulesDefinition).stream());
		
		Mockito.when(rulesDefinitionRepository.findAll()).thenReturn(flux);
		final Collection<RulesDefinition> results =rulesService.rulesDefinitions();
		assertEquals(1, results.size());
		
		assertEquals(rulesDefinition, results.stream().findFirst().get());
	}
	
	@Test
	void save() {
		
		final RulesDefinition rulesDefinition = new RulesDefinitionImpl(RulesDefinition.Id.DefaultDailyIotBatch);
		rulesDefinition.assign(RulesDefinition.HOLIDAY_ALARM_TIME_KEY, "7:15");
		rulesDefinition.assign(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY, "5:15");
		rulesDefinition.assign(RulesDefinition.MIN_SUN_DOWN_TIME_KEY, " ");
		@SuppressWarnings("unchecked")
		final Mono<RulesDefinition> mono = Mockito.mock(Mono.class);
		
		Mockito.doReturn(mono).when(rulesDefinitionRepository).save(Mockito.any(RulesDefinition.class));
		final ArgumentCaptor<RulesDefinition> ruleDefinitionCaptor = ArgumentCaptor.forClass(RulesDefinition.class);
		
		
		rulesService.save(rulesDefinition);
		
		Mockito.verify(mono).block(Duration.ofMillis(TIMEOUT));
		
		Mockito.verify(rulesDefinitionRepository).save(ruleDefinitionCaptor.capture());
		
		assertEquals(rulesDefinition, ruleDefinitionCaptor.getValue());
		
		assertEquals(2, ruleDefinitionCaptor.getValue().inputData().size());
		assertTrue(ruleDefinitionCaptor.getValue().inputData().containsKey(RulesDefinition.HOLIDAY_ALARM_TIME_KEY));
		assertTrue(ruleDefinitionCaptor.getValue().inputData().containsKey(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY));
		assertFalse(ruleDefinitionCaptor.getValue().inputData().containsKey(RulesDefinition.MIN_SUN_DOWN_TIME_KEY));
	}

}
