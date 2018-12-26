package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import de.mq.iot.state.State;

class BooleanStateTest {

	private static final String NAME = "WorkingDay";
	private static final long ID = 19680528L;
	private final State<Boolean> state = new BooleanStateImpl(ID, NAME, LocalDateTime.now());

	@Test
	final void id() {
		assertEquals(ID, state.id());
	}

	@Test
	final void name() {
		assertEquals(NAME, state.name());
	}

	@Test
	final void valueDefault() {
		
		assertFalse(state.value());
	}

	@Test
	final void assignValue() {
		state.assign(true);

		assertTrue(state.value());
	}

	@Test
	final void lastUpdate() throws InterruptedException {
		assertTrue(Math.abs(Duration.between(LocalDateTime.now(), state.lastupdate()).toMillis()) < 50);
	}

	@Test
	final void wrongId() {
		assertThrows(IllegalArgumentException.class, () -> new BooleanStateImpl(ID * -1, NAME, LocalDateTime.now()));
	}
	
	@Test
	final void assignNull() {
		state.assign(true);
		assertTrue(state.value());
		
		state.assign(null);
		
		assertFalse(state.value());
	}
	
}
