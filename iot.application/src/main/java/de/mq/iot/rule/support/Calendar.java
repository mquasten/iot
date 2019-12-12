package de.mq.iot.rule.support;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map.Entry;
import java.util.AbstractMap;
import java.util.Optional;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import de.mq.iot.calendar.SpecialdayService.DayType;

class Calendar implements ValidFieldValues{
	
	enum  Time {
		Winter(1),
		Summer(2);
		
		
		
		private final int offset;
		Time(int offset){
			this.offset=offset;
		}
		
		String key() {
			return name().toUpperCase();
		}
		
		int offset() {
			return offset;
		}
	}
	
	private DayType dayType;
	
	private LocalDate date;
	
	private Time time;
	
	@Nullable
	private Double temperature;
	
	@Nullable
	private Entry<String,String> events;
	
	final LocalDate date() {
		Assert.notNull(date, "Date not set.");
		return date;
	}
	
	
	final Month month() {
		return date().getMonth();
	}
	
	final int dayOfYear() {
		return date().getDayOfYear();
	}

	final void assignDate(final LocalDate date) {
		this.date = date;
	}

	final boolean workingDay() {
		Assert.notNull(dayType, "DayType not set.");
		return dayType!=DayType.NonWorkingDay;
		
	}

	final void assignDayType(final DayType dayType) {
		this.dayType = dayType;
	}

	final DayType dayType() {
		return dayType;
	}
	
	final void assignTime(final Time time) {
		this.time=time;
	}
	
	final Time time() {
		Assert.notNull(time, "Time not set.");
		return time;
	}
	
	final Optional<Double> temperature() {
		return Optional.ofNullable(temperature);
		
	}

	final void assignTemperature(final double temperature) {
		this.temperature=temperature;
	}
	
	final void  assignEvents(final String key, String events) {
		this.events=new AbstractMap.SimpleImmutableEntry<>(key, events);
	}


	
	final Optional<Entry<String,String>> events() {
		return	 Optional.ofNullable(events);
		
	}
}
