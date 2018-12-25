package de.mq.iot.state.support;

import java.util.Collection;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;

import de.mq.iot.state.State;

interface StateConverter<T>  extends Converter<Map<String,String> , State<T>>{
	
	static final String KEY_TIMESTAMP = "timestamp";
	static final String KEY_VALUE = "value";
	static final String KEY_TYPE = "type";
	static final String KEY_NAME = "name";
	static final String KEY_ID = "ise_id";
	
	static final String KEY_MIN = "min";
	static final String KEY_MAX = "max";
	static final String KEY_VALUE_LIST= "value_list";
	
	 Collection<String> keys(); 

}
 