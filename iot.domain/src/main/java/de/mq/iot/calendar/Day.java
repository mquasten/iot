package de.mq.iot.calendar;

import java.time.LocalDate;

public interface Day<T>  extends  Comparable<Day<?>> {
	
	String id();
	
	DayGroup dayGroup();

	boolean evaluate(final LocalDate date);

	T value();
	int frequency();
	
}
