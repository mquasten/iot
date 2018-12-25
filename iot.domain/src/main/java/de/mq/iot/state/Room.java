package de.mq.iot.state;

import java.util.Collection;

public interface Room {

	String name();

	<T> Collection<State<T>> states();

}