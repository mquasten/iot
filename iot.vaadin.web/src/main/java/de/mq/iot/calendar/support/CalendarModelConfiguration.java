package de.mq.iot.calendar.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import de.mq.iot.model.Subject;

@Configuration
class CalendarModelConfiguration {
	
	@Bean
	@Scope("prototype")
	CalendarModel calendarModel(final Subject<CalendarModel.Events, CalendarModel> subject) {
		return new CalendarModelImpl(subject);

	}


}
