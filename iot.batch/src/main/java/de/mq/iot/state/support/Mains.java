package de.mq.iot.state.support;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Mains {
	Main[] value() ; 
	

}
