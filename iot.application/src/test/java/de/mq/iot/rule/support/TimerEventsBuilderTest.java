package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.springframework.test.util.ReflectionTestUtils;

class TimerEventsBuilderTest {
	
	TimerEventsBuilder timerEventsBuilder= TimerEventsBuilder.newBuilder();
	
	@Test
	void withNonMode() {
		
		timerEventsBuilder.with(false);
		assertEquals(TimerEventsBuilder.DAILY_EVENTS_VARIABLE_NAME, ReflectionTestUtils.getField(timerEventsBuilder, "name"));
		
		
	}
	
	@Test
	void withMode() {
		
		timerEventsBuilder.with(true);
		assertEquals(TimerEventsBuilder.EVENT_EXECUTIONS_VARIABLE_NAME, ReflectionTestUtils.getField(timerEventsBuilder, "name"));
	}


	
}




