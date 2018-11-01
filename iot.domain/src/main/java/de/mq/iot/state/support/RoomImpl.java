package de.mq.iot.state.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import de.mq.iot.state.Room;
import de.mq.iot.state.State;

class RoomImpl implements Room {

	private final String name;

	private final Collection<State<?>> states = new ArrayList<>();

	RoomImpl(final String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.state.support.Room#name()
	 */
	@Override
	public String name() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.state.support.Room#states()
	 */
	@Override
	public Collection<State<?>> states() {
		return Collections.unmodifiableCollection(states);
	}

	/**
	 * assign the state for a devices in the room
	 * 
	 * @param state
	 */
	void assign(final State<?> state) {
		this.states.add(state);
	}

}
