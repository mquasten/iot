package de.mq.iot.calendar.support;




import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.jeasy.rules.api.Facts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService.DayType;


class AbstractSpecialdaysRuleTest {
	
	static final int PRIORITY = 1;
	private final AbstractSpecialdaysRule abstractSpecialdaysRuleMock = Mockito.mock(AbstractSpecialdaysRule.class);
	private AbstractSpecialdaysRule abstractSpecialdaysRule;
	private SpecialdaysRulesEngineResultImpl specialdaysRulesEngineResult = Mockito.mock(SpecialdaysRulesEngineResultImpl.class);
	private Specialday specialday = Mockito.mock(Specialday.class);

	@BeforeEach
	void setup() throws Exception {
	
		abstractSpecialdaysRule=BeanUtils.instantiateClass(abstractSpecialdaysRuleMock.getClass().getDeclaredConstructor(int.class), PRIORITY);
	}
	
	@Test
	void created() {
		assertNotNull(abstractSpecialdaysRule);
	}
	
	@Test
	void execute() throws Exception {
		
		Mockito.doReturn(Optional.of(new AbstractMap.SimpleImmutableEntry<>(DayType.SpecialWorkingDay, DayType.SpecialWorkingDay.name()))).when(abstractSpecialdaysRuleMock).execute(Mockito.any(), Mockito.any());
		final Facts facts = new Facts();
		final LocalDate localDate = LocalDate.now();
		facts.put(SpecialdaysRulesEngineBuilder.RESULT, specialdaysRulesEngineResult );
		facts.put(SpecialdaysRulesEngineBuilder.SPECIALDAYS_INPUT, Arrays.asList(specialday));
		facts.put(SpecialdaysRulesEngineBuilder.DATE_INPUT, localDate);
		abstractSpecialdaysRuleMock.execute(facts);
		Mockito.verify(specialdaysRulesEngineResult).assign(DayType.SpecialWorkingDay, DayType.SpecialWorkingDay.name());
		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Collection<Specialday>> argumentCaptorSpecialdays = ArgumentCaptor.forClass(Collection.class);
		final ArgumentCaptor<LocalDate> argumentCaptorDate = ArgumentCaptor.forClass(LocalDate.class);
		Mockito.verify(abstractSpecialdaysRuleMock).execute(argumentCaptorSpecialdays.capture(), argumentCaptorDate.capture());
		
		assertEquals(localDate, argumentCaptorDate.getValue());
		assertEquals(Arrays.asList(specialday), argumentCaptorSpecialdays.getValue());
	}
	
	@Test
	void getPriority() {
		assertEquals(PRIORITY, abstractSpecialdaysRule.getPriority());
	}
	
	@Test
	void  evaluate() {
		assertTrue(abstractSpecialdaysRule.evaluate(new Facts()));
	}
	
	@Test
	void getName() {
		assertEquals(abstractSpecialdaysRule.getClass().getSimpleName(), abstractSpecialdaysRule.getName());
	}
	
	
	@Test
	void getDescription() {
		assertEquals(String.format(AbstractSpecialdaysRule.DESCRIPTION_FORMAT,abstractSpecialdaysRule.getClass().getSimpleName(), PRIORITY ), abstractSpecialdaysRule.getDescription());
	}

}
