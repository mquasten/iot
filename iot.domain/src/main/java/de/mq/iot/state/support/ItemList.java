package de.mq.iot.state.support;

import java.util.Collection;
import java.util.Map.Entry;

public interface ItemList {
	
	Collection<Entry<Integer, String>> items();
	
	void assign(final String value);

	String stringValue();


	boolean hasLabel(String stringValue); 

}
