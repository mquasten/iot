package de.mq.iot.state.support;

import java.util.Collection;
import java.util.Map;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.state.State;

interface StateRepository {
	
	
	
	Collection<Map<String, String>> findStates(final ResourceIdentifier resourceIdentifier);


	void changeState(final ResourceIdentifier resourceIdentifier, State<?> state);

}