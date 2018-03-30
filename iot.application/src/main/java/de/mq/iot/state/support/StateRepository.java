package de.mq.iot.state.support;

import java.util.Collection;
import java.util.Map;

import de.mq.iot.resource.ResourceIdentifier;

interface StateRepository {
	
	
	
	Collection<Map<String, String>> findStates(final ResourceIdentifier resourceIdentifier);


	void changeState(final ResourceIdentifier resourceIdentifier, State<?> state);

}