package de.mq.iot.state.support;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Main {

	MainParameter[] parameters() ;
	
	String name();
}
