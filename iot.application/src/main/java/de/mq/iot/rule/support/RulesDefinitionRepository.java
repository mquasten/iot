package de.mq.iot.rule.support;



import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot.rule.RulesDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RepositoryDefinition(domainClass = RulesDefinitionImpl.class, idClass = RulesDefinition.Id.class)
public interface RulesDefinitionRepository   {

	Mono<RulesDefinition> findById(final RulesDefinition.Id id );
	
	Mono<RulesDefinition> save(final RulesDefinition  rulesDefinition); 
	
	Flux<RulesDefinition> findAll();
	
}
