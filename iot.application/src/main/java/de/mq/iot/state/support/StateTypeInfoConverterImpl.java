package de.mq.iot.state.support;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.mq.iot.state.State;
import de.mq.iot.state.StateService.DeviceType;
@Component("stateTypeInfoConverter")
class StateTypeInfoConverterImpl implements Converter<State<?>,DeviceType> {
	
	
	private final Map<Class<?>,DeviceType> mappings = new HashMap<>();
	
	StateTypeInfoConverterImpl() {
		mappings.put(Double.class, DeviceType.Level);
		mappings.put(Boolean.class, DeviceType.State);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public DeviceType convert(final State<?> state) {
		final ParameterizedType t = (ParameterizedType) state.getClass().getGenericSuperclass(); 
		final Class<?> clazz = (Class<?>) t.getActualTypeArguments()[0]; 
		Assert.isTrue(mappings.containsKey(clazz), String.format("Mapping not defined for State of %s" , clazz));
		return mappings.get(clazz);
	}

}
