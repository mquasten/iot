package de.mq.iot.openweather.support;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map.Entry;

import de.mq.iot.resource.ResourceIdentifier;

interface WeatherRepository {

	Collection<Entry<LocalDate, Double>> forecast(final ResourceIdentifier resourceIdentifier);

	Entry<LocalDateTime, Double> weather(final ResourceIdentifier resourceIdentifier);

}