package de.mq.iot.resource.support;

import java.util.HashMap;
import java.util.Map;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.resource.ResourceIdentifier.ResourceType;

public interface TestResourceIdentifier {
	
	static final String HOST_KEY = "host";
	static final String HOST_VALUE = "192.168.2.102";
	static final String PORT_KEY = "port";
	static final String PORT_VALUE = "80";
	static final String URI = "http://{host}:{port}/addons/xmlapi/{resource}";

	public static ResourceIdentifier resourceIdentifier() {
		final ResourceIdentifier result = new ResourceIdentifierImpl(ResourceType.XmlApi,URI);
		final Map<String,String> parameters = new HashMap<>();
		
		parameters.put(HOST_KEY, HOST_VALUE);
		parameters.put(PORT_KEY, PORT_VALUE);
		
		result.assign(parameters);
		return result;
		
	}

}
