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
	
private boolean updateMode=false;
private LocalTime minEventTime=LocalTime.now();
   
	TimerEventsBuilder with(final Key key, LocalTime localTime){
		Assert.notNull(key, "Key is required.");
		Assert.notNull(localTime, "Value is required");
		
	
		
		events.put(key, localTime);
		
	
		return this;
	}
	TimerEventsBuilder with(final boolean updateMode){
		this.updateMode=updateMode;
		return this;
	}
	
	TimerEventsBuilder withMinEventTime(LocalTime minEventTime) {
		
		Assert.notNull(minEventTime, "Time is required.");
		this.minEventTime=minEventTime;
		return this;
		
	}
	
	
	String build() {
		
		
		
		
		
		final StringBuilder builder = new StringBuilder();
		

		
		
		events.keySet().stream().sorted().forEach(key -> {
			
		
			
		
				
				
			if( builder.length()!=0) {
				builder.append(";");
			}
			
			
			if(  updateMode ) {
				updateMode(builder, key);
			} else {
				builder.append(String.format("%s:%s", key,  events.get(key).getHour() + 0.01 * (events.get(key).getMinute()  )));
			}
					
					
		
			
			
			
		});
		
 
	
		return builder.toString();
		
		
	}
	private void updateMode(final StringBuilder builder, final Key key) {
		if( (events.get(key).compareTo(minEventTime)>0)) {
			builder.append(String.format("%s:%s", key,  events.get(key).getHour() + 0.01 * (events.get(key).getMinute()  )));
		}
	}
	
	

	

	

	
	


static TimerEventsBuilder newBuilder() {
		return new TimerEventsBuilder();
	}

}
