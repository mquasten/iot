package de.mq.iot.calendar.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import de.mq.iot.calendar.DayGroup;

@Document(collection=GaussDayImpl.DAY_COLLECTION_NAME)
class DayOfWeekImpl extends  AbstractDay<DayOfWeek> {
	static final int KEY_PREFIX = 4;

	static final int FREQUENCY_ONCE_PER_WEEK = 51;

	static final String TO_STRING_PATTERN = " DayOfWeek: day=%s, dayGroup=%s";

	private Integer dayOfWeek;
	
	DayOfWeekImpl(final DayGroup dayGroup, final DayOfWeek dayOfWeek) {
		super(dayGroup);
		Assert.notNull(dayOfWeek, "DayOfWeek is required.");
		assign(new UUID(KEY_PREFIX, dayOfWeek.getValue()));
		this.dayOfWeek=dayOfWeek.getValue();
	}
	@SuppressWarnings("unused")
	private DayOfWeekImpl() {
	
	}

	@Override
	public final boolean evaluate(final LocalDate date) {
		return value().equals(date.getDayOfWeek());
	}

	@Override
	public final DayOfWeek value() {
		return DayOfWeek.of(dayOfWeek);
	}
	@Override
	public final String toString() {
		return String.format(TO_STRING_PATTERN ,value(), dayGroup().name());
	}
	
	public int frequency() {
		return FREQUENCY_ONCE_PER_WEEK;
	}

	

}
