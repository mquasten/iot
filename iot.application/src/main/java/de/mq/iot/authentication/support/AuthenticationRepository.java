package de.mq.iot.authentication.support;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.Authority;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@RepositoryDefinition(domainClass = UserAuthenticationImpl.class, idClass = String.class)
public interface AuthenticationRepository {
	
		Mono<Authentication> findByUsername(final String username );
		
		Flux<Authentication> findAll();
		
		Mono<Authentication> save(final Authentication resourceIdentifier);
		
		
		@Query("{ $and: [ { username: { $ne: ?0 } },{ authorities: { $in: [?1] } }  ] }")
		Flux<Authentication>  findByUsernameNotAndAuthority(final String username, final Authority authority);
		
		
		Mono<Authentication> delete(final Authentication location);


}
