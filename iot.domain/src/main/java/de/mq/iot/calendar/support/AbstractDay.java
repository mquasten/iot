package de.mq.iot.calendar.support;

import java.time.YearMonth;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.util.Assert;

import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;



 abstract class AbstractDay<T>   implements Day<T>{
	@Id
	private final String id;
	
	@Transient
	private final Supplier<YearMonth> yearMonth = () -> YearMonth.now();
	
	AbstractDay(final DayGroup dayGroup, final int key) {
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
	public int compareTo(final Day<T> other) {
		return dayGroup.priority() - other.dayGroup().priority(); 
		
	}
	
	YearMonth yearMonth() {
		return yearMonth.get();
	}
	
	
}
