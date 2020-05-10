package de.mq.iot.synonym;

import java.util.Collection;



/**
 * Service for Synonyms
 * @author Admin
 *
 */
public interface SynonymService {

	
	/**
	 * All synonym for devices
	 * @return Collection with synonyms for devices
	 */
	Collection<Synonym> deviveSynonyms();

	/**
	 * update/persist Synonym 
	 * @param synonym the should be updated
	 */
	void save(Synonym synonym);

}