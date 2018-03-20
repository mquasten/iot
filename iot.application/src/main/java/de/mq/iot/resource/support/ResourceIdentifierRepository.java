package de.mq.iot.resource.support;

import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.resource.ResourceIdentifier.ResourceType;
import reactor.core.publisher.Mono;


@RepositoryDefinition(domainClass = ResourceIdentifierImpl.class, idClass = ResourceType.class)
interface ResourceIdentifierRepository   {

	Mono<ResourceIdentifier> findById(final ResourceType resourceType );
	
	Mono<ResourceIdentifier> save(final ResourceIdentifier resourceIdentifier); 
	
}
