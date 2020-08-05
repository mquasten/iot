package de.mq.iot.calendar.support;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import de.mq.iot.calendar.DayGroup;

@Document(collection=GaussDayImpl.DAY_COLLECTION_NAME)
class FixedDayImpl extends AbstractDay<LocalDate> {

	static final int KEY_PREFIX = 2;

	static final String TO_STRING_PATTERN = "Fixed: month=%s, dayOfMonth=%s, dayGroup=%s";
	
	private final Integer month;
	private final Integer dayOfMonth;
	FixedDayImpl(final DayGroup dayGroup, final MonthDay monthDay) {
		super(dayGroup);
		Assert.notNull(monthDay, "MonthDay is required.");
		assign(new UUID(KEY_PREFIX,100* monthDay.getMonthValue() + monthDay.getDayOfMonth()));
		month=monthDay.getMonthValue();
		dayOfMonth=monthDay.getDayOfMonth();
	}
	
	@SuppressWarnings("unused")
	private FixedDayImpl() {
		super();
		month=null;
		dayOfMonth=null;
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

	
}
