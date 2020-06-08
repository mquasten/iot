package de.mq.iot.calendar.support;



import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.Specialday;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@RepositoryDefinition(domainClass = AbstractDay.class, idClass = String.class)
public interface DayRepository {

	Mono<Day<?>> save(final Day<?> specialday); 
	
	Mono<Specialday> delete(final Day<?> specialday); 
	
	Flux<Specialday> findAll();
	
	
	
}
