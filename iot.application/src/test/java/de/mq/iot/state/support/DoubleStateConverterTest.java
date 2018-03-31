package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

class DoubleStateConverterTest {

	private static final Double MAX_VALUE = 10d;

	private static final Double MIN_VALUE = -10d;

	private static final Double VALUE = Double.valueOf(47.11);
	private final ConversionService conversionService = new DefaultConversionService();
	private final AbstractStateConverter<Double> converter = new DoubleStateConverterImpl(conversionService);

	private static final String ID = "4711";
	private static final String NAME = "DoubleValue";
	private static final String TIMESTAMP = "" + new Date().getTime() / 1000;

	@Test
	void key() {
		assertEquals("4", converter.key());
	}

	@Test
	void target() {
		assertEquals(DoubleStateImpl.class, converter.target());
	}
	@Test
	void convert() {
		final State<Double> state = converter.convert(valuesMap());
		assertEquals(Long.valueOf(ID), Long.valueOf(state.id()));
		assertEquals(NAME, state.name());
		assertEquals(VALUE, state.value());
		assertEquals(expectedTime(), state.lastupdate());
	}
	
	@Test
	void validators() {
		assertEquals(DoubleStateImpl.class, converter.target());

		final Map<String, String> values = valuesMap();
		values.put(StateConverter.KEY_MIN, ""+ MIN_VALUE);
		values.put(StateConverter.KEY_MAX, ""+MAX_VALUE);
		final State<Double> state = converter.convert(values);
		final double value = 10d; 
		final double delta = 1e-12; 
		assertTrue(state.validate(value));
		assertFalse(state.validate(value+delta));
		
		assertTrue(state.validate(-value));
		assertFalse(state.validate(-value-delta));
		
		assertTrue(((MinMaxRange)state).getMin().isPresent());
		assertTrue(((MinMaxRange)state).getMax().isPresent());
	
		assertEquals(MIN_VALUE, ((MinMaxRange)state).getMin().get());
		assertEquals(MAX_VALUE, ((MinMaxRange)state).getMax().get());

	}

	private Map<String, String> valuesMap() {
		final Map<String, String> valuesMap = new HashMap<>();
		valuesMap.put(StateConverter.KEY_ID, ID);
		valuesMap.put(StateConverter.KEY_NAME, NAME);
		valuesMap.put(StateConverter.KEY_TIMESTAMP, TIMESTAMP);
		valuesMap.put(StateConverter.KEY_VALUE, "" + VALUE);
		valuesMap.put(StateConverter.KEY_TYPE, "4");
		return valuesMap;
	}

	private LocalDateTime expectedTime() {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * Long.valueOf(TIMESTAMP)), TimeZone.getDefault().toZoneId());
	}

}
