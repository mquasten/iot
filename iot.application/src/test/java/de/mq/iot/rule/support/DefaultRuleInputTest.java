package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.calendar.DayGroup;

class DefaultRuleInputTest {
	
	private final LocalTime workingdayAlarmTime = LocalTime.of(5, 15);
	
	private final  LocalTime holidayAlarmTime = LocalTime.of(7, 15);
	
	private final  LocalTime specialWorkingdayTime = LocalTime.of(6, 15);
	
	private final  LocalTime minSunDownTime = LocalTime.of(17, 15);
	
	private final DefaultRuleInput ruleInput = new DefaultRuleInput(workingdayAlarmTime, holidayAlarmTime, specialWorkingdayTime, minSunDownTime);
	
	private final DayGroup dayGroup = Mockito.mock(DayGroup.class);
	
	@Test
	void workingdayAlarmTime() {
		Mockito.when(dayGroup.name()).thenReturn(DayGroup.WORKINGDAY_GROUP_NAME);
		assertEquals(workingdayAlarmTime, ruleInput.alarmTime(dayGroup));
	}
	
	
	@Test
	void holidayAlarmTime() {
		Mockito.when(dayGroup.name()).thenReturn(DayGroup.NON_WORKINGDAY_GROUP_NAME);
		assertEquals(holidayAlarmTime, ruleInput.alarmTime(dayGroup));
	}
	
	@Test
	void specialWorkingdayAlarmTime() {
		Mockito.when(dayGroup.name()).thenReturn(DayGroup.SPECIAL_WORKINGDAY_GROUP_NAME);
		assertEquals(specialWorkingdayTime, ruleInput.alarmTime(dayGroup));
	}
	@Test
	void alarmTimeDayTypeMissing() {
		assertThrows(IllegalArgumentException.class, () -> ruleInput.alarmTime(null));
	}
	
	@Test
	void  minSunDownTime() {
		assertEquals(minSunDownTime, ruleInput.minSunDownTime());
	}
	
	@Test
	void updateMode() {
		assertFalse(ruleInput.isUpdateMode());
		ruleInput.useUpdateMode();
		assertTrue(ruleInput.isUpdateMode());
	}
	
	@Test
	void testMode() {
		assertFalse(ruleInput.isTestMode());
		ruleInput.useTestMode();
		assertTrue(ruleInput.isTestMode());
	}
	@Test
	void defaultConstructor() {
		final DefaultRuleInput ruleInput = new DefaultRuleInput();
		assertFalse(ruleInput.isUpdateMode());
		
		
		assertFalse(ruleInput.valid());
	}
	@Test
	void valid() {
		assertTrue(ruleInput.valid());
		
		final List<String> fields = Arrays.asList(DefaultRuleInput.class.getDeclaredFields()).stream().filter(field->field.getType()==LocalTime.class).map(field -> field.getName()).collect(Collectors.toList());
	
	    fields.forEach(name -> ReflectionTestUtils.setField(ruleInput, name, null));
	    assertFalse(ruleInput.valid());
	    ReflectionTestUtils.setField(ruleInput, fields.get(0), LocalTime.now());
	    assertFalse(ruleInput.valid());
	    ReflectionTestUtils.setField(ruleInput, fields.get(1), LocalTime.now());
	    ReflectionTestUtils.setField(ruleInput, fields.get(2), LocalTime.now());
	    assertTrue(ruleInput.valid());
	    
	}


	@Test
	void minEventTime() throws InterruptedException {
		
		assertTrue(100 > toMillis(LocalTime.now()) - toMillis(ruleInput.minEventTime()));
		
		final LocalTime time = LocalTime.of(11, 11);
		ReflectionTestUtils.setField(ruleInput, "minEventTime", time);
		assertEquals(time, ruleInput.minEventTime());
	}
	private long toMillis(LocalTime localTime) {
	 return localTime.atDate(LocalDate.now())
             .atZone(java.time.ZoneId.systemDefault())
             .toInstant()
             .toEpochMilli();
	}
}
