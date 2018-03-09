package de.mq.iot.state.support;






import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.Assert;



abstract class AbstractStateConverter<T>  implements StateConverter<T> {
	private final ConversionService conversionService = new  DefaultConversionService() ; 
	
	
	ConversionService conversionService() {
		return conversionService;
	}

	@Override
	public State<T> convert(final Map<String, String> values) {
		Assert.hasText(values.get(StateConverter.KEY_ID), "Id is mandatory."); 
		Assert.hasText(values.get(StateConverter.KEY_NAME), "Name is mandatory.");
		Assert.hasText(values.get(StateConverter.KEY_VALUE), "Value is mandatory");
		Assert.hasText(values.get(StateConverter.KEY_TIMESTAMP), "Timestamp is mandatory");
		Assert.hasText(values.get(StateConverter.KEY_TYPE), "Type is mandatory");
		final Long id = conversionService.convert(values.get(StateConverter.KEY_ID), Long.class);
		final LocalDateTime lastupdate = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000*Long.valueOf(values.get(StateConverter.KEY_TIMESTAMP))), TimeZone.getDefault().toZoneId());
		final String name = values.get(StateConverter.KEY_NAME);
		return createState(id, lastupdate, name, values.get(StateConverter.KEY_VALUE)); 
	}

	 abstract State<T> createState(final Long id, final LocalDateTime lastupdate, final String name, final String value );

	

}
