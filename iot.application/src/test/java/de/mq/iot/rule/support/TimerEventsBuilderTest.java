package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;
import java.util.Map;


import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.rule.support.TimerEventsBuilder.Key;


class TimerEventsBuilderTest {
	
	TimerEventsBuilder timerEventsBuilder= TimerEventsBuilder.newBuilder();
	
	
	
	
	@Test
	void withEntry() {
	
		LocalTime now = LocalTime.now();
		timerEventsBuilder.with(Key.T0, now);
		final Map<?,?> events = (Map<?, ?>) ReflectionTestUtils.getField(timerEventsBuilder, "events");
		assertEquals(1, events.size());
		assertEquals(events.get(Key.T0), now);
	}
	@Test
	void build() {
		timerEventsBuilder.with(Key.T0, LocalTime.of(5, 15)).with(Key.T1, LocalTime.of(6,45)).with(Key.T6, LocalTime.of(20, 45)).build();
		final Map<?,?> fields = (Map<?, ?>) ReflectionTestUtils.getField(timerEventsBuilder, "events");
		assertEquals(3, fields.size());
				
	}
	
	
	
	
	
	
	}