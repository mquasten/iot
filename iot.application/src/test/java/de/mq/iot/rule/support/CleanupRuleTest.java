package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class CleanupRuleTest {
	
	private final EndOfDayRuleInput ruleInput = new EndOfDayRuleInput(100,6,30,false);
	private final CleanupRuleImpl rule = new CleanupRuleImpl();
	
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
	

}
