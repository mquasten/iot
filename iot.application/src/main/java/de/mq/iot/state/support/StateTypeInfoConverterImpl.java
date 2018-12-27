package de.mq.iot.state.support;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.mq.iot.state.State;
@Component("stateTypeInfoConverter")
class StateTypeInfoConverterImpl implements Converter<State<?>,String> {
	
	static final String STATE = "STATE";
	static final String LEVEL = "LEVEL";
	private final Map<Class<?>,String> mappings = new HashMap<>();
	
	StateTypeInfoConverterImpl() {
		mappings.put(Double.class, LEVEL);
		mappings.put(Boolean.class, STATE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public String convert(final State<?> state) {
		final ParameterizedType t = (ParameterizedType) state.getClass().getGenericSuperclass(); 
		final Class<?> clazz = (Class<?>) t.getActualTypeArguments()[0]; 
		Assert.isTrue(mappings.containsKey(clazz), String.format("Mapping not defined for State of %s" , clazz));
		return mappings.get(clazz);
	}

}
