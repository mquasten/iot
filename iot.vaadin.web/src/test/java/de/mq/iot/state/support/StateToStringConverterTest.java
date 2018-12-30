package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import de.mq.iot.state.State;

public class StateToStringConverterTest {
	
	private static final LocalDateTime NOW = LocalDateTime.now();
	private static final String NAME = "name";
	private static final long ID = 4711L;
	private  StateToStringConverter  stateToStringConverter = new StateToStringConverterImpl();
	
	
	@Test
	void convertDoubleState() {
		final State<Double> state = new DoubleStateImpl(ID, NAME, NOW);
		final double value = 0.5d;
		state.assign(value);
		
		assertEquals(String.valueOf((int)(100d*value)), stateToStringConverter.convert(state));
	}
	
	
	@Test
	void convertBooleanState() {
		final State<Boolean> state = new BooleanStateImpl(ID, NAME, NOW);
		state.assign(Boolean.TRUE);
		
		assertEquals(Boolean.TRUE.toString(), stateToStringConverter.convert(state));
	}
	
	@Test
	void convertUndefinedState() {
		assertThrows(IllegalArgumentException.class, () ->stateToStringConverter.convert(new StringStateImpl(ID, NAME, NOW)));
	}
	
	@Test
	void convertValueDouble() {
		final double value = 0.5d;
		assertEquals(String.valueOf((int)(100d*value)), stateToStringConverter.convertValue(value));
	}

	
	@Test
	void convertValueBoolean() {
		assertEquals(Boolean.TRUE.toString(), stateToStringConverter.convertValue(Boolean.TRUE));
	}
	
	@Test
	void convertValueUndefined() {
		assertThrows(IllegalArgumentException.class, () -> stateToStringConverter.convertValue(new Integer(4711)));
	}

}
