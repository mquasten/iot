package de.mq.iot.synonym;
/**
 * A synonym for a device etc. to be independent from technical names and ids
 * @author Admin
 *
 */
public interface Synonym {
	
	public enum  Type{
			Devive;
		}


	/**
	 * The name, key, id of the Synonym
	 * @return the key id 
	 */
	String key();

	/**
	 * The Synonym for the key
	 * @return the synonym
	 */
	String value();

	/**
	 * The Type of the Synonym
	 * @return synonym type
	 */
	Type type();

	/**
	 * A description for the synonym
	 * @return the descption for the synonym
	 */
	String description();

}