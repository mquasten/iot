package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;
import java.util.Map;


import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.rule.support.TimerEventsBuilder.Key;


class TimerEventsBuilderTest {
	
	private static final String EVENT1 = "T1:6.45";
	private static final String EVENT6 = "T6:23.59";
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
		
		
		assertEquals(String.format("T0:5.15;%s;T6:20.45", EVENT1), builder);
				
	}
	
	@Test
	void buildUpdateMode() {
	final String evemts = timerEventsBuilder.with(true).with(Key.T0, LocalTime.of(5, 15)).with(Key.T1, LocalTime.of(6,45)).with(Key.T6, LocalTime.of(23, 59)).build();
	
	
	assertEquals(EVENT6, evemts);
	
	}
	@Test
	void buildUpdateModeWithMinEventTime() {
		final String events = timerEventsBuilder.with(true).withMinEventTime(LocalTime.of(6, 0)).with(Key.T0, LocalTime.of(5, 15)).with(Key.T1, LocalTime.of(6,45)).with(Key.T6, LocalTime.of(23, 59)).build();

		final String[] results = events.split(";");
		
		assertEquals(2, results.length);
		
		assertEquals(EVENT1, results[0]);
		assertEquals(EVENT6, results[1]);
	
	}
	
	
	
	}