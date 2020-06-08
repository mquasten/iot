package de.mq.iot.calendar.support;

import java.time.LocalDate;
import java.time.MonthDay;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import de.mq.iot.calendar.DayGroup;

@Document(collection=GaussDayImpl.DAY_COLLECTION_NAME)
class FixedDayImpl<Localdate> extends AbstractDay<LocalDate, MonthDay> {

	static final int KEY_PREFIX = 2;

	static final String TO_STRING_PATTERN = "Fixed: month=%s, dayOfMonth=%s, dayGroup=%s";
	
	private final Integer month;
	private final Integer dayOfMonth;
	FixedDayImpl(final DayGroup dayGroup, final MonthDay monthDay) {
		super(dayGroup, monthDay);
		Assert.notNull(monthDay, "MonthDay is required.");
		month=monthDay.getMonthValue();
		dayOfMonth=monthDay.getDayOfMonth();
	}

	@Override
	public final  boolean evaluate(final LocalDate date) {
		Assert.notNull(date, "Date is required.");
		return date.equals(LocalDate.of(date.getYear(), month, dayOfMonth));
		
	}

	@Override
	public final LocalDate value() {
		return LocalDate.of(yearMonth().getYear(), month, dayOfMonth);
		
	}

	
	@Override
	public final String toString() {
		return String.format(TO_STRING_PATTERN ,month, dayOfMonth, dayGroup().name());
	}

	@Override
	long keyPrefix() {
		return KEY_PREFIX;
	}
}
