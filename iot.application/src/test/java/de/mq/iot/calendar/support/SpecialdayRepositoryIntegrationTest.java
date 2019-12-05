package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.Specialday.FixedSpecialDay;
import de.mq.iot.calendar.support.SpecialdayImpl.Type;
import de.mq.iot.support.ApplicationConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })
class SpecialdayRepositoryIntegrationTest {
	
	private static final int YEAR = 2018;
	private static final LocalDate DATE = LocalDate.of(YEAR, 5, 28);
	@Autowired
	private SpecialdayRepository specialdaysRepository;

	
	@Test
	@Disabled
	void saveFixedSpecialDay() {
	
		final Set<LocalDate> days = new HashSet<>();
		Arrays.asList(Specialday.FixedSpecialDay.values()).forEach(value -> {
			Specialday specialday = new SpecialdayImpl(value);
			assertEquals(specialday.date(YEAR), specialdaysRepository.save(specialday).block().date(2018));
			days.add(specialday.date(2018));
		});
		
		final Collection<Specialday> results = specialdaysRepository.findByTypeIn(Arrays.asList(Type.Fix)).collectList().block(Duration.ofMillis(500));
		assertEquals(FixedSpecialDay.values().length, results.size());
		
		results.forEach(result -> assertTrue(days.contains(result.date(YEAR))));
		
	}
	
	@Test
	@Disabled
	void saveVariantSpecialDay() {
		final Set<LocalDate> days = new HashSet<>();
		Arrays.asList(Specialday.VariantSpecialDay.values()).forEach(value -> {
			Specialday specialday = new SpecialdayImpl(value);
			assertEquals(specialday.date(YEAR), specialdaysRepository.save(specialday).block().date(2018));
			days.add(specialday.date(2018));
		});
		
		final Collection<Specialday> results = specialdaysRepository.findByTypeIn(Arrays.asList(Type.Gauss)).collectList().block(Duration.ofMillis(500));
		assertEquals(FixedSpecialDay.values().length, results.size());
		
		results.forEach(result -> assertTrue(days.contains(result.date(YEAR))));
		
	}
	
	@Test
	@Disabled
	void saveVacation() {
		final Specialday specialday = new SpecialdayImpl(DATE);
		
		assertEquals(DATE, specialdaysRepository.save(specialday).block().date(YEAR));
		
		final Collection<Specialday> results = specialdaysRepository.findByTypeAndYear(Type.Vacation, YEAR).collectList().block(Duration.ofMillis(500));
		
		assertEquals(1, results.size());
		assertEquals(DATE, results.stream().findAny().orElseThrow(() -> new IllegalStateException("Result expected.")).date(YEAR));
	}
	
	
	
}
