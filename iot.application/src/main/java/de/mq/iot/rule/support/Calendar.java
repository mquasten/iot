package de.mq.iot.rule.support;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

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
	
	private Boolean workingDay;
	
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
		Assert.notNull(workingDay, "Workingday not set.");
		return workingDay;
	}

	final void assignWorkingDay(final boolean workingDay) {
		this.workingDay = workingDay;
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
	
	final void assignEvents(final Entry<String,String> events) {
		eventsMandataryGuard(events);
		this.events=events;
	}


	private void eventsMandataryGuard(final Entry<?,?> events) {
		Assert.notNull(events, "Events is mandatory.");
	}
	
	final Optional<Entry<String,String>> events() {
		return Optional.ofNullable(events);
	}
}
