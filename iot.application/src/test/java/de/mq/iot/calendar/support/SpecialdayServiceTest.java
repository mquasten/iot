package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.jeasy.rules.api.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.Specialday.Type;
import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.calendar.SpecialdayService.DayType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class SpecialdayServiceTest {

	private static final int TIMEOUT = 500;

	private final SpecialdayRepository specialdayRepository = Mockito.mock(SpecialdayRepository.class);
	
	private final Collection<Rule> rules = new ArrayList<>();
	private  SpecialdayService specialdayService;

	private final Specialday specialday = Mockito.mock(Specialday.class);
	private final Specialday otherSpecialday = Mockito.mock(Specialday.class);
	
	private final Specialday weekendSpecialday = Mockito.mock(Specialday.class);
	private final Specialday specialWorkingday = Mockito.mock(Specialday.class);

	@SuppressWarnings("unchecked")
	private final Mono<Specialday> mono = Mockito.mock(Mono.class);

	private final Flux<Specialday> fluxHoliday =  Flux.fromArray(new Specialday[] {specialday});
	
	private final Flux<Specialday> fluxWeekend = Flux.fromArray(new Specialday[] {weekendSpecialday});
	
	private final Flux<Specialday> fluxSpecialWorkingDay = Flux.fromArray(new Specialday[] {specialWorkingday});

	@SuppressWarnings("unchecked")
	private final Mono<List<Specialday>> collectListHoliday = Mockito.mock(Mono.class);

	private final Flux<Specialday> fluxVacation = Flux.fromStream(Arrays.asList(otherSpecialday).stream());
	@SuppressWarnings("unchecked")
	private final Mono<List<Specialday>> collectListVacation = Mockito.mock(Mono.class);

	private Year year = Year.of(LocalDate.now().getYear());

	@BeforeEach
	void setup() {
		rules.add(new VacationOrHolidayRuleImpl());
		rules.add(new WeekendRuleImpl());
		rules.add(new SpecialWorkingDateRuleImpl());
		rules.add(new SpecialWorkingDayRuleImpl());
		rules.add(new WorkingdayRuleImpl());
		specialdayService = new SpecialdayServiceImpl(specialdayRepository,rules, TIMEOUT);
		Mockito.when(specialdayRepository.save(specialday)).thenReturn(mono);

		Mockito.when(specialdayRepository.findByTypeIn(Arrays.asList(Type.Fix, Type.Gauss))).thenReturn(fluxHoliday);

		Mockito.when(collectListHoliday.block(Duration.ofMillis(TIMEOUT))).thenReturn(Arrays.asList(specialday));

		Mockito.when(specialdayRepository.findByTypeAndYear(Type.Vacation, year.getValue())).thenReturn(fluxVacation);
		Mockito.when(collectListVacation.block(Duration.ofMillis(TIMEOUT))).thenReturn(Arrays.asList(otherSpecialday));
		
		Mockito.when(specialdayRepository.findByTypeIn(Arrays.asList(Type.Weekend))).thenReturn(fluxWeekend);
		
		Mockito.when(specialdayRepository.findByTypeIn(Arrays.asList(Type.SpecialWorkingDay))).thenReturn(fluxSpecialWorkingDay);
		Mockito.when(specialdayRepository.findByTypeIn(Arrays.asList(Type.SpecialWorkingDate))).thenReturn(fluxSpecialWorkingDay);

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
	void typeOfDayWeekend() {
		Mockito.doReturn(DayOfWeek.SATURDAY).when(weekendSpecialday).dayOfWeek();
		final Entry<DayType, String> typeOfDay = specialdayService.typeOfDay(LocalDate.of(2019, 12, 7));
		
		assertEquals(DayType.NonWorkingDay,typeOfDay.getKey());
		assertEquals(String.format(SpecialdayServiceImpl.DAY_TYPE_INFO_FORMAT, Type.Weekend, DayOfWeek.SATURDAY), typeOfDay.getValue());
	}
	
	@Test
	void typeOfDayVacation() {
		LocalDate date = LocalDate.of(Year.now().getValue(), 12, 25);
		Mockito.doReturn(date).when(specialday).date(Year.now().getValue());
		final Entry<DayType, String> typeOfDay = specialdayService.typeOfDay(date);
		
		assertEquals(DayType.NonWorkingDay, typeOfDay.getKey());
		assertEquals(String.format(SpecialdayServiceImpl.DAY_TYPE_INFO_FORMAT,SpecialdayServiceImpl.VACATION_OR_PUBLIC_HOLIDAY_INFO,date), typeOfDay.getValue());
		
		
	}
	
	
	@Test
	void typeOfDaySpecialWorkingDay() {
		final int year=2019; 
		Mockito.doReturn(DayOfWeek.MONDAY).when(specialWorkingday).dayOfWeek();
		Mockito.when(specialdayRepository.findByTypeAndYear(Type.Vacation, year)).thenReturn(fluxVacation);
		final Entry<DayType, String> typeOfDay = specialdayService.typeOfDay(LocalDate.of(year, 12,9));
		assertEquals(DayType.SpecialWorkingDay, typeOfDay.getKey());
		assertEquals(String.format(SpecialdayServiceImpl.DAY_TYPE_INFO_FORMAT,DayType.SpecialWorkingDay, DayOfWeek.MONDAY), typeOfDay.getValue());
	}
	
	@Test
	void typeOfDaySpecialWorkingDate() {
		final LocalDate date = LocalDate.of(2019, 12,9);
		Mockito.doReturn(date).when(specialWorkingday).date(Mockito.anyInt());
		Mockito.when(specialdayRepository.findByTypeAndYear(Type.Vacation, date.getYear())).thenReturn(fluxVacation);
		final Entry<DayType, String> typeOfDay = specialdayService.typeOfDay(date);
		
		assertEquals(DayType.SpecialWorkingDay, typeOfDay.getKey());
		assertEquals(String.format(SpecialdayServiceImpl.DAY_TYPE_INFO_FORMAT,Type.SpecialWorkingDate,date), typeOfDay.getValue());
		
	}
	
	@Test
	void typeOfDayNormalWorkingDay() {
		final int year = 2019;
		Mockito.when(specialdayRepository.findByTypeAndYear(Type.Vacation, year)).thenReturn(fluxVacation);
		final Entry<DayType, String> typeOfDay = specialdayService.typeOfDay( LocalDate.of(year, 12,11));
		assertEquals(DayType.WorkingDay, typeOfDay.getKey());
		assertEquals(DayType.WorkingDay.name(), typeOfDay.getValue());
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
	void  vacationOrSpecialWorkingDate() {
		final Flux<Specialday> fluxHoliday = Flux.fromStream(Arrays.asList(new SpecialdayImpl(MonthDay.of(12, 25)), new SpecialdayImpl(MonthDay.of(12, 26)), new SpecialdayImpl(MonthDay.of(1, 1))).stream());
		Mockito.when(specialdayRepository.findByTypeIn(Arrays.asList(Type.Fix, Type.Gauss))).thenReturn(fluxHoliday);
		final LocalDate begin = LocalDate.of(2018, 12, 17);
		final LocalDate end = LocalDate.of(2019, 1, 6);
		final Collection<Specialday> results = specialdayService.vacationOrSpecialWorkingDates(begin, end, false);

		final Collection<LocalDate> expectedDates = expectedDatesVacation(begin);

		assertEquals(expectedDates.size(), results.size());
		assertEquals(expectedDates, results.stream().map(specialday -> specialday.date(2018)).collect(Collectors.toList()));
		
		results.stream().map( specialday -> specialday.type()).forEach(type -> assertEquals(Type.Vacation, type));
		
	}
	
	@Test
	void vacationOrSpecialWorkingDateSpecialWorkingDate() {
		final Flux<Specialday> fluxHoliday = Flux.fromStream(Arrays.asList(new SpecialdayImpl(MonthDay.of(12, 25)), new SpecialdayImpl(MonthDay.of(12, 26)), new SpecialdayImpl(MonthDay.of(1, 1))).stream());
		Mockito.when(specialdayRepository.findByTypeIn(Arrays.asList(Type.Fix, Type.Gauss))).thenReturn(fluxHoliday);
		final LocalDate begin = LocalDate.of(2018, 12, 17);
		final LocalDate end = LocalDate.of(2019, 1, 6);
		final Collection<Specialday> results = specialdayService.vacationOrSpecialWorkingDates(begin, end, true);

		final Collection<LocalDate> expectedDates = expectedDatesVacation(begin);

		assertEquals(expectedDates.size(), results.size());
		assertEquals(expectedDates, results.stream().map(specialday -> specialday.date(2018)).collect(Collectors.toList()));
		
		results.stream().map( specialday -> specialday.type()).forEach(type -> assertEquals(Type.SpecialWorkingDate,type));
		
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
	void  vacationOrSpecialWorkingDateOnlyOneYear() {

		final Flux<Specialday> fluxHoliday = Flux.fromStream(Arrays.asList(new SpecialdayImpl(MonthDay.of(12, 25)), new SpecialdayImpl(MonthDay.of(12, 26)), specialday).stream());
		Mockito.when(specialdayRepository.findByTypeIn(Arrays.asList(Type.Fix, Type.Gauss))).thenReturn(fluxHoliday);
		final LocalDate begin = LocalDate.of(2018, 12, 17);
		final LocalDate end = LocalDate.of(2018, 12, 31);
		final Collection<Specialday> results = specialdayService.vacationOrSpecialWorkingDates(begin, end, false);

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
	void  vacationOrSpecialWorkingDateSingleDay() {
		final LocalDate date = LocalDate.of(2018, 12, 17);

		final Collection<Specialday> results = specialdayService.vacationOrSpecialWorkingDates(date, date, false);

		assertEquals(1, results.size());
		assertEquals(date, results.stream().findFirst().get().date(2018));
	}

	@Test
	void  vacationOrSpecialWorkingDateEmpty() {
		final Flux<Specialday> fluxHoliday = Flux.fromStream(Arrays.asList(new SpecialdayImpl(MonthDay.of(12, 25)), new SpecialdayImpl(MonthDay.of(12, 26))).stream());
		Mockito.when(specialdayRepository.findByTypeIn(Arrays.asList(Type.Fix, Type.Gauss))).thenReturn(fluxHoliday);
		final LocalDate begin = LocalDate.of(2018, 12, 25);
		final LocalDate end = LocalDate.of(2018, 12, 26);
		final Collection<Specialday> results = specialdayService.vacationOrSpecialWorkingDates(begin, end, false);

		assertTrue(results.isEmpty());
	}

	@Test
	void  vacationOrSpecialWorkingDateEndBeforeBegin() {
		assertThrows(IllegalArgumentException.class, () -> specialdayService.vacationOrSpecialWorkingDates(LocalDate.of(2018, 12, 31), LocalDate.of(2018, 12, 17), false));
	}

	@Test
	void delete() {

		Mockito.doReturn(mono).when(specialdayRepository).delete(specialday);

		specialdayService.delete(specialday);

		Mockito.verify(specialdayRepository).delete(specialday);
		Mockito.verify(mono).block(Duration.ofMillis(TIMEOUT));
	}

	@Test
	void vacationsBeforeEquals() {
		final LocalDate minDate = LocalDate.now().minusDays(30);
		final Specialday first = new SpecialdayImpl(LocalDate.now());
		final Specialday second =new SpecialdayImpl(minDate);
		
		final Specialday third =new SpecialdayImpl(LocalDate.now().minusDays(400));
		final Flux<Specialday> flux = Flux.fromStream(Arrays.asList(first,second,third).stream());
		Mockito.when(specialdayRepository.findByTypeIn(Arrays.asList(Type.Vacation, Type.SpecialWorkingDate))).thenReturn(flux);
		
		final Collection<Specialday> results = specialdayService.vacationsOrSpecialWorkingDatesBeforeEquals(minDate);
		assertEquals(2, results.size());
		
		assertTrue(results.contains(second));
		assertTrue(results.contains(third));
		
		assertFalse(results.contains(first));
	}
	
	@Test
	void specialdaysWithoutYear() {
		Mockito.when(specialdayRepository.findByTypeIn(Arrays.asList(Type.Weekend))).thenReturn(fluxWeekend);
		final Collection<Specialday> results = specialdayService.specialdays(Arrays.asList(Type.Weekend));
		assertEquals(1, results.size());
		assertEquals(weekendSpecialday, results.stream().findFirst().get());
	}
	
	@Test
	void specialdaysWithYear() {
		Mockito.when(specialdayRepository.findByTypeAndYear(Type.Vacation, Year.now().getValue())).thenReturn(fluxVacation);
		
		final Collection<Specialday> results = specialdayService.specialdays(Arrays.asList(Type.Vacation));
		assertEquals(1, results.size());
		assertEquals(otherSpecialday, results.stream().findFirst().get());
	}
	
	@Test
	void specialdaysWithoutYearSort() {
		final Specialday sunday = new SpecialdayImpl(DayOfWeek.SUNDAY,true);
		final Specialday saturday = new SpecialdayImpl(DayOfWeek.SATURDAY,true);
	
		Mockito.when(specialdayRepository.findByTypeIn(Arrays.asList(Type.Weekend))).thenReturn(Flux.fromArray(new Specialday[] {sunday, saturday}));
	
		
		final Collection<Specialday> results = specialdayService.specialdays(Arrays.asList(Type.Weekend));
		assertEquals(2, results.size());
		assertEquals(DayOfWeek.SATURDAY, results.stream().findAny().get().date(1).getDayOfWeek());
		
	}
	
	@Test
	void specialdaysWithYearSort() {
		
		final Specialday christmas2 = new  SpecialdayImpl(LocalDate.of(Year.now().getValue(), 12, 26));
		final Specialday christmas1 = new  SpecialdayImpl(LocalDate.of(Year.now().getValue(), 12, 25));
		Mockito.when(specialdayRepository.findByTypeAndYear(Type.Vacation, Year.now().getValue())).thenReturn(Flux.fromArray(new Specialday[] {christmas2, christmas1}));
		
		final Collection<Specialday> results = specialdayService.specialdays(Arrays.asList(Type.Vacation));
		
		assertEquals(2, results.size());
		
		assertEquals(christmas1, results.stream().findAny().get());
	}
	
	@Test
	void specialdaysWithoutInput() {
		assertEquals(0, specialdayService.specialdays(Arrays.asList()).size());
		assertEquals(0, specialdayService.specialdays((Collection<Type>)null).size());
	}
	
	@Test
	void specialdaysRulesEngineResultGauss() {
		prepareRulesEngine();
		final LocalDate date = LocalDate.of(2020, 4, 12);
		final SpecialdaysRulesEngineResult  result =specialdayService.specialdaysRulesEngineResult(date);
		assertEquals(DayType.NonWorkingDay, result.dayType());
		assertEquals(String.format(AbstractSpecialdaysRule.DAY_TYPE_INFO_FORMAT, Type.Gauss, date ), result.description());
	}

	private void prepareRulesEngine() {
		final Flux<Specialday> specialdays =  Flux.fromArray(new Specialday[] {new SpecialdayImpl(0), new SpecialdayImpl(MonthDay.of(Month.MAY, 1)), new SpecialdayImpl(DayOfWeek.SATURDAY, true), new SpecialdayImpl(LocalDate.of(2020,  5, 28), true), new SpecialdayImpl(DayOfWeek.FRIDAY)});
		Mockito.when(specialdayRepository.findByTypeIn(Arrays.asList(Type.values()))).thenReturn(specialdays);
	}
	@Test
	void specialdaysRulesEngineResultFix() {
		prepareRulesEngine();
		final LocalDate date = LocalDate.of(2020, 5, 1);
		final SpecialdaysRulesEngineResult  result =specialdayService.specialdaysRulesEngineResult(date);
		assertEquals(DayType.NonWorkingDay, result.dayType());
		assertEquals(String.format(AbstractSpecialdaysRule.DAY_TYPE_INFO_FORMAT, Type.Fix, date ), result.description());
	}
	@Test
	void specialdaysRulesEngineResultWeekend() {
		prepareRulesEngine();
		final LocalDate date = LocalDate.of(2020, 5, 2);
		
		final SpecialdaysRulesEngineResult  result =specialdayService.specialdaysRulesEngineResult(date);
		assertEquals(DayType.NonWorkingDay, result.dayType());
		assertEquals(String.format(AbstractSpecialdaysRule.DAY_TYPE_INFO_FORMAT, Type.Weekend, date.getDayOfWeek() ), result.description());
		
	}	
	
	@Test
	void specialdaysRulesEngineResultSpecialWorkingDate() {
		prepareRulesEngine();
		final LocalDate date = LocalDate.of(2020, 5, 28);
		
		final SpecialdaysRulesEngineResult  result =specialdayService.specialdaysRulesEngineResult(date);
		assertEquals(DayType.SpecialWorkingDay, result.dayType());
		assertEquals(String.format(AbstractSpecialdaysRule.DAY_TYPE_INFO_FORMAT, Type.SpecialWorkingDate, date ), result.description());
	}
	
	@Test
	void specialdaysRulesEngineResultSpecialWorkingDay() {
		prepareRulesEngine();
		final LocalDate date = LocalDate.of(2020, 5, 8);
		
		final SpecialdaysRulesEngineResult  result =specialdayService.specialdaysRulesEngineResult(date);
		assertEquals(DayType.SpecialWorkingDay, result.dayType());
		assertEquals(String.format(AbstractSpecialdaysRule.DAY_TYPE_INFO_FORMAT, Type.SpecialWorkingDay, date.getDayOfWeek() ), result.description());
	}
	
	@Test
	void specialdaysRulesEngineResultWorkingDay() {
		prepareRulesEngine();
		final LocalDate date = LocalDate.of(2020, 5, 4);
		final SpecialdaysRulesEngineResult  result =specialdayService.specialdaysRulesEngineResult(date);
		assertEquals(DayType.WorkingDay, result.dayType());
		assertEquals(String.format(AbstractSpecialdaysRule.DAY_TYPE_INFO_FORMAT, DayType.WorkingDay, date ), result.description());
		
	}
	
	
	
}
