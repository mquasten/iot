package de.mq.iot.rule;

import java.util.Date;

public record Person(  
	    String firstName,
	    String lastName,
	    int age,
	    String address,
	    Date birthday
	){}