package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.AbstractMap;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.rule.RulesDefinition;
import reactor.core.publisher.Mono;

public class RulesServiceTest {
	
	private final RulesServiceImpl rulesService = Mockito.mock(RulesServiceImpl.class, Mockito.CALLS_REAL_METHODS); 
	private final RulesAggregate defaultDailyIotBatchRulesAggregate = Mockito.mock(RulesAggregate.class);
	
	private final RulesAggregate endOfDayBatchRulesAggregate = Mockito.mock(RulesAggregate.class);
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
		
	}
	
	@Test
	void rulesAggregate() {
		
		final RulesAggregate result 	= rulesService.rulesAggregate(RulesDefinition.Id.DefaultDailyIotBatch, Arrays.asList(new AbstractMap.SimpleImmutableEntry<>(RulesDefinition.UPDATE_MODE_KEY, Boolean.TRUE.toString())));
		
		assertEquals(defaultDailyIotBatchRulesAggregate, result);
		
		Mockito.verify(defaultDailyIotBatchRulesAggregate).with(rulesDefinition);
		
		Mockito.verify(rulesDefinition).assign(RulesDefinition.UPDATE_MODE_KEY, Boolean.TRUE.toString());
		
	}

}
