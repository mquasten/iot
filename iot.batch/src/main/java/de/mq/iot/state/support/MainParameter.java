package de.mq.iot.state.support;

public @interface MainParameter {
	
	String name() ; 
	
	String defaultValue() default "" ; 

}
