package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService.DayType;

class SpecialdaysRulesEngineBuilderTest {
	
	private static final String DESCRIPTION = "description";

	private static final String RULENAME = "rulename";

	private final SpecialdaysRulesEngineBuilder specialdaysRulesEngineBuilder = new SpecialdaysRulesEngineBuilder();
	
	private final Rule rule = Mockito.mock(Rule.class);
	
	private final Specialday specialday = Mockito.mock(Specialday.class);
	
	@Test
	void withRules() {
		specialdaysRulesEngineBuilder.withRules(Arrays.asList(rule));
		
		final var rules=  dependency(specialdaysRulesEngineBuilder, Rules.class);
		
		
		final Set<?> ruleSet  = dependency(rules, Set.class);
		assertEquals(1, ruleSet.size());
		assertEquals(rule, ruleSet.stream().findAny().get());
	    
	}
	@Test
	void withSpecialDays() {
		specialdaysRulesEngineBuilder.withSpecialdays(Arrays.asList(specialday));
		final Collection<?> specialdays= dependency(specialdaysRulesEngineBuilder, Collection.class);
		
		assertEquals(1, specialdays.size());
		assertEquals(specialday, specialdays.stream().findAny().get());
	}

	@SuppressWarnings("unchecked")
	private <T> T dependency(final Object target, Class<?> type) {
		return (T) Arrays.asList(target.getClass().getDeclaredFields()).stream().filter(field -> field.getType().equals(type)).map(field ->ReflectionTestUtils.getField(target, field.getName())).findAny().orElseThrow();
	}
	@Test
	void applySucces() throws Exception {
		Mockito.when(rule.evaluate(Mockito.any())).thenReturn(true);
		Mockito.when(rule.getName()).thenReturn(RULENAME);
		Mockito.doAnswer(answer->{
			final Facts facts = (Facts) answer.getArguments()[0];
			final SpecialdaysRulesEngineResultImpl result =facts.get(SpecialdaysRulesEngineBuilder.RESULT);
			result.assign(DayType.WorkingDay, DESCRIPTION);
		return null;
		}).when(rule).execute(Mockito.any());
		final LocalDate localDate = LocalDate.now();
		SpecialdaysRulesEngineResultImpl specialdaysRulesEngineResult = (SpecialdaysRulesEngineResultImpl) specialdaysRulesEngineBuilder.withRules(Arrays.asList(rule)).withSpecialdays(Arrays.asList(specialday)).execute(localDate);
		
		assertEquals(Optional.of(RULENAME), specialdaysRulesEngineResult.successRule());
		assertTrue(specialdaysRulesEngineResult.finished());
		assertEquals(DayType.WorkingDay, specialdaysRulesEngineResult.dayType());
		assertEquals(DESCRIPTION, specialdaysRulesEngineResult.description());
	}

}
