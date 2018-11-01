package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.mq.iot.state.Room;
import de.mq.iot.state.State;

class RoomTest {
	
	private static final long ID = 4711L;

	private static final String NAME = "Schlafzimmer";
	

	
	
	private final Room room = new RoomImpl(NAME);
	
	private final State<Double> state = new DoubleStateImpl(ID, NAME, LocalDateTime.now());

	
	@Test
	void name() {
		assertEquals(NAME, room.name());
	}
	
	
	
	
	@Test
	void states() {
		assertEquals(0, room.states().size());
	
		((RoomImpl) room).assign(state);
		assertEquals(1,  room.states().size());
		assertEquals(Optional.of(state), room.states().stream().findAny());
	}

}
