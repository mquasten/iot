package de.mq.iot.calendar.support;

import java.time.LocalDate;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import de.mq.iot.calendar.DayGroup;
@Document(collection=GaussDayImpl.DAY_COLLECTION_NAME)
class LocalDateDayImpl extends AbstractDay<LocalDate,LocalDate> {

	static final int KEY_PREFIX = 3;

	static final String TO_STRING_PATTERN = "Date: localDate=%s, dayGroup=%s";
	
	private final Integer dayOfMonth;
	
	private final Integer month;
	
	private final Integer year;
	
	LocalDateDayImpl(DayGroup dayGroup, LocalDate date) {
		super(dayGroup, date);
		Assert.notNull(date, "LocalDate is required.");
		dayOfMonth=date.getDayOfMonth();
		month=date.getMonthValue();
		year=date.getYear();
		
	}

	@Override
	public final boolean evaluate(final LocalDate date) {
		
		return date.equals(value());
	}

	@Override
	public final LocalDate value() {
		return LocalDate.of(year, month, dayOfMonth);
	}
	
	@Override
	public final String toString() {
		return String.format(TO_STRING_PATTERN ,value(), dayGroup().name());
	}

	@Override
	long keyPrefix() {
		return KEY_PREFIX;
	}

}
