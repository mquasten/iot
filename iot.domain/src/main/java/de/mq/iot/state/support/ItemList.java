package de.mq.iot.state.support;

import java.util.Collection;
import java.util.Map.Entry;
@FunctionalInterface
public interface ItemList {
	
	Collection<Entry<Integer, String>> items();

}
