package de.mq.iot.state.support;

import org.springframework.core.convert.converter.Converter;

import de.mq.iot.state.State;

interface StateToStringConverter  extends   Converter<State<?>,String>  {

	String convert(State<?> state);
	
	String convertValue(Object state);

}