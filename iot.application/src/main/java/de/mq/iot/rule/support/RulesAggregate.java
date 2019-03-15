package de.mq.iot.rule.support;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;

public interface RulesAggregate {
	enum Type {
		DefaultDailyIotBtach
	}
	
	Type type();

	Rules rules();
	
	Facts facts();
}
