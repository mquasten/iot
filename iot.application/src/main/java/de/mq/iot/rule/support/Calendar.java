package de.mq.iot.rule.support;

import java.time.LocalDate;
import java.time.Month;

import org.springframework.util.Assert;

class Calendar {
	
	enum  Time {
		Winter,
		Summer;
		
		String key() {
			return name().toUpperCase();
		}
	}
	
	private Boolean workingDay;
	
	private LocalDate date;
	
	private Time time;
	
	final LocalDate date() {
		Assert.notNull(date, "Date not set.");
		return date;
	}
	
	
	final Month month() {
		return date().getMonth();
	}
	
	final int dayOfYear() {
		return date.getDayOfYear();
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
	
	final boolean valid() {
		return workingDay!=null&&date!=null&&time!=null;
		
	}

}
