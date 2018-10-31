package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.mq.iot.state.Room;
import de.mq.iot.state.State;

class RoomTest {
	
	private static final long ID = 4711L;

	private static final String NAME = "HMW-LC-Bl1-DR NEQ1415509:3";
	
	private static final String DESCRIPTION = "Fenster links";
	
	
	private final Room room = new RoomImpl(ID, String.format("%s_%s", NAME, DESCRIPTION));
	
	private final State<Double> state = new DoubleStateImpl(ID, NAME, LocalDateTime.now());

	
	@Test
	void name() {
		assertEquals(NAME, room.name());
	}
	
	@Test
	void description() {
		assertEquals(DESCRIPTION, room.description());
	}
	
	@Test
	void id() {
		assertEquals(ID, room.id());
	}
	
	@Test
	void createNameOnly() {
		 final Room room = new RoomImpl(ID, NAME);
		 assertEquals(NAME, room.name());
		 assertEquals("", room.description());
	}
	@Test
	void states() {
		assertEquals(0, room.states().size());
	
		((RoomImpl) room).assign(state);
		assertEquals(1,  room.states().size());
		assertEquals(Optional.of(state), room.states().stream().findAny());
	}

}
