package de.mq.iot.authentication.support;

import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot.authentication.Authentication;
import reactor.core.publisher.Mono;
@RepositoryDefinition(domainClass = UserAuthenticationImpl.class, idClass = String.class)
public interface AuthenticationRepository {
	
		Mono<Authentication> findByUsername(final String username );
		
		Mono<Authentication> save(final Authentication resourceIdentifier); 
		


}
