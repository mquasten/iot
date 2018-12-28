package de.mq.iot.state.support;

import org.springframework.core.convert.converter.Converter;

import de.mq.iot.state.State;

interface StateToStringConverter  extends   Converter<State<Object>,String>  {

	String convert(State<Object> state);
	
	String convertValue(Object state);

}