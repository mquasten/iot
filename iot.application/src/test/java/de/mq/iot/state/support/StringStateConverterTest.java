package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

class StringStateConverterTest {
	
	private static final String VALUE = "KylieIsNiceAnd...";
	private final ConversionService conversionService = new DefaultConversionService();
	private final StringStateConverterImpl converter = new StringStateConverterImpl(conversionService);
	
	private static final String ID = "4711";
	private static final String NAME = "StringValue";
	private static final String TIMESTAMP = "" + new Date().getTime() / 1000;

	@Test
	void key() {
		assertEquals("20", converter.key());
	}
	
	@Test
	void target() {
		assertEquals(StringStateImpl.class, converter.target());
	}
	
	
	@Test
	void  createState() {
		final State<String> state = converter.convert(valuesMap());
		
		assertEquals(Long.valueOf(ID), Long.valueOf(state.id()));
		assertEquals(NAME, state.name());
		assertEquals(VALUE, state.value());
		assertEquals(expectedTime(), state.lastupdate());
	}
	
	private Map<String, String> valuesMap() {
		final Map<String, String> valuesMap = new HashMap<>();
		valuesMap.put(StateConverter.KEY_ID, ID);
		valuesMap.put(StateConverter.KEY_NAME, NAME);
		valuesMap.put(StateConverter.KEY_TIMESTAMP, TIMESTAMP);
		valuesMap.put(StateConverter.KEY_VALUE, VALUE);
		valuesMap.put(StateConverter.KEY_TYPE, "20");
		return valuesMap;
	}
	
	private LocalDateTime expectedTime() {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * Long.valueOf(TIMESTAMP)), TimeZone.getDefault().toZoneId());
	}

}
