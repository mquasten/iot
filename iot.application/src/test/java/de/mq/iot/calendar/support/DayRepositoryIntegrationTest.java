package de.mq.iot.calendar.support;

import java.time.DayOfWeek;
import java.time.Duration;

import java.time.MonthDay;
import java.util.ArrayList;
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
import de.mq.iot.support.ApplicationConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })
@Disabled
class DayRepositoryIntegrationTest {
	

	@Autowired
	private DayRepository dayRepository;
	
	private final List<Day<?>> nonWorkingDays= new ArrayList<>(); 
	private final DayGroup dayGroup = new DayGroupImpl("NonWorkingDay", 1);
	
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
	void save() {
		
		IntStream.range(0, 1000).forEach(i -> nonWorkingDays.forEach(day -> dayRepository.save(day).block(Duration.ofMillis(500))));
		
	}
	
	
}
