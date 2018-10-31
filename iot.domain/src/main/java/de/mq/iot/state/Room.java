package de.mq.iot.state;

import java.util.Collection;

public interface Room {

	long id();

	String name();

	String description();

	Collection<State<?>> states();

}