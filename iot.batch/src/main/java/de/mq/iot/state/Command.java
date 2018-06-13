package de.mq.iot.state;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	String name();
	String[] arguments();
	int order() default 0; 
}
