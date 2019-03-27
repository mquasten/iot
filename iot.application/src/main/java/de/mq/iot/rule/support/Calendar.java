package de.mq.iot.rule.support;

import java.time.LocalDate;

import org.springframework.util.Assert;

public class Calendar {
	
	private Boolean workingDay;
	
	private LocalDate date;
	
	public LocalDate date() {
		Assert.notNull(date, "Date not set.");
		return date;
	}

	public void assignDate(final LocalDate date) {
		this.date = date;
	}

	public boolean workingDay() {
		Assert.notNull(workingDay, "Workingday not set.");
		return workingDay;
	}

	public void assignWorkingDay(final boolean workingDay) {
		this.workingDay = workingDay;
	}

	
	

}
