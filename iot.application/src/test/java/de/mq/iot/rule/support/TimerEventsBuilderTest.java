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
		final String builder = timerEventsBuilder.with(Key.T0, LocalTime.of(5, 15)).with(Key.T1, LocalTime.of(6,45)).with(Key.T6, LocalTime.of(20, 45)).build();
		
		
		assertEquals("T0:5.15;T1:6.45;T6:20.45", builder);
				
	}
	
	@Test
	void buildUpdateMode() {
	final String builder = timerEventsBuilder.with(true).with(Key.T0, LocalTime.of(5, 15)).with(Key.T1, LocalTime.of(6,45)).with(Key.T6, LocalTime.of(23, 59)).build();
	
	
	assertEquals("T6:23.59", builder);
	
	}
	
	
	
	
	
	}