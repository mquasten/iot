package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.calendar.support.SpecialdayImpl.Type;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class SpecialdayServiceTest {

	private static final int TIMEOUT = 500;

	private final SpecialdayRepository specialdayRepository = Mockito.mock(SpecialdayRepository.class);

	private final SpecialdayService specialdayService = new SpecialdayServiceImpl(specialdayRepository, TIMEOUT);

	private final Specialday specialday = Mockito.mock(Specialday.class);
	private final Specialday otherSpecialday = Mockito.mock(Specialday.class);

	@SuppressWarnings("unchecked")
	private final Mono<Specialday> mono = Mockito.mock(Mono.class);

	private final Flux<Specialday> fluxHoliday = Flux.fromStream(Arrays.asList(specialday).stream());

	@SuppressWarnings("unchecked")
	private final Mono<List<Specialday>> collectListHoliday = Mockito.mock(Mono.class);

	private final Flux<Specialday> fluxVacation = Flux.fromStream(Arrays.asList(otherSpecialday).stream());
	@SuppressWarnings("unchecked")
	private final Mono<List<Specialday>> collectListVacation = Mockito.mock(Mono.class);

	private Year year = Year.of(LocalDate.now().getYear());

	@BeforeEach
	void setup() {

		Mockito.when(specialdayRepository.save(specialday)).thenReturn(mono);

		Mockito.when(specialdayRepository.findByTypeIn(Arrays.asList(Type.Fix, Type.Gauss))).thenReturn(fluxHoliday);

		Mockito.when(collectListHoliday.block(Duration.ofMillis(TIMEOUT))).thenReturn(Arrays.asList(specialday));

		Mockito.when(specialdayRepository.findByTypeAndYear(Type.Vacation, year.getValue())).thenReturn(fluxVacation);
		Mockito.when(collectListVacation.block(Duration.ofMillis(TIMEOUT))).thenReturn(Arrays.asList(otherSpecialday));

	}

	@Test
	void save() {
		specialdayService.save(specialday);
		Mockito.verify(specialdayRepository).save(specialday);
		Mockito.verify(mono).block(Duration.ofMillis(TIMEOUT));
	}

	@Test
	void specialdays() {
		final List<Specialday> results = new ArrayList<>(specialdayService.specialdays(year));

		assertEquals(2, results.size());
		assertEquals(specialday, results.get(0));
		assertEquals(otherSpecialday, results.get(1));
	}

	@Test
	void specialdaysAll() {
		Mockito.doReturn(Flux.just(specialday, otherSpecialday)).when(specialdayRepository).findByTypeIn(Arrays.asList(Type.values()));

		Collection<Specialday> specialdays = specialdayService.specialdays();

		assertEquals(2, specialdays.size());
		assertTrue(specialdays.contains(specialday));
		assertTrue(specialdays.contains(otherSpecialday));
	}

	@Test
	void vacation() {
		final Flux<Specialday> fluxHoliday = Flux.fromStream(Arrays.asList(new SpecialdayImpl(MonthDay.of(12, 25)), new SpecialdayImpl(MonthDay.of(12, 26)), new SpecialdayImpl(MonthDay.of(1, 1))).stream());
		Mockito.when(specialdayRepository.findByTypeIn(Arrays.asList(Type.Fix, Type.Gauss))).thenReturn(fluxHoliday);
		final LocalDate begin = LocalDate.of(2018, 12, 17);
		final LocalDate end = LocalDate.of(2019, 1, 6);
		final Collection<Specialday> results = specialdayService.vacation(begin, end);

		final Collection<LocalDate> expectedDates = expectedDatesVacation(begin);

		assertEquals(expectedDates.size(), results.size());
		assertEquals(expectedDates, results.stream().map(specialday -> specialday.date(2018)).collect(Collectors.toList()));
	}

	private Collection<LocalDate> expectedDatesVacation(final LocalDate begin) {
		final Collection<LocalDate> expectedDates = LongStream.range(0, 20).mapToObj(i -> begin.plusDays(i)).collect(Collectors.toList());

		expectedDates.remove(LocalDate.of(2018, 12, 25));
		expectedDates.remove(LocalDate.of(2018, 12, 26));
		expectedDates.remove(LocalDate.of(2019, 1, 1));

		expectedDates.remove(LocalDate.of(2018, 12, 22));
		expectedDates.remove(LocalDate.of(2018, 12, 23));
		expectedDates.remove(LocalDate.of(2018, 12, 29));
		expectedDates.remove(LocalDate.of(2018, 12, 30));
		expectedDates.remove(LocalDate.of(2019, 1, 5));
		expectedDates.remove(LocalDate.of(2019, 1, 6));
		return expectedDates;
	}

	@Test
	void vacationOnlyOneYear() {

		final Flux<Specialday> fluxHoliday = Flux.fromStream(Arrays.asList(new SpecialdayImpl(MonthDay.of(12, 25)), new SpecialdayImpl(MonthDay.of(12, 26)), specialday).stream());
		Mockito.when(specialdayRepository.findByTypeIn(Arrays.asList(Type.Fix, Type.Gauss))).thenReturn(fluxHoliday);
		final LocalDate begin = LocalDate.of(2018, 12, 17);
		final LocalDate end = LocalDate.of(2018, 12, 31);
		final Collection<Specialday> results = specialdayService.vacation(begin, end);

		final Collection<LocalDate> expectedDates = LongStream.range(0, 15).mapToObj(i -> begin.plusDays(i)).collect(Collectors.toList());
		expectedDates.remove(LocalDate.of(2018, 12, 25));
		expectedDates.remove(LocalDate.of(2018, 12, 26));

		expectedDates.remove(LocalDate.of(2018, 12, 22));
		expectedDates.remove(LocalDate.of(2018, 12, 23));
		expectedDates.remove(LocalDate.of(2018, 12, 29));
		expectedDates.remove(LocalDate.of(2018, 12, 30));

		assertEquals(expectedDates.size(), results.size());
		assertEquals(expectedDates, results.stream().map(specialday -> specialday.date(2018)).collect(Collectors.toList()));

		Mockito.verify(specialday, Mockito.atLeastOnce()).date(2018);
		Mockito.verify(specialday, Mockito.never()).date(2019);
	}

	@Test
	void vacationSingleDay() {
		final LocalDate date = LocalDate.of(2018, 12, 17);

		final Collection<Specialday> results = specialdayService.vacation(date, date);

		assertEquals(1, results.size());
		assertEquals(date, results.stream().findFirst().get().date(2018));
	}

	@Test
	void vacationEmpty() {
		final Flux<Specialday> fluxHoliday = Flux.fromStream(Arrays.asList(new SpecialdayImpl(MonthDay.of(12, 25)), new SpecialdayImpl(MonthDay.of(12, 26))).stream());
		Mockito.when(specialdayRepository.findByTypeIn(Arrays.asList(Type.Fix, Type.Gauss))).thenReturn(fluxHoliday);
		final LocalDate begin = LocalDate.of(2018, 12, 25);
		final LocalDate end = LocalDate.of(2018, 12, 26);
		final Collection<Specialday> results = specialdayService.vacation(begin, end);

		assertTrue(results.isEmpty());
	}

	@Test
	void vacationEndBeforeBegin() {
		assertThrows(IllegalArgumentException.class, () -> specialdayService.vacation(LocalDate.of(2018, 12, 31), LocalDate.of(2018, 12, 17)));
	}

	@Test
	void delete() {

		Mockito.doReturn(mono).when(specialdayRepository).delete(specialday);

		specialdayService.delete(specialday);

		Mockito.verify(specialdayRepository).delete(specialday);
		Mockito.verify(mono).block(Duration.ofMillis(TIMEOUT));
	}

}
