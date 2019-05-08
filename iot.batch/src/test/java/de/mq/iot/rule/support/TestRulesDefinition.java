package de.mq.iot.rule.support;

import de.mq.iot.rule.RulesDefinition;

public interface TestRulesDefinition {
	
	static final String MIN_SUN_DOWN_TIME = "17:15";
	static final String HOLIDAY_ALARM_TIME = "7:15";
	static final String WORKINGDAY_ALARM_TIME = "5:15";
	static final String TEMPERATURE_RULE = "temperatureRule";

	static RulesDefinition rulesDefinition() {
		final RulesDefinition rulesDefinition = new RulesDefinitionImpl(RulesDefinition.Id.DefaultDailyIotBatch);
		rulesDefinition.assign(RulesDefinitionImpl.WORKINGDAY_ALARM_TIME_KEY, WORKINGDAY_ALARM_TIME);
		rulesDefinition.assign(RulesDefinitionImpl.HOLIDAY_ALARM_TIME_KEY, HOLIDAY_ALARM_TIME);
		rulesDefinition.assign(RulesDefinitionImpl.MIN_SUN_DOWN_TIME_KEY, MIN_SUN_DOWN_TIME);
		
		rulesDefinition.assignRule(TEMPERATURE_RULE);
	
		return rulesDefinition;
	}

}
