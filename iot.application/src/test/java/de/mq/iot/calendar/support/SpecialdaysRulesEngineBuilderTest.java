package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.calendar.Specialday;

class SpecialdaysRulesEngineBuilderTest {
	
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
		specialdaysRulesEngineBuilder.withSpecialDays(Arrays.asList(specialday));
		final Collection<?> specialdays= dependency(specialdaysRulesEngineBuilder, Collection.class);
		
		assertEquals(1, specialdays.size());
		assertEquals(specialday, specialdays.stream().findAny().get());
	}

	@SuppressWarnings("unchecked")
	private <T> T dependency(final Object target, Class<?> type) {
		return (T) Arrays.asList(target.getClass().getDeclaredFields()).stream().filter(field -> field.getType().equals(type)).map(field ->ReflectionTestUtils.getField(target, field.getName())).findAny().orElseThrow();
	}

}
