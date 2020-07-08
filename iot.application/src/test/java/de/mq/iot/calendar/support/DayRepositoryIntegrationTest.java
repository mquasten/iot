package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;
import de.mq.iot.calendar.SpecialdayService.DayType;
import de.mq.iot.support.ApplicationConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })
@Disabled
class DayRepositoryIntegrationTest {
	

	@Autowired
	private DayRepository dayRepository;
	@Autowired
	private DayService dayService;
	
	private final List<Day<?>> nonWorkingDays= new ArrayList<>(); 
	private final DayGroup dayGroup = new DayGroupImpl(DayType.NonWorkingDay.name(), 1);
	
	@BeforeEach
	void setup() {
		nonWorkingDays.clear();
		
		nonWorkingDays.add(new DayOfWeekImpl(dayGroup,DayOfWeek.SATURDAY ));
		nonWorkingDays.add(new DayOfWeekImpl(dayGroup,DayOfWeek.SUNDAY ));
		
		nonWorkingDays.add(new GaussDayImpl<>(dayGroup, -2));
		nonWorkingDays.add(new GaussDayImpl<>(dayGroup, 0));
		nonWorkingDays.add(new GaussDayImpl<>(dayGroup, 1));
		nonWorkingDays.add(new GaussDayImpl<>(dayGroup, 39));
		nonWorkingDays.add(new GaussDayImpl<>(dayGroup, 50));
		nonWorkingDays.add(new GaussDayImpl<>(dayGroup, 60));
		
		
		nonWorkingDays.add(new FixedDayImpl<>(dayGroup, MonthDay.of(1, 1)));
		
		nonWorkingDays.add(new FixedDayImpl<>(dayGroup, MonthDay.of(5,1)));
		nonWorkingDays.add(new FixedDayImpl<>(dayGroup,MonthDay.of(10,3)));
		nonWorkingDays.add(new FixedDayImpl<>(dayGroup,MonthDay.of(11,1)));
		nonWorkingDays.add(new FixedDayImpl<>(dayGroup,MonthDay.of(12,25)));
		nonWorkingDays.add(new FixedDayImpl<>(dayGroup,MonthDay.of(12,26))); 
		
	}

	@Test
	@Disabled
	void save() {
		
		IntStream.range(0, 1000).forEach(i -> nonWorkingDays.forEach(day -> dayRepository.save(day).block(Duration.ofMillis(500))));
		
	}
	
	@Test
	@Disabled
	void dayGoup() {
		assertEquals(DayType.NonWorkingDay.name(), dayService.dayGroup(LocalDate.of(2020, 5, 1)).name());
		assertEquals(DayType.WorkingDay.name(), dayService.dayGroup(LocalDate.of(2020, 6, 17)).name());
	}
	
	@Test
	@Disabled
	void findByDayGroupName() {
		Collection<Day<?>> results = dayRepository.findByDayGroupName(dayGroup.name()).collectList().block(Duration.ofMillis(500));
		assertEquals(14, results.size());
	}
	
	@Test
	@Disabled
	void test() {
		final Collection<Day<LocalDate>> results = dayService.newLocalDateDay(dayGroup, LocalDate.of(2020, 6, 27),  LocalDate.of(2020, 7, 12));
		//results.forEach(day -> System.out.println(day.value()));
		assertEquals(10, results.size());
	}
}
