package de.mq.iot.openweather.support;

import java.util.Collection;

import de.mq.iot.resource.ResourceIdentifier;

interface WeatherRepository {

	Collection<MeteorologicalData> forecast(final ResourceIdentifier resourceIdentifier);

	MeteorologicalData weather(final ResourceIdentifier resourceIdentifier);

}