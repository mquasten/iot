package de.mq.iot.calendar.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Configuration
class CalendarRulesConfiguration {

	

	@Bean()
	@Scope(proxyMode = ScopedProxyMode.DEFAULT, value = "prototype")
	SpecialdaysRulesEngineBuilder rulesEngine() {
		return new SpecialdaysRulesEngineBuilder();
	
	}

}
