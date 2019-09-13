package de.mq.iot.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SunDownCalculationServiceTest {

	private final SunDownCalculationService sunDownCalculationService = new SimpleSunDownCalculationServiceImpl(51.1423399, 6.2815922);

	private final Map<Month, LocalTime> winterDown = new HashMap<>();
	private final Map<Month, LocalTime> summerDown = new HashMap<>();

	private final Map<Month, LocalTime> winterUp = new HashMap<>();
	private final Map<Month, LocalTime> summerUp = new HashMap<>();

	@BeforeEach
	void setup() {
		winterDown.put(Month.JANUARY, LocalTime.of(16, 59));
		winterDown.put(Month.FEBRUARY, LocalTime.of(17, 48));
		winterDown.put(Month.MARCH, LocalTime.of(18, 39));

		winterDown.put(Month.OCTOBER, LocalTime.of(17, 41));
		winterDown.put(Month.NOVEMBER, LocalTime.of(16, 49));
		winterDown.put(Month.DECEMBER, LocalTime.of(16, 30));

		summerDown.put(Month.MARCH, LocalTime.of(19, 39));
		summerDown.put(Month.APRIL, LocalTime.of(20, 30));
		summerDown.put(Month.MAY, LocalTime.of(21, 18));
		summerDown.put(Month.JUNE, LocalTime.of(21, 50));
		summerDown.put(Month.JULY, LocalTime.of(21, 43));
		summerDown.put(Month.AUGUST, LocalTime.of(20, 56));
		summerDown.put(Month.SEPTEMBER, LocalTime.of(19, 50));
		summerDown.put(Month.OCTOBER, LocalTime.of(18, 41));

		winterUp.put(Month.JANUARY, LocalTime.of(8, 28));
		winterUp.put(Month.FEBRUARY, LocalTime.of(7, 49));
		winterUp.put(Month.MARCH, LocalTime.of(6, 48));

		winterUp.put(Month.OCTOBER, LocalTime.of(6, 59));
		winterUp.put(Month.NOVEMBER, LocalTime.of(7, 51));
		winterUp.put(Month.DECEMBER, LocalTime.of(8, 30));

		summerUp.put(Month.MARCH, LocalTime.of(7, 48));
		summerUp.put(Month.APRIL, LocalTime.of(6, 40));
		summerUp.put(Month.MAY, LocalTime.of(5, 45));
		summerUp.put(Month.JUNE, LocalTime.of(5, 21));
		summerUp.put(Month.JULY, LocalTime.of(5, 38));
		summerUp.put(Month.AUGUST, LocalTime.of(6, 21));
		summerUp.put(Month.SEPTEMBER, LocalTime.of(7, 10));
		summerUp.put(Month.OCTOBER, LocalTime.of(7, 59));
	}

	@Test
	final void sunDownTime() {

		final LocalTime result = sunDownCalculationService.sunDownTime(86, 2);
		// System.out.println(">>>"+(int) result +":" + (int) Math.round(60 *
		// (result % 1) ));

		assertEquals(19, result.getHour());
		assertEquals(57, result.getMinute());
	}

	@Test

	final void sunUpTime() {

		final LocalTime result = sunDownCalculationService.sunUpTime(86, 2);

		// System.out.println(result);

		assertEquals(7, result.getHour());
		assertEquals(23, result.getMinute());
	}

	@Test
	final void sunDownTimeMonth() {
		LocalTime result = sunDownCalculationService.sunDownTime(Month.MARCH, 2);

		// System.out.println(result);

		assertEquals(LocalTime.of(19, 39), result);

	}

	@Test
	final void sunDownTimeMonthAll() {
		IntStream.range(1, 4).forEach(month -> assertEquals(winterDown.get(Month.of(month)), sunDownCalculationService.sunDownTime(Month.of(month), 1)));

		IntStream.range(3, 11).forEach(month -> assertEquals(summerDown.get(Month.of(month)), sunDownCalculationService.sunDownTime(Month.of(month), 2)));

		IntStream.range(10, 13).forEach(month -> assertEquals(winterDown.get(Month.of(month)), sunDownCalculationService.sunDownTime(Month.of(month), 1)));
	}

	@Test
	final void sunUpTimeMonth() {
		final LocalTime result = sunDownCalculationService.sunUpTime(Month.MARCH, 2);

		// System.out.println(result);

		assertEquals(LocalTime.of(7, 48), result);

	}

	@Test
	final void sunUpTimeMonthAll() {

		IntStream.range(1, 4).forEach(month -> assertEquals(winterUp.get(Month.of(month)), sunDownCalculationService.sunUpTime(Month.of(month), 1)));

		IntStream.range(3, 11).forEach(month -> assertEquals(summerUp.get(Month.of(month)), sunDownCalculationService.sunUpTime(Month.of(month), 2)));

		IntStream.range(10, 13).forEach(month -> assertEquals(winterUp.get(Month.of(month)), sunDownCalculationService.sunUpTime(Month.of(month), 1)));
	}

}
