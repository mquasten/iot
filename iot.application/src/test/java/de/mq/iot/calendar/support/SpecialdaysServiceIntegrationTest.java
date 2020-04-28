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

}
