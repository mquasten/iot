package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

import de.mq.iot.state.State;

public class StateTypeInfoConverterTest {
	
	private static final LocalDateTime DATE = LocalDateTime.now();
	private static final String NAME = "name";
	private static final long ID = 4711L;
	private final Converter<State<?>, String> converter = new StateTypeInfoConverterImpl();
	
	@Test
	void doubleState() {
		assertEquals(StateTypeInfoConverterImpl.LEVEL, converter.convert(new DoubleStateImpl(ID, NAME, DATE)));
	}
	
	@Test
	void booleanState() {
		assertEquals(StateTypeInfoConverterImpl.STATE, converter.convert(new BooleanStateImpl(ID, NAME, DATE)));
	}
	
	@Test
	void itemsState() {
		assertThrows(IllegalArgumentException.class, () -> converter.convert(new ItemsStateImpl(ID, NAME, DATE)));
	}
	
	@Test
	void stringState() {
		assertThrows(IllegalArgumentException.class, () -> converter.convert(new StringStateImpl(ID, NAME, DATE)));
	}

}
