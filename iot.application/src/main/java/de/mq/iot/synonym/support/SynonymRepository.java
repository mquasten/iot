package de.mq.iot.synonym.support;

import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot.synonym.Synonym;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * RepositoryOperations Synonym
 * @author Admin
 *
 */
@RepositoryDefinition(domainClass = SynonymImpl.class, idClass = String.class)
public interface SynonymRepository {
	
			/**
			 * All Synonyms for a type
			 * @param type the type of the Synonyms, that will be searched for
			 * @return Flux of  synonyms
			 */
			Flux<Synonym> findByType(final Synonym.Type type );
			
			/**
			 * Save the Synonym, update it, make it persistent 
			 * @param synonym the synonym that should be stored
			 * @return Mono of synonym
			 */
			Mono<Synonym> save(final Synonym synonym); 
			
			/**
			 * Delete a Synonym by its key
			 * @param key the Key of the Synonym that should be deleted
			 * @return  Mono of synonym
			 */
			Mono<Synonym> deleteByKey(final String key);
			

}
