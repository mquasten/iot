package de.mq.iot.calendar.support;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import de.mq.iot.calendar.DayGroup;
import de.mq.iot.model.Subject;

@Configuration
class CalendarModelConfiguration {
	
	@Bean
	@Scope("prototype")
	CalendarModel calendarModel(final Subject<CalendarModel.Events, CalendarModel> subject, @Qualifier(value="dayGroups")  final Collection<DayGroup> dayGroups) {
		return new CalendarModelImpl(subject, dayGroups);

	}


}
