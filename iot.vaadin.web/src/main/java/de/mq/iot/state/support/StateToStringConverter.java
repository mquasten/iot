package de.mq.iot.state.support;

import org.springframework.core.convert.converter.Converter;

import de.mq.iot.state.State;

interface StateToStringConverter  extends   Converter<State<?>,Object>  {

	String convert(State<?> state);
	
	Object convertValue(Object state);

}