package de.mq.iot.calendar.support;

import java.time.YearMonth;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;


@Document(collection=GaussDayImpl.DAY_COLLECTION_NAME)
 abstract class AbstractDay<T>   implements Day<T>{
	 static final int FREQUENCY_ONCE_PER_YEAR = 1;

	@Id
	private String id;
	
	@Transient
	private final Supplier<YearMonth> yearMonth = () -> YearMonth.now();
	
	AbstractDay(final DayGroup dayGroup) {
		Assert.notNull(dayGroup, "DayGroup is required.");
		this.dayGroup = dayGroup;
	}

	AbstractDay() {
		dayGroup=null;
	}
	private final DayGroup dayGroup;
	
	
	public String id() {
		return id;
	}
	
	public DayGroup dayGroup() {
		return dayGroup;
	}
	
	

	@Override
	public int compareTo(final Day<?> other) {
		final int result = dayGroup.priority() - other.dayGroup().priority(); 
		if (result==0) {
			return frequency() -other.frequency();
		}
		return result;
	}
	
	YearMonth yearMonth() {
		return yearMonth.get();
	}
	
	public int frequency() {
		return FREQUENCY_ONCE_PER_YEAR;
	}
	
	void assign(final UUID id) {
		Assert.notNull(id, "Id is required.");
		this.id=id.toString();
	}

	

	@Override
	public int hashCode() {
		return id != null ? id.hashCode(): super.hashCode(); 
		
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof AbstractDay)) {
			 return super.equals(other);
			
		}
		if(id == null) {
			return  super.equals(other);
		}
		final AbstractDay<?> day=  (AbstractDay<?>) other;
		if(day.id==null) {
			return  super.equals(other);
		}
		return id.equals(day.id);
	}
	
	final long mostSigBits(final int key) {
		Assert.hasText(dayGroup.name(), "DayGroup name is required.");
		return 100L*dayGroup.name().hashCode()+key;
	}
	
}
