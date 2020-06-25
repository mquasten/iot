package de.mq.iot.calendar.support;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;


@Service
class DayServiceImpl implements DayService {
	
	
	
	private final DayRepository dayRepository;
	private final Duration duration;
	private final DayGroup defaultDayGroup;
	
	@Autowired
	DayServiceImpl(final DayRepository dayRepository,  final DayGroup defaultDayGroup, @Value("${mongo.timeout:500}") final Integer timeout) {
		this.dayRepository=dayRepository;
		this.defaultDayGroup=defaultDayGroup;
		this.duration=Duration.ofMillis(timeout);
	}

	@Override
	public DayGroup dayGroup(final LocalDate date) {
		return dayRepository.findAll().collectList().block(duration).stream().filter(day -> day.evaluate(date)).sorted().findFirst().map(day -> day.dayGroup()).orElse(defaultDayGroup);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Day<LocalDate>> localDateDaysBeforeOrEquals(final LocalDate date) {
		return dayRepository.findAll().collectList().block(duration).stream().filter(day -> day.getClass().equals(LocalDateDayImpl.class)).filter( day  -> ! ((LocalDate) day.value()).isAfter(date)).map(day -> (Day<LocalDate>)day).collect(Collectors.toSet());
		
	}
	@Override
	public void save(final Day<?> day) {
		dayRepository.save(day).block(duration);
	}
	
	@Override
	public void delete(final Day<?> day) {
		dayRepository.delete(day).block(duration);
	}
	
	

}
