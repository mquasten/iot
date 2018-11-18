package de.mq.iot.state;

import java.util.Collection;

public interface Room {

	String name();

	Collection<State<Double>> states();

}