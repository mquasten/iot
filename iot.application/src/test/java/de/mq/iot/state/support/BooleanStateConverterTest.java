package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

class BooleanStateConverterTest {

	
	private final BooleanStateConverterImpl converter = new BooleanStateConverterImpl();
	private static final String ID = "4711";
	private static final String WORKINGDAY = "Workingday";
	private static final String TIMESTAMP = "" + new Date().getTime() / 1000;
	
	@Test
	final void convert() {
		final Map<String, String> valuesMap = valuesMap();
		
		
		final State<Boolean> state = converter.convert(valuesMap);
		assertEquals(WORKINGDAY, state.name());
		assertEquals(ID,  "" + state.id());
		assertTrue(state.value());
		assertEquals(expectedTime(), state.lastupdate());
	
		
	}

	private Map<String, String> valuesMap() {
		final Map<String,String> valuesMap = new HashMap<>();
		valuesMap.put(StateConverter.KEY_ID, ID);
		valuesMap.put(StateConverter.KEY_NAME, WORKINGDAY);
		valuesMap.put(StateConverter.KEY_TIMESTAMP, TIMESTAMP);
		valuesMap.put(StateConverter.KEY_VALUE, Boolean.TRUE.toString());
		valuesMap.put(StateConverter.KEY_TYPE, "2");
		return valuesMap;
	}
	
	@Test
	final void key() {
		assertEquals("2", converter.key());
	}

	private LocalDateTime expectedTime() {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(1000* Long.valueOf(TIMESTAMP)), TimeZone.getDefault().toZoneId());
	}
	

	@Test
	final void target() {
		assertEquals(BooleanStateImpl.class, converter.target());
	}
	
	@Test
	final void convertNoConstructor() {
		
		final AbstractStateConverter<?> converter  =  Mockito.mock(AbstractStateConverter.class, Mockito.CALLS_REAL_METHODS);
		Mockito.doReturn(State.class).when(converter).target();
		ConversionService conversionService = new DefaultConversionService();
		Mockito.doReturn(conversionService).when(converter).conversionService();
		
		assertThrows(IllegalStateException.class, () -> converter.convert(valuesMap()));
		
	
		
	}
}
