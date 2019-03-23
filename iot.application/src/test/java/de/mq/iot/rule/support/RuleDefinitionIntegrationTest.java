package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mq.iot.rule.RulesDefinition;
import de.mq.iot.support.ApplicationConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })
@Disabled
class RuleDefinitionIntegrationTest {
	
	private static final Duration DURATION = Duration.ofMillis(2000);
	@Autowired
	private RulesDefinitionRepository rulesDefinitionRepository;
	
	@Test
	@Disabled
	void save() {
		final RulesDefinition rulesDefinition = new RulesDefinitionImpl(RulesDefinition.Id.DefaultDailyIotBatch);
		rulesDefinition.assign(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY, "5:15");
		rulesDefinition.assign(RulesDefinition.HOLIDAY_ALARM_TIME_KEY, "7:15");
		rulesDefinition.assignRule("optionalRule");
		
		
		rulesDefinitionRepository.save(rulesDefinition).block(DURATION);
		
		
		assertEquals(RulesDefinition.Id.DefaultDailyIotBatch, rulesDefinitionRepository.findById(RulesDefinition.Id.DefaultDailyIotBatch).block(DURATION).id());
	}

}
