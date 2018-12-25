package de.mq.iot.openweather.support;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.mq.iot.openweather.MeteorologicalData;
import de.mq.iot.openweather.MeteorologicalDataService;
import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.resource.ResourceIdentifier.ResourceType;
import de.mq.iot.resource.support.ResourceIdentifierRepository;

@Service
class MeteorologicalDataServiceImpl implements MeteorologicalDataService {
	
	
	private final WeatherRepository weatherRepository;
	
	private final ResourceIdentifierRepository resourceIdentifierRepository;
	
	private final  Duration duration;
	
	@Autowired
	MeteorologicalDataServiceImpl(final WeatherRepository weatherRepository, final ResourceIdentifierRepository resourceIdentifierRepository, @Value("${mongo.timeout:500}") final Long timeout) {
		this.weatherRepository = weatherRepository;
		this.resourceIdentifierRepository = resourceIdentifierRepository;
		this.duration=Duration.ofMillis(timeout);
	}

	@Override
	public final MeteorologicalData forecastMaxTemperature(final LocalDate localDate) {
		final ResourceIdentifier resourceIdentifier = resourceIdentifierRepository.findById(ResourceType.OpenWeather).block(duration);
		final Collection<MeteorologicalData>  results = meteorologicalData(localDate, resourceIdentifier);
		
		
		return results.stream().max((c1, c2) ->(int)  Math.signum(c1.temperature() - c2.temperature()) ).orElseThrow(() -> new IllegalArgumentException("MeteorologicalData expected.")); 
	}

	private Collection<MeteorologicalData> meteorologicalData(final LocalDate localDate, final ResourceIdentifier resourceIdentifier) {
		final Collection<MeteorologicalData> results = new ArrayList<>(weatherRepository.forecast(resourceIdentifier).stream().filter(meteorologicalData -> meteorologicalData.hasDate(localDate)).collect(Collectors.toList()));
		
		if( LocalDate.now(MapToMeteorologicalDataConverterImpl.ZONE_OFFSET).equals(localDate)) {
			results.add(weatherRepository.weather(resourceIdentifier));
		}
		
		return results; 
	}

	@Override
	public Collection<MeteorologicalData> forecasts() {
		final ResourceIdentifier resourceIdentifier = resourceIdentifierRepository.findById(ResourceType.OpenWeather).block(duration);
		final Collection<MeteorologicalData> results = new ArrayList<>();
		results.add(weatherRepository.weather(resourceIdentifier));
		results.addAll(weatherRepository.forecast(resourceIdentifier));
		return results;
	}

}
