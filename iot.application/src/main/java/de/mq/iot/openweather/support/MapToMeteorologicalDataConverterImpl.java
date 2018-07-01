package de.mq.iot.openweather.support;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;


class MapToMeteorologicalDataConverterImpl implements Converter<Map<String,Object> , MeteorologicalData> {

	static final String DEGREES_WIND_VELOCITY_KEY = "deg";
	static final String AMOUNT_WIND_VELOCITY_KEY = "speed";
	static final String HIGHEST_TEMPERATURE_KEY = "temp_max";
	static final String LOWEST_TEMPERATURE_KEY = "temp_min";
	static final String TEMPERATURE_KEY = "temp";
	static final String DATETIME_KEY= "dt";
	static final String WIND_DATA_KEY = "wind";
	static final String MAIN_DATA_KEY = "main";
	
	
	
	@Override
	public MeteorologicalData convert(Map<String, Object> data) {
		
		mandatoryDataExistsGuard(data, DATETIME_KEY);
		mandatoryDataExistsGuard(data, MAIN_DATA_KEY);
		mandatoryDataExistsGuard(data, WIND_DATA_KEY);
		@SuppressWarnings("unchecked")
		final Map<String,?> mainData = (Map<String, ?>) data.get(MAIN_DATA_KEY);
		mandatoryDataExistsGuard(data, DATETIME_KEY );
		mandatoryDataExistsGuard(mainData, LOWEST_TEMPERATURE_KEY );
		mandatoryDataExistsGuard(mainData, TEMPERATURE_KEY );
		mandatoryDataExistsGuard(mainData, HIGHEST_TEMPERATURE_KEY );
		mandatoryDataExistsGuard(data, WIND_DATA_KEY );
		@SuppressWarnings("unchecked")
		final Map<String,?> windData = (Map<String, ?>) data.get(WIND_DATA_KEY);
		
		
		mandatoryDataExistsGuard(windData, AMOUNT_WIND_VELOCITY_KEY);
		mandatoryDataExistsGuard(windData, DEGREES_WIND_VELOCITY_KEY);
		return new MeteorologicalData(((Number) mainData.get(LOWEST_TEMPERATURE_KEY)).doubleValue(), ((Number) mainData.get(TEMPERATURE_KEY)).doubleValue(), ((Number) mainData.get(HIGHEST_TEMPERATURE_KEY)).doubleValue(), ((Number) windData.get(AMOUNT_WIND_VELOCITY_KEY)).doubleValue(), ((Number) windData.get(DEGREES_WIND_VELOCITY_KEY)).doubleValue(), Instant.ofEpochMilli(1000 * Long.valueOf(((Number) data.get(DATETIME_KEY)).intValue())).atZone(ZoneOffset.UTC).toLocalDateTime());
	}

	private void mandatoryDataExistsGuard(Map<String, ?> data, final String key ) {
		Assert.notNull(data.containsKey(key), String.format("%s s required", key));
		Assert.notNull(data.get(key),  String.format("%s s required", key));
	}

}
