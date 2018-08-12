package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.Year;
import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.support.ApplicationConfiguration;
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })
@Disabled
public class SpecialdayServiceIntegrationTest {
	
	@Autowired
	private SpecialdayService specialdayService;
	
	@Test
	void vacation() {
		final Collection<Specialday> days = specialdayService.vacation(LocalDate.of(2018, 8, 11), LocalDate.of(2018, 8, 19));
		
		assertEquals(5, days.size());
		
		days.forEach(specialday -> specialdayService.save(specialday));
		
		
		Collection<LocalDate> dates = days.stream().map(day -> day.date(2018)).collect(Collectors.toSet());
		
		final Collection<Specialday>  results = specialdayService.specialdays(Year.of(2018)).stream().filter(day ->  dates.contains(day.date(2018))).collect(Collectors.toList());
		
		
		assertEquals(5, results.size());
	
		
		assertEquals(dates,results.stream().map(day -> day.date(2018)).collect(Collectors.toSet()) );
		
	}
	
}
