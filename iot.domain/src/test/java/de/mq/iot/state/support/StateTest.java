package de.mq.iot.state.support;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.state.State;
class StateTest {
	
	@Test
	void function() {
		final State<?> state = Mockito.mock(State.class);
		assertEquals(Optional.empty(), state.function());
	}
	
}
