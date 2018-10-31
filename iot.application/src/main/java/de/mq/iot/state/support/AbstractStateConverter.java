package de.mq.iot.state.support;






import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import de.mq.iot.state.State;



abstract class AbstractStateConverter<T>  implements StateConverter<T> {
	private final ConversionService conversionService;
	
	AbstractStateConverter(final ConversionService conversionService) {
		this.conversionService=conversionService;
	}
	
	
	ConversionService conversionService() {
		return conversionService;
	}

	
	
	@Override
	public State<T> convert(final Map<String, String> values) {
		Assert.hasText(values.get(StateConverter.KEY_ID), "Id is mandatory."); 
		Assert.hasText(values.get(StateConverter.KEY_NAME), "Name is mandatory.");
		Assert.hasText(values.get(StateConverter.KEY_TIMESTAMP), "Timestamp is mandatory");
		Assert.hasText(values.get(StateConverter.KEY_TYPE), "Type is mandatory");
		final Long id = conversionService().convert(values.get(StateConverter.KEY_ID), Long.class);
		final LocalDateTime lastupdate = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000*Long.valueOf(values.get(StateConverter.KEY_TIMESTAMP))), TimeZone.getDefault().toZoneId());
		final String name = values.get(StateConverter.KEY_NAME);
		
		return createState((State<T>) BeanUtils.instantiateClass(constructor(), id, name, lastupdate), Collections.unmodifiableMap(values) ); 
	}

	 abstract State<T> createState(final State<T> state, final Map<String,String> values );


	 abstract Class<? extends State<T>> target();
	

	 
	 private Constructor<? extends State<T>> constructor() {
		try {
			return target().getDeclaredConstructor(long.class, String.class, LocalDateTime.class);
		} catch (final Exception e) {
			throw new IllegalStateException();
		}
	 }
	 
	 void setStateField(final String name, final State<Double> state, final Double min) {
			final Field field = ReflectionUtils.findField(target(), name);
			Assert.notNull(field , String.format("Field '%s' is required in '%s'.", name, state.getClass().getName()));
			field.setAccessible(true);
			ReflectionUtils.setField(field, state, min);
		}
}
