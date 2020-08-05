package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class DayServiceTest {

	static final int TIMEOUT = 500;
	private final DayRepository dayRepository = Mockito.mock(DayRepository.class);
	private final DayGroup defaultDayGroup = new DayConfiguration().defaultDayGroup();

	private final DayGroup nonWorkingDayGroup = new DayGroupImpl(DayGroup.NON_WORKINGDAY_GROUP_NAME, 1);
	private final DayGroup specialWorkinDayGroup = new DayGroupImpl(DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME, 0);

	private final DayService dayService = new DayServiceImpl(dayRepository, defaultDayGroup, TIMEOUT);
	
	@SuppressWarnings("unchecked")
	private final Mono<Day<?>> mono = Mockito.mock(Mono.class);

	private static final LocalDate DATE = LocalDate.of(2020, 5, 28);

	@Test
	void dayGroup() {

		Mockito.when(dayRepository.findAll()).thenReturn(Flux.just(new LocalDateDayImpl(nonWorkingDayGroup, DATE),
				new LocalDateDayImpl(specialWorkinDayGroup, DATE)));

		assertEquals(specialWorkinDayGroup, dayService.dayGroup(DATE));
	}

	@Test
	void dayGroupFilter() {
		Mockito.when(dayRepository.findAll())
				.thenReturn(Flux.just(new LocalDateDayImpl(specialWorkinDayGroup, DATE.minusDays(1)),
						new LocalDateDayImpl(nonWorkingDayGroup, DATE)));
		assertEquals(nonWorkingDayGroup, dayService.dayGroup(DATE));
	}

	@Test
	void dayGroupFilterDefault() {
		Mockito.when(dayRepository.findAll()).thenReturn(Flux.empty());
		assertEquals(defaultDayGroup, dayService.dayGroup(DATE));
	}

	@Test
	void localDateDaysBeforeOrEquals() {
		final Day<?> day = new LocalDateDayImpl(specialWorkinDayGroup, DATE);
		Mockito.when(dayRepository.findAll()).thenReturn(Flux.just(day, new FixedDayImpl(nonWorkingDayGroup, MonthDay.of(DATE.getMonth(), DATE.getDayOfMonth()))));

		final Collection<Day<LocalDate>> results = dayService.localDateDaysBeforeOrEquals(DATE);
		assertEquals(1, results.size());
		assertEquals(day, results.stream().findAny().get());
		
		assertTrue(dayService.localDateDaysBeforeOrEquals(DATE.minusDays(1)).isEmpty());
	}
	
	@Test
	void save() {
		final Day<?> day =  Mockito.mock(Day.class);
		Mockito.doReturn(mono).when(dayRepository).save(day);
		
		dayService.save(day);
		
		Mockito.verify(mono).block(Duration.ofMillis(TIMEOUT));
	}
	
	@Test
	void delete() {
		final Day<?> day =  Mockito.mock(Day.class);
		Mockito.doReturn(mono).when(dayRepository).delete(day);
		
		dayService.delete(day);
		
		Mockito.verify(mono).block(Duration.ofMillis(TIMEOUT));
	}
	
	@Test
	void newLocalDateDay() {
		Mockito.when(dayRepository.findAll()).thenReturn(Flux.just(new DayOfWeekImpl(nonWorkingDayGroup, DayOfWeek.SATURDAY) , new DayOfWeekImpl(nonWorkingDayGroup, DayOfWeek.SUNDAY) ));
		final Collection<Day<LocalDate>> results =dayService.newLocalDateDay(nonWorkingDayGroup, LocalDate.of(2020, 6, 27), LocalDate.of(2020, 7, 12));
		assertEquals(10, results.size());
		final Collection<LocalDate> expected = new ArrayList<>();
		expected.addAll( days(YearMonth.of(2020, 6), 29,30));
		expected.addAll( days(YearMonth.of(2020, 7), 1,3));
		expected.addAll( days(YearMonth.of(2020, 7), 6,10));
		
		assertEquals(expected, results.stream().map(day -> day.value()).collect(Collectors.toList()));
	}
	
	@Test
	void newLocalDateDayDifferentGroups() {
		Mockito.when(dayRepository.findAll()).thenReturn(Flux.just(new DayOfWeekImpl(specialWorkinDayGroup, DayOfWeek.SATURDAY) , new DayOfWeekImpl(specialWorkinDayGroup, DayOfWeek.SUNDAY) ));
		final Collection<Day<LocalDate>> results =dayService.newLocalDateDay(nonWorkingDayGroup, LocalDate.of(2020, 6, 27), LocalDate.of(2020, 7, 12));
		assertEquals(16, results.size());
		final Collection<LocalDate> expected = new ArrayList<>();
		
		expected.addAll( days(YearMonth.of(2020, 6), 27,30));
		expected.addAll( days(YearMonth.of(2020, 7), 1,12));
		
		
		assertEquals(expected, results.stream().map(day -> day.value()).collect(Collectors.toList()));
	}
	
	@Test
	void newLocalDateDayWrongDates() {
		assertThrows(IllegalArgumentException.class, () -> dayService.newLocalDateDay(nonWorkingDayGroup, LocalDate.of(2020, 7, 12), LocalDate.of(2020, 6, 27)));
	}


	private List<LocalDate> days(final YearMonth yearMonth, final int start, final int stop  ) {
		
		return IntStream.rangeClosed(start, stop).mapToObj(i -> LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), i)).collect(Collectors.toList());
	}
	
	@Test
	void days() {
		
		final Day<?>[] days = new Day<?>[] { new LocalDateDayImpl(nonWorkingDayGroup, DATE), new LocalDateDayImpl(specialWorkinDayGroup, DATE)};
		Mockito.when(dayRepository.findAll()).thenReturn(Flux.just(days));
		
	
		assertEquals(Arrays.asList(days), dayService.days());
	}
}
