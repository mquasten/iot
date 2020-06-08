package de.mq.iot.calendar.support;


import java.time.LocalDate;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import de.mq.iot.calendar.DayGroup;

@Document(collection=GaussDayImpl.DAY_COLLECTION_NAME)
class GaussDayImpl<LocaleDate> extends AbstractDay<LocalDate,Integer> {

	static final String DAY_COLLECTION_NAME = "Specialday";
	static final int KEY_PREFIX = 1;
	static final String TO_STRING_PATTERN = "Gauss: offset=%s, dayGroup=%s";
	private final Integer  offset;
	
	GaussDayImpl(final DayGroup dayGroup, final int offset) {
		super(dayGroup, offset);
		this.offset=offset;
	}

	@Override
	public final boolean evaluate(final LocalDate date) {
		Assert.notNull(date, "Date is required.");
		return date.equals(easterdate(date.getYear()).plusDays(offset));
	}

	@Override
	public final LocalDate value() {
		return easterdate(yearMonth().getYear()).plusDays(offset);
	}

	@Override
	public final String toString() {
		return String.format(TO_STRING_PATTERN , offset, dayGroup().name());
	}

	
	LocalDate easterdate(final int year) {
		final int k = year / 100;
		final int m  = 15 + (3*k + 3) / 4 - (8*k + 13) / 25;
		final int s = 2 - (3*k + 3) / 4;
		final int a = year % 19; 
		final int d  = (19*a + m) % 30; 
		final int r = (d + a / 11) / 29;
		final int og = 21 + d - r;
		final int sz = 7 - (year + year / 4 + s) % 7;
		final int oe = 7 - (og - sz) % 7;
		final int daysFromFirstOfMarch = og+oe;
		return LocalDate.of(year, 3, 1).minusDays(1).plusDays(daysFromFirstOfMarch);
			
	}

	@Override
	long keyPrefix() {
		return KEY_PREFIX;
	}

	
	
	
}
