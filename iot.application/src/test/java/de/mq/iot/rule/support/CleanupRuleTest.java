package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService;

class CleanupRuleTest {
	
	private static final int MAX_IP_COUNT = 6;
	private static final int FIRST_IP = 100;
	private static final int DAYS_BACK = 60;
	private final EndOfDayRuleInput ruleInput = new EndOfDayRuleInput(FIRST_IP,MAX_IP_COUNT,DAYS_BACK,false, false);
	
	private final SpecialdayService specialdayService = Mockito.mock(SpecialdayService.class);
	private final CleanupRuleImpl rule = new CleanupRuleImpl(specialdayService);
	
	private final Collection<String> results = new ArrayList<>();
	
	private  final Specialday first = Mockito.mock(Specialday.class);
	private final Specialday second = Mockito.mock(Specialday.class);
	private final Collection<Specialday> specialdays = Arrays.asList(first,second);
	
	@BeforeEach
	void setup() {
		Mockito.doReturn(specialdays).when(specialdayService).vacationsBeforeEquals(LocalDate.now().minusDays(DAYS_BACK));
	}
	
	@Test
	void evaluate() {
		assertTrue(rule.evaluate(ruleInput));
	}
	
	@Test
	void evaluateInvalid() {
		setInvalid();
	
		assertFalse(rule.evaluate(ruleInput));
	
	}

	private void setInvalid() {
		Arrays.asList(EndOfDayRuleInput.class.getDeclaredFields()).stream().filter(field -> ! Modifier.isStatic(field.getModifiers())).filter(field -> field.getType().equals(Integer.class)).forEach(field -> ReflectionTestUtils.setField(ruleInput, field.getName(), null));
	}
	

	
	@Test
	void cleanup() {
		
		rule.cleanup(ruleInput, results);
		
		assertResults(CleanupRuleImpl.SUCCESS_MESSAGE);
		
		Mockito.verify(specialdayService).delete(first);
		Mockito.verify(specialdayService).delete(second);
	}

	private void assertResults(final String pattern) {
		assertEquals(1, results.size());
		
		assertEquals(String.format(pattern, 2, LocalDate.now().minusDays(DAYS_BACK)), results.stream().findAny().get());
	}
	
	@Test
	void cleanupTest() {
		
		final EndOfDayRuleInput ruleInput = new EndOfDayRuleInput(FIRST_IP,MAX_IP_COUNT,DAYS_BACK,false, true);
		
		rule.cleanup(ruleInput, results);
		
		assertResults(CleanupRuleImpl.SUCCESS_MESSAGE_TEST);
		
		Mockito.verify(specialdayService,Mockito.never()).delete(first);
		Mockito.verify(specialdayService, Mockito.never()).delete(second);
	}
}
