package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class DefaultRuleInputTest {
	
	private final LocalTime workingdayAlarmTime = LocalTime.of(5, 15);
	
	private final  LocalTime holidayAlarmTime = LocalTime.of(7, 15);
	
	private final DefaultRuleInput ruleInput = new DefaultRuleInput(workingdayAlarmTime, holidayAlarmTime);
	
	@Test
	void workingdayAlarmTime() {
		assertEquals(workingdayAlarmTime, ruleInput.workingdayAlarmTime());
	}
	
	
	@Test
	void holidayAlarmTime() {
		assertEquals(holidayAlarmTime, ruleInput.holidayAlarmTime());
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
		assertNull(ruleInput.workingdayAlarmTime());
		assertNull(ruleInput.holidayAlarmTime());
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
	    assertTrue(ruleInput.valid());
	    
	}
}
