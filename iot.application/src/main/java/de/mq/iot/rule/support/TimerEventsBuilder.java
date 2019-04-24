package de.mq.iot.rule.support;



import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
;

public class TimerEventsBuilder{
	
	static final String EVENT_EXECUTIONS_VARIABLE_NAME = "EventExecutions";

	static final String DAILY_EVENTS_VARIABLE_NAME = "DailyEvents";

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
	
	private String name;
	

	
	TimerEventsBuilder with(final Key key, LocalTime localTime){
		Assert.notNull(key, "Key is required.");
		Assert.notNull(localTime, "Value is required");
		
		Assert.isTrue(!events.containsKey(key), "Envent already assigned.");
		
		events.put(key, localTime);
		
	
		return this;
	}
	
	TimerEventsBuilder with(final boolean updateMode) {
		Assert.isNull(name, "Name already assigned.");
		this.name = updateMode ? EVENT_EXECUTIONS_VARIABLE_NAME : DAILY_EVENTS_VARIABLE_NAME;
		return this;
	}
	
	
	Entry<String,String> build() {
		
		Assert.isTrue(!CollectionUtils.isEmpty(events), "At least one event is required.");
		 
		Assert.hasText(name , "Name must be assigned.");
		
		final StringBuilder builder = new StringBuilder();
		
		final LocalTime[] lastEvent = new LocalTime[] {LocalTime.MIDNIGHT};
		final LocalTime time = name.equals(EVENT_EXECUTIONS_VARIABLE_NAME) ? LocalTime.now() : LocalTime.MIDNIGHT;
		events.keySet().stream().filter(key -> events.get(key).isAfter(time)).sorted().forEach(key -> {
			boolean test = events.get(key).getNano() >= lastEvent[0].getNano();
			
			Assert.isTrue(test, "Timerevents must be chronological.");
			
			if( builder.length()!=0) {
				builder.append(";");
			}
			builder.append(String.format("%s:%s", key,  events.get(key).getHour() + 0.01 * (events.get(key).getMinute()  )));
		});
		
		
		return new AbstractMap.SimpleImmutableEntry<>(name, builder.toString());
		
	}
	
	static TimerEventsBuilder newBuilder() {
		return new TimerEventsBuilder();
	}

}
