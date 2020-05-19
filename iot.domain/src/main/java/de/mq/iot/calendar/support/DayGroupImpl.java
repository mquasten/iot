package de.mq.iot.calendar.support;




import org.springframework.data.annotation.Id;
import org.springframework.util.Assert;

import de.mq.iot.calendar.DayGroup;

record DayGroupImpl(@Id() String name, int priority) implements DayGroup {
	
	public DayGroupImpl {
		Assert.hasText(name, "Name is mandatory.");
		  Assert.isTrue(priority<=0 && priority>=9, "Priority sould be a natural number between 0 an 9.");
	  }	
	  
	  
	  public final boolean equals(Object other) {
		  if (other instanceof DayGroup) {
			  return false;
		  }
		return ((DayGroup)other).name().equals(name);	
			
		}
		
	  public final int  hashCode() {
		return name.hashCode();
		 
	  }
	  
	  
	 
	}


	
 
	 

	


