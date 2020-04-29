package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService.DayType;
import de.mq.iot.support.ApplicationConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })
@Disabled
public class SpecialdaysServiceIntegrationTest {
	@Autowired
	private  SpecialdayServiceImpl specialdayService;
	
	@Test
	void specialdaysRulesEngineResultFix() {
		final LocalDate date = LocalDate.of(2020, 5, 1);
		final SpecialdaysRulesEngineResult result = specialdayService.specialdaysRulesEngineResult(date);
		
		assertEquals(DayType.NonWorkingDay, result.dayType());
		assertEquals(String.format(AbstractSpecialdaysRule.DAY_TYPE_INFO_FORMAT, Specialday.Type.Fix,date ), result.description());
		assertTrue(result.successRule().isPresent());
		assertEquals(VacationOrHolidayRuleImpl.class.getSimpleName(), result.successRule().get());
	}
	
	@Test
	void specialdaysRulesEngineResultGauss() {
		final LocalDate date = LocalDate.of(2020, 4, 13);
		final SpecialdaysRulesEngineResult result = specialdayService.specialdaysRulesEngineResult(date);
		assertEquals(DayType.NonWorkingDay, result.dayType());
		assertEquals(String.format(AbstractSpecialdaysRule.DAY_TYPE_INFO_FORMAT, Specialday.Type.Gauss,date ), result.description());
		assertTrue(result.successRule().isPresent());
		assertEquals(VacationOrHolidayRuleImpl.class.getSimpleName(), result.successRule().get());
	}
	
	@Test
	void specialdaysRulesEngineResultWeekend() {
		final LocalDate date = LocalDate.of(2020, 4, 26);
		final SpecialdaysRulesEngineResult result = specialdayService.specialdaysRulesEngineResult(date);
		assertEquals(DayType.NonWorkingDay, result.dayType());
		assertEquals(String.format(AbstractSpecialdaysRule.DAY_TYPE_INFO_FORMAT, Specialday.Type.Weekend,date.getDayOfWeek() ), result.description());
		assertTrue(result.successRule().isPresent());
		assertEquals(WeekendRuleImpl.class.getSimpleName(), result.successRule().get());
	}
	
	@Test
	void specialdaysRulesEngineResultVacation() {
		final LocalDate date = LocalDate.of(1831, 6, 13);
		final SpecialdaysRulesEngineResult result = specialdayService.specialdaysRulesEngineResult(date);
		assertEquals(DayType.NonWorkingDay, result.dayType());
		assertEquals(String.format(AbstractSpecialdaysRule.DAY_TYPE_INFO_FORMAT, Specialday.Type.Vacation,date ), result.description());
		assertTrue(result.successRule().isPresent());
		assertEquals(VacationOrHolidayRuleImpl.class.getSimpleName(), result.successRule().get());
	}
	
	@Test
	void specialdaysRulesEngineResultSpecialWorkingDate() {
		final LocalDate date = LocalDate.of(1968, 5, 28);
		final SpecialdaysRulesEngineResult result = specialdayService.specialdaysRulesEngineResult(date);
		assertEquals(DayType.SpecialWorkingDay, result.dayType());
		assertEquals(String.format(AbstractSpecialdaysRule.DAY_TYPE_INFO_FORMAT, Specialday.Type.SpecialWorkingDate,date ), result.description());
		assertTrue(result.successRule().isPresent());
		assertEquals(SpecialWorkingDateRuleImpl.class.getSimpleName(), result.successRule().get());
	}
	
	@Test
	void specialdaysRulesEngineResultSpecialWorkingDay() {
		final LocalDate date = LocalDate.of(2020, 4, 24);
		final SpecialdaysRulesEngineResult result = specialdayService.specialdaysRulesEngineResult(date);
		assertEquals(DayType.SpecialWorkingDay, result.dayType());
		assertEquals(String.format(AbstractSpecialdaysRule.DAY_TYPE_INFO_FORMAT, Specialday.Type.SpecialWorkingDay,date.getDayOfWeek() ), result.description());
		assertTrue(result.successRule().isPresent());
		assertEquals(SpecialWorkingDayRuleImpl.class.getSimpleName(), result.successRule().get());
	}
	
	@Test
	void specialdaysRulesEngineResultWorkingDay() {
		final LocalDate date = LocalDate.of(2020, 4, 29);
		final SpecialdaysRulesEngineResult result = specialdayService.specialdaysRulesEngineResult(date);
		assertEquals(DayType.WorkingDay, result.dayType());
		assertEquals(String.format(AbstractSpecialdaysRule.DAY_TYPE_INFO_FORMAT, DayType.WorkingDay,date ), result.description());
		assertTrue(result.successRule().isPresent());
		assertEquals(WorkingdayRuleImpl.class.getSimpleName(), result.successRule().get());
	}

}
