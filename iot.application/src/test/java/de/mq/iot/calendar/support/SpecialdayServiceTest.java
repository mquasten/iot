package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

}
