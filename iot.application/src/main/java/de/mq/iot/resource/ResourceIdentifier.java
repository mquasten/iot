package de.mq.iot.resource;

import java.util.Map;

public interface ResourceIdentifier {
	
	public enum ResourceType{
		XmlApiSysVarlist
	}
	
	ResourceIdentifier id();
	
	String uri();
	
	Map<String,String> parameters();

}
