package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
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
import de.mq.iot.calendar.SpecialdayService.DayType;
import de.mq.iot.support.ApplicationConfiguration;
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })
@Disabled
public class SpecialdayServiceIntegrationTest {
	
	@Autowired
	private SpecialdayService specialdayService;
	@Autowired
	private SpecialdayRepository specialdayRepository;
	
	@Test
	@Disabled
	void vacation() {
		final Collection<Specialday> days = specialdayService.vacation(LocalDate.of(2018, 8, 11), LocalDate.of(2018, 8, 19));
		
		assertEquals(5, days.size());
		
		days.forEach(specialday -> specialdayService.save(specialday));
		
		
		Collection<LocalDate> dates = days.stream().map(day -> day.date(2018)).collect(Collectors.toSet());
		
		final Collection<Specialday>  results = specialdayService.specialdays(Year.of(2018)).stream().filter(day ->  dates.contains(day.date(2018))).collect(Collectors.toList());
		
		
		assertEquals(5, results.size());
	
		
		assertEquals(dates,results.stream().map(day -> day.date(2018)).collect(Collectors.toSet()) );
		
	}
	

	
	
	
	@Test
	@Disabled
	void typeOfDay() {
		assertEquals(DayOfWeek.SATURDAY, specialdayRepository.save(new SpecialdayImpl(DayOfWeek.SATURDAY,true)).block().dayOfWeek());
		assertEquals(DayOfWeek.SUNDAY, specialdayRepository.save(new SpecialdayImpl(DayOfWeek.SUNDAY,true)).block().dayOfWeek());
		assertEquals(DayOfWeek.MONDAY, specialdayRepository.save(new SpecialdayImpl(DayOfWeek.MONDAY,false)).block().dayOfWeek());
		
		assertEquals( LocalDate.of(1968, 5, 28) ,  specialdayRepository.save(new SpecialdayImpl(LocalDate.of(1968, 5, 28),true)).block().date(1));
		
		
		assertTrue(specialdayService.typeOfDay(LocalDate.of(1968, 5, 28)).getValue().startsWith(SpecialdayImpl.Type.SpecialWorkingDate.name()));
		
		assertTrue(specialdayService.typeOfDay(LocalDate.of(2019, 12, 25)).getValue().startsWith(SpecialdayServiceImpl.VACATION_OR_PUBLIC_HOLIDAY_INFO));
		assertEquals(DayType.WorkingDay, specialdayService.typeOfDay(LocalDate.of(2019, 12, 10)).getKey());
		
		assertTrue(specialdayService.typeOfDay(LocalDate.of(2019, 12, 9)).getValue().startsWith(SpecialdayImpl.Type.SpecialWorkingDay.name()));
	}
	@Disabled
	@Test
	void specialdays() {
		assertEquals(2, specialdayService.specialdays(Arrays.asList(Type.Weekend)).size());
		assertEquals(12, specialdayService.specialdays(Arrays.asList(Type.Gauss, Type.Fix)).size());
	}
	
}
