package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.Year;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.Specialday.Type;
import de.mq.iot.calendar.SpecialdayService;

import de.mq.iot.support.ApplicationConfiguration;
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })
@Disabled
public class SpecialdayServiceIntegrationTest {
	
	@Autowired
	private SpecialdayService specialdayService;

	
	@Test
	@Disabled
	void vacation() {
		final Collection<Specialday> days = specialdayService.vacationOrSpecialWorkingDates(LocalDate.of(2018, 8, 11), LocalDate.of(2018, 8, 19),false);
		
		assertEquals(5, days.size());
		
		days.forEach(specialday -> specialdayService.save(specialday));
		
		
		Collection<LocalDate> dates = days.stream().map(day -> day.date(2018)).collect(Collectors.toSet());
		
		final Collection<Specialday>  results = specialdayService.specialdays(Year.of(2018)).stream().filter(day ->  dates.contains(day.date(2018))).collect(Collectors.toList());
		
		
		assertEquals(5, results.size());
	
		
		assertEquals(dates,results.stream().map(day -> day.date(2018)).collect(Collectors.toSet()) );
		
	}


	@Test
	@Disabled
	void specialdays() {
		assertEquals(2, specialdayService.specialdays(Arrays.asList(Type.Weekend)).size());
		assertEquals(12, specialdayService.specialdays(Arrays.asList(Type.Gauss, Type.Fix)).size());
		specialdayService.specialdays(Arrays.asList(Type.Gauss, Type.Fix)).forEach(day -> System.out.println(day.date(Year.now().getValue())));
		
	}
	
}
