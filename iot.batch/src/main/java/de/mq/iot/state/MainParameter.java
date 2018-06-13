package de.mq.iot.state;



public @interface MainParameter {
	
	String name() ; 
	
	boolean hasArg() default true;
	
	String defaultValue() default "" ; 
	
	String desc();

}
