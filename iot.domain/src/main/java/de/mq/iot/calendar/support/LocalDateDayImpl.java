package de.mq.iot.calendar.support;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import de.mq.iot.calendar.DayGroup;
@Document(collection=GaussDayImpl.DAY_COLLECTION_NAME)
class LocalDateDayImpl extends AbstractDay<LocalDate> {

	static final int KEY_PREFIX = 3;

	static final String TO_STRING_PATTERN = "Date: localDate=%s, dayGroup=%s";
	
	private final Integer dayOfMonth;
	
	private final Integer month;
	
	private final Integer year;
	
	LocalDateDayImpl(final DayGroup dayGroup, final LocalDate date) {
		super(dayGroup);
		Assert.notNull(date, "LocalDate is required.");
		assign(new UUID(mostSigBits(KEY_PREFIX), date.getDayOfMonth() + 100 * date.getMonthValue() + 10000*date.getYear()));
		dayOfMonth=date.getDayOfMonth();
		month=date.getMonthValue();
		year=date.getYear();
		
	}
	
	@SuppressWarnings("unused")
	private LocalDateDayImpl() {
		super();
		this.dayOfMonth = null;
		this.month = null;
		this.year=null;
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


}
