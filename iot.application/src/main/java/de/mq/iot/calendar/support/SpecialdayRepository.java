package de.mq.iot.calendar.support;

import java.util.List;

import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.support.SpecialdayImpl.Type;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@RepositoryDefinition(domainClass = SpecialdayImpl.class, idClass = String.class)
public interface SpecialdayRepository {

	Mono<Specialday> save(final Specialday specialday); 
	
	Flux<Specialday> findByTypeIn(final List<Type> types);
	
	Flux<Specialday> findByTypeAndYear(final Type type, final int year);
	
}
