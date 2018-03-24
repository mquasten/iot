package de.mq.iot.resource;

import java.util.Map;

public interface ResourceIdentifier {
	
	public enum ResourceType{
		XmlApiSysVarlist
	}
	
	ResourceType id();
	
	String uri();
	
	Map<String,String> parameters();
	
	void assign(final Map<String,String> parameters);

}
