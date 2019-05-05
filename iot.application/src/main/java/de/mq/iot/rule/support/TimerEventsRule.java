package de.mq.iot.rule.support;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import de.mq.iot.rule.support.TimerEventsBuilder.Key;
import de.mq.iot.support.SunDownCalculationService;

@Rule(name = "timerEventsRule", priority=2)
public class TimerEventsRule {
	
	private final SunDownCalculationService sunDownCalculationService;
	
	TimerEventsRule(SunDownCalculationService sunDownCalculationService) {
		this.sunDownCalculationService = sunDownCalculationService;
	}

	@Condition
	public  boolean evaluate(@Fact(RulesAggregate.RULE_CALENDAR) final Calendar calendar) {
		return calendar.valid();
	}

	
	@Action
	public void calculateEvents(@Fact(RulesAggregate.RULE_CALENDAR) final Calendar calendar,   @Fact(RulesAggregate.RULE_INPUT) DefaultRuleInput ruleInput) {
		
		final LocalTime alarmTime = calendar.workingDay() ?  ruleInput.workingdayAlarmTime() : ruleInput.holidayAlarmTime();
		
		final LocalTime uptime = sunDownCalculationService.sunUpTime(calendar.dayOfYear(), calendar.time().offset());
		
		final LocalTime downTime = sunDownCalculationService.sunDownTime(calendar.dayOfYear(), calendar.time().offset());
		
	
		
		final String builder = TimerEventsBuilder.newBuilder().with(ruleInput.isUpdateMode()).with(Key.T0, alarmTime).with(Key.T1,Collections.max(Arrays.asList(alarmTime,uptime)) ).with(Key.T6, Collections.max(Arrays.asList(downTime,ruleInput.minSunDownTime()))).build();
		calendar.assignEvents(ruleInput.isUpdateMode()?SystemVariablesRuleImpl.EVENT_EXECUTIONS:SystemVariablesRuleImpl.DAILY_EVENTS, builder);
		
	}
	
	
		
}
