package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class DoubleStateTest {
	
	private static final Double VALUE = 47.11;
	private static final String NAME = "DoubleState";
	private static final long ID = 19680528L;
	
	private final State<Double>  doubleState = new DoubleStateImpl(ID, NAME, LocalDateTime.now());
	
	@Test
	void assign() {
		assertEquals(Double.valueOf(0d), doubleState.value());
		
		doubleState.assign(VALUE);
		
		assertEquals(VALUE, doubleState.value());
	}


	@Test
	void assignWrongRange() {
		((AbstractState<Double>)doubleState).assign(value -> value >=0);
		((AbstractState<Double>)doubleState).assign(value -> value <=10);
		
		assertThrows(IllegalArgumentException.class,  () -> doubleState.assign(VALUE));
	}
	
	@Test
	void assignNullValue() {
		doubleState.assign(VALUE);
		assertEquals(VALUE, doubleState.value());
		
		doubleState.assign(null);
		
		assertEquals(Double.valueOf(0d), doubleState.value());
	}
	
	@Test
	void Validate() {
		final double delta = 1e-6;
		((AbstractState<Double>)doubleState).assign(value -> value >=0 && value <=10);
		assertTrue(doubleState.validate(0d));
		assertTrue(doubleState.validate(10d));
		assertFalse(doubleState.validate(-delta));
		assertFalse(doubleState.validate(10 + delta));
	}
	
}
