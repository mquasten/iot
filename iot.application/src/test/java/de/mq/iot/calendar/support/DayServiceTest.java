package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.calendar.DayGroup;
import de.mq.iot.calendar.SpecialdayService.DayType;
import reactor.core.publisher.Flux;

class DayServiceTest {
	
	static final int TIMEOUT = 500;
	private final DayRepository dayRepository = Mockito.mock(DayRepository.class);
	private final DayGroup defaultDayGroup = new DayConfiguration().defaultDayGroup();
	
	private final DayGroup nonWorkingDayGroup = new DayGroupImpl(DayType.NonWorkingDay.name(), 1);
	private final DayGroup specialWorkinDayGroup = new DayGroupImpl(DayType.SpecialWorkingDay.name(), 0);
	
	private final DayService dayService = new DayServiceImpl(dayRepository, defaultDayGroup , TIMEOUT);
	
	private static final LocalDate DATE = LocalDate.of(2020, 5, 28);
	@Test
	void dayGroup() {
		
		Mockito.when(dayRepository.findAll()).thenReturn(Flux.just(new LocalDateDayImpl(nonWorkingDayGroup, DATE),  new LocalDateDayImpl(specialWorkinDayGroup, DATE)));
	
		assertEquals(specialWorkinDayGroup, dayService.dayGroup(DATE));	
	}
	@Test
	void dayGroupFilter() {
		Mockito.when(dayRepository.findAll()).thenReturn(Flux.just(new LocalDateDayImpl(specialWorkinDayGroup, DATE.minusDays(1)),  new LocalDateDayImpl(nonWorkingDayGroup, DATE)));
		assertEquals(nonWorkingDayGroup, dayService.dayGroup(DATE));	
	}
	@Test
	void dayGroupFilterDefault() {
		Mockito.when(dayRepository.findAll()).thenReturn(Flux.empty());
		assertEquals(defaultDayGroup, dayService.dayGroup(DATE));
	}

}
