package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

class BooleanStateConverterTest {

	private static final String ID = "4711";
	private static final String WORKINGDAY = "Workingday";
	final BooleanStateConverter converter = new BooleanStateConverter();
	private static final String TIMESTAMP = "" + new Date().getTime() / 1000;
	
	@Test
	final void convert() {
		final Map<String,String> valuesMap = new HashMap<>();
		valuesMap.put(StateConverter.KEY_ID, ID);
		valuesMap.put(StateConverter.KEY_NAME, WORKINGDAY);
		valuesMap.put(StateConverter.KEY_TIMESTAMP, TIMESTAMP);
		valuesMap.put(StateConverter.KEY_VALUE, Boolean.TRUE.toString());
		valuesMap.put(StateConverter.KEY_TYPE, "2");
		
		
		final State<Boolean> state = converter.convert(valuesMap);
		assertEquals(WORKINGDAY, state.name());
		assertEquals(ID,  "" + state.id());
		assertTrue(state.value());
		assertEquals(expectedTime(), state.lastupdate());
	
		
	}
	
	@Test
	final void key() {
		assertEquals("2", converter.key());
	}

	private LocalDateTime expectedTime() {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(1000* Long.valueOf(TIMESTAMP)), TimeZone.getDefault().toZoneId());
	}
	

}
