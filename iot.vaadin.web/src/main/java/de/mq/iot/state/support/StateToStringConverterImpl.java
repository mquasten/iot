package de.mq.iot.state.support;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.util.Assert;

import de.mq.iot.state.State;

class StateToStringConverterImpl implements StateToStringConverter {
	
	private Map<Class<?>, Function<Object,Object>> mappings = new HashMap<>();
	
	StateToStringConverterImpl() {
		mappings.put(Double.class, value -> ""  + (int)  Math.round(((Double) value) * 100d));
		mappings.put(Boolean.class, value -> value   );
	}

	/* (non-Javadoc)
	 * @see de.mq.iot.state.support.StateToStringConverter#convert(de.mq.iot.state.State)
	 */
	@Override
	public String convert(final State<?> state) {
		final ParameterizedType t = (ParameterizedType) state.getClass().getGenericSuperclass(); 
		final Class<?> clazz = (Class<?>) t.getActualTypeArguments()[0]; 
		Assert.isTrue(mappings.containsKey(clazz), String.format("Mapping not defined for %s", clazz));
		
		return  mappings.get(clazz).apply(state.value()).toString();
		
	}

	
	public Object convertValue(final Object value) {
		Assert.isTrue(mappings.containsKey(value.getClass()), String.format("Mapping not defined for %s", value.getClass()));
		return mappings.get(value.getClass()).apply(value);
	}

	

}
