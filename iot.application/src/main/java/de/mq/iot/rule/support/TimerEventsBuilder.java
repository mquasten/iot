package de.mq.iot.rule.support;





import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

;

public class TimerEventsBuilder{
	


	enum Key {
		T0,
		T1,
		T2,
		T3,
		T4,
		T5,
		T6,
		T7,
		T8,
		T9;

	}
	
private Map<Key,LocalTime> events = new HashMap<>();
	


   
	TimerEventsBuilder with(final Key key, LocalTime localTime){
		Assert.notNull(key, "Key is required.");
		Assert.notNull(localTime, "Value is required");
		
	
		
		events.put(key, localTime);
		
	
		return this;
	}
	
	
	
	String build() {
		
		
		
		
		
		final StringBuilder builder = new StringBuilder();
		

		
		
		events.keySet().stream().sorted().forEach(key -> {
			
		
			
		
				
				
			if( builder.length()!=0) {
				builder.append(";");
			}
			
			
			builder.append(String.format("%s:%s", key,  events.get(key).getHour() + 0.01 * (events.get(key).getMinute()  )));
			
			
		});
		
 
	
		return builder.toString();
		
		
	}
	
	

	

	

	
	


static TimerEventsBuilder newBuilder() {
		return new TimerEventsBuilder();
	}

}
