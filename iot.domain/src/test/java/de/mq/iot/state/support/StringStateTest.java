package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import de.mq.iot.state.State;

class StringStateTest {
	
	private static final String VALUE_INVALID = "Kylie\tis\tnice";
	private static final String VALUE = "KylieIsNiceAnd...";
	private static final String NAME = "StringState";
	private static final long ID = 19680528L;
	
	private final State<String> state = new StringStateImpl(ID, NAME, LocalDateTime.now());
	
	
	@Test
	final void id() {
		assertEquals(ID, state.id());
	}
	
	@Test
	final void name() {
		assertEquals(NAME, state.name());
	}
	
	@Test
	final void lastUpdate() throws InterruptedException {
		assertTrue(Math.abs(Duration.between(LocalDateTime.now(), state.lastupdate()).toMillis()) < 50);
	}
	
	@Test
	final void value() {
		assertEquals(StringStateImpl.DEFAULT_VALUE, state.value());

		state.assign(VALUE);
		assertEquals(VALUE, state.value());
		
		state.assign(null);
		assertEquals(StringStateImpl.DEFAULT_VALUE, state.value());
		
		state.assign(StringStateImpl.NULL_VALUE_CCU);
		assertEquals(StringStateImpl.DEFAULT_VALUE, state.value());
	}
	
	@Test
	final void  validate() {
		assertTrue(state.validate(VALUE));
		assertFalse(state.validate(VALUE_INVALID));
		assertFalse(state.validate(VALUE_INVALID.replace('\t', ' ')));
		assertTrue(state.validate(VALUE_INVALID.replace('\t', '_')));
	}
	
	@Test
	final void assignInValid() {
		assertThrows(IllegalArgumentException.class, () -> state.assign(VALUE_INVALID));
	}
	
	@Test
	final void hasValue() {
		assertFalse(state.hasValue(null));
		
		assertFalse(state.hasValue(VALUE));
		
		state.assign(VALUE);
		assertTrue(state.hasValue(VALUE));
	}

}
