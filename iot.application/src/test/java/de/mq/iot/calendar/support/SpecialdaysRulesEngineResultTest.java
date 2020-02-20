package de.mq.iot.calendar.support;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.calendar.SpecialdayService.DayType;

class SpecialdaysRulesEngineResultTest {
	
	private static final String SUCCESS_RULE = "successRule";
	private static final String DESCRIPTION = "Description";
	private static final String RULE_NAME = "rule";
	private final SpecialdaysRulesEngineResultImpl specialdaysRulesEngineResult = new SpecialdaysRulesEngineResultImpl();
	
	@Test
	void dayType() {
		specialdaysRulesEngineResult.assign(DayType.WorkingDay, DESCRIPTION);
		
		assertEquals(DayType.WorkingDay, specialdaysRulesEngineResult.dayType());
		
		assertEquals(DESCRIPTION, specialdaysRulesEngineResult.description());
	}
	
	@Test
	void dayTypeNotAware() {
		assertThrows(IllegalStateException.class, () ->specialdaysRulesEngineResult.dayType());
	}
	@Test
	void descriptionNotAware() {
		assertThrows(IllegalStateException.class, () ->specialdaysRulesEngineResult.description());
	}
	
	@Test
	void dayTypeException() {
	
		final Exception exception = Mockito.mock(Exception.class);
		specialdaysRulesEngineResult.assign(exception, RULE_NAME);
		
		try {
		specialdaysRulesEngineResult.dayType();
		fail(IllegalStateException.class.getName() + " should be thrown");
		
		} catch(final Exception expectedException) {
			assertTrue(expectedException instanceof IllegalStateException); 
			assertEquals(exception, expectedException.getCause());
			assertEquals(SpecialdaysRulesEngineResultImpl.ERROR_MESSAGE + RULE_NAME, expectedException.getMessage());
		}
	}
	
	@Test
	void descriptionException() {
	
		final Exception exception = Mockito.mock(Exception.class);
		specialdaysRulesEngineResult.assign(exception, RULE_NAME);
		
		try {
		specialdaysRulesEngineResult.description();
		fail(IllegalStateException.class.getName() + " should be thrown");
		} catch(final Exception expectedException) {
			assertTrue(expectedException instanceof IllegalStateException); 
			assertEquals(exception, expectedException.getCause());
			assertEquals(SpecialdaysRulesEngineResultImpl.ERROR_MESSAGE + RULE_NAME, expectedException.getMessage());
		}
	}
	@Test
	void finishedResult() {
		assertFalse(specialdaysRulesEngineResult.finished());
		
		specialdaysRulesEngineResult.assign(DayType.WorkingDay, DESCRIPTION);
		
		assertTrue(specialdaysRulesEngineResult.finished());
	}
	
	@Test
	void finishedException() {
		assertFalse(specialdaysRulesEngineResult.finished());
		
		specialdaysRulesEngineResult.assign(Mockito.mock(Exception.class), RULE_NAME);
		
		assertTrue(specialdaysRulesEngineResult.finished());
	}
	
	@Test
	void successRule() {
		assertEquals(Optional.empty(), specialdaysRulesEngineResult.successRule());
		
		specialdaysRulesEngineResult.assignSuccessRule(SUCCESS_RULE);
		
		assertEquals(Optional.of(SUCCESS_RULE), specialdaysRulesEngineResult.successRule());
	}
	

}
