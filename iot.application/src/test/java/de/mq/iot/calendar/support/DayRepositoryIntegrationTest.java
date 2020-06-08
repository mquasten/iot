package de.mq.iot.calendar.support;

import java.time.Duration;

import java.time.MonthDay;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mq.iot.calendar.DayGroup;
import de.mq.iot.support.ApplicationConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })
@Disabled
class DayRepositoryIntegrationTest {
	

	@Autowired
	private DayRepository dayRepository;

	@Test
	void save() {
		
		final DayGroup dayGroup = new DayGroupImpl("NonWorkingDay", 1);
		IntStream.range(0, 100).forEach(i -> {
		dayRepository.save(new FixedDayImpl<>(dayGroup, MonthDay.of(5, 1))).block(Duration.ofMillis(500));
		});
	}
	
	
}
