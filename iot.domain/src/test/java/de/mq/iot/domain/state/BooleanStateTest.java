package de.mq.iot.domain.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class BooleanStateTest {

	private static final String NAME = "WorkingDay";
	private static final long ID = 19680528L;
	private final State<Boolean> state = new BooleanStateImpl(ID, NAME, true, LocalDateTime.now());

	@Test
	final void id() {
		assertEquals(ID, state.id());
	}

	@Test
	final void name() {
		assertEquals(NAME, state.name());
	}

	@Test
	final void value() {
		assertTrue(state.value());
	}

	@Test
	final void assignValue() {
		state.assign(false);

		assertFalse(state.value());
	}

	@Test
	final void lastUpdate() throws InterruptedException {
		assertTrue(Math.abs(Duration.between(LocalDateTime.now(), state.lastupdate()).toMillis()) < 5);
	}

	@Test()
	public final void wrongId() {
		assertThrows(IllegalArgumentException.class, () -> new BooleanStateImpl(ID * -1, NAME, true, LocalDateTime.now()));
	}
}
