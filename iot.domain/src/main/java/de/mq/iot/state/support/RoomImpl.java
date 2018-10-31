package de.mq.iot.state.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.util.Assert;

import de.mq.iot.state.Room;
import de.mq.iot.state.State;

class RoomImpl implements Room {
	
	private static final String DELIMITER = "[|,;_]";

	private final long id;

	private final String name;
	
	private final String description;
	
	private final Collection<State<?>> states = new ArrayList<>();
	
	RoomImpl(final long id, final String name) {
		this.id = id;
		Assert.hasText(name, "Name is redired.");
		final String[] cols = name.split(DELIMITER,2);
		this.name = cols[0];
		description = cols.length > 1 ? cols[1] : "" ; 
	}


	/* (non-Javadoc)
	 * @see de.mq.iot.state.support.Room#id()
	 */
	@Override
	public long id() {
		return id;
	}


	/* (non-Javadoc)
	 * @see de.mq.iot.state.support.Room#name()
	 */
	@Override
	public String name() {
		return name;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.iot.state.support.Room#description()
	 */
	@Override
	public String description() {
		return description;
	}


	/* (non-Javadoc)
	 * @see de.mq.iot.state.support.Room#states()
	 */
	@Override
	public Collection<State<?>> states() {
		return Collections.unmodifiableCollection(states);
	}

	
	/**
	 * assign the state for a devices in the room
	 * @param state
	 */
	void assign(final State<?> state) {
		this.states.add(state);
	}
	
}
