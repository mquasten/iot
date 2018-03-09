package de.mq.iot.state.support;





import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.Assert;



class BooleanStateConverter  implements StateConverter<Boolean>{
	private final ConversionService conversionService = new  DefaultConversionService() ; 
	
	
	@Override
	public State<Boolean> convert(final Map<String, String> values) {
		Assert.hasText(values.get(StateConverter.KEY_ID), "Id is mandatory."); 
		Assert.hasText(values.get(StateConverter.KEY_NAME), "Name is mandatory.");
		Assert.hasText(values.get(StateConverter.KEY_VALUE), "Value is mandatory");
		Assert.hasText(values.get(StateConverter.KEY_TIMESTAMP), "Timestamp is mandatory");
		Assert.hasText(values.get(StateConverter.KEY_TYPE), "Type is mandatory");
		final Long id = conversionService.convert(values.get(StateConverter.KEY_ID), Long.class);
		final LocalDateTime lastUpdate = LocalDateTime.ofInstant(Instant.ofEpochSecond(conversionService.convert(values.get(StateConverter.KEY_TIMESTAMP), Long.class)), ZoneOffset.UTC);
		
		final String name = values.get(StateConverter.KEY_NAME);
		final Boolean value =conversionService.convert(values.get(StateConverter.KEY_VALUE), Boolean.class);
		
		
	
		
		
		
		
		
		
		return new BooleanStateImpl(id, name, value,lastUpdate);
	}

	@Override
	public String key() {
		return "2";
	}

}
