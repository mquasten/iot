package de.mq.iot.state.support;

import java.util.Optional;

interface MinMaxRange {

	Optional<Double> getMin();
	Optional<Double> getMax();

}