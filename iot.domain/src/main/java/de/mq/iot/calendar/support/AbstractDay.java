package de.mq.iot.calendar.support;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.util.Assert;

import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;


 abstract class AbstractDay   implements Day{
	@Id
	private final String id;
	
	AbstractDay(final DayGroup dayGroup, final long key) {
		Assert.notNull(key, "Key is required.");
		Assert.notNull(dayGroup, "DayGroup is required.");
		this.id = new UUID(getClass().hashCode(), key).toString();
		this.dayGroup = dayGroup;
	}

	private final DayGroup dayGroup;
	
	
	public String id() {
		return id;
	}
	
	public DayGroup dayGroup() {
		return dayGroup;
	}
	
	

	@Override
	public int compareTo(final Day other) {
		return dayGroup.priority() - other.dayGroup().priority(); 
		
	}
	
	
}
