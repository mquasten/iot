package de.mq.iot.rule.support;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import de.mq.iot.openweather.MeteorologicalData;
import de.mq.iot.openweather.MeteorologicalDataService;
import de.mq.iot.rule.support.Calendar.Time;
@Rule(name = "temperatureRule", priority=2)
public class TemperatureRuleImpl {
	
	private final MeteorologicalDataService meteorologicalDataService;

	TemperatureRuleImpl(MeteorologicalDataService meteorologicalDataService) {
		this.meteorologicalDataService = meteorologicalDataService;
	}
	
	
	
	@Condition
	public  boolean evaluate(@Fact(RulesAggregate.RULE_CALENDAR) final Calendar calendar) {
		return calendar.valid()&&calendar.time()==Time.Summer;
	}


	@Action
	public void forecast(@Fact(RulesAggregate.RULE_CALENDAR) final Calendar calendar) {
		final MeteorologicalData meteorologicalData = meteorologicalDataService.forecastMaxTemperature(calendar.date());
		calendar.assignTemperature(meteorologicalData.temperature());
	}
	
}
