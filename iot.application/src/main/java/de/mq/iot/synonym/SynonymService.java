package de.mq.iot.synonym;

import java.util.Collection;

import de.mq.iot.synonym.Synonym;

/**
 * Service for Synonyms
 * @author Admin
 *
 */
public interface SynonymService {

	
	/**
	 * All synonym for the given Type
	 * @param type the Type of the Synonyms
	 * @return Collection with synonyms for the given type
	 */
	Collection<Synonym> synonyms(final Synonym.Type type);

}