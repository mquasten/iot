package de.mq.iot.calendar.support;



import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot.calendar.Day;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@RepositoryDefinition(domainClass = AbstractDay.class, idClass = String.class)
public interface DayRepository {

	Mono<Day<?>> save(final Day<?> specialday); 
	
	Mono<Day<?>> delete(final Day<?> specialday); 
	
	Flux<Day<?>> findAll();
	
	@Query("{  \"dayGroup.name\": { $eq: ?0 } }")
	Flux<Day<?>> findByDayGroupName(final String groupName); 
	
}
