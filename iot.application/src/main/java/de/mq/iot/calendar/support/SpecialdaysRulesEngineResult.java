package de.mq.iot.calendar.support;

import java.nio.channels.IllegalSelectorException;
import java.util.Optional;

import org.springframework.util.Assert;

import de.mq.iot.calendar.SpecialdayService.DayType;


public class SpecialdaysRulesEngineResult {
	
	private Optional<DayType> dayType=Optional.empty();
	
	private Optional<IllegalStateException> exception = Optional.empty();
	
	private String description;
	
	DayType dayType() {
		throwExceptionIfExists();
		return dayType.orElseThrow(() -> new IllegalSelectorException());
	}
	
	boolean finished() {
		return dayType.isPresent()||exception.isPresent();
	}

	private void throwExceptionIfExists() {
		if( exception.isPresent()) {
			throw exception.get();
		}
	}
	
	void assign(final Throwable exception, final String rule) {
		Assert.notNull(exception, "Exception is mandatory.");
		Assert.hasText(rule, "Rule is mandatory.");
		this.exception=Optional.of(new IllegalStateException("Error specialdays rulesengine, rule:"+rule, exception));
	}
	void assign(final DayType dayType) {
		this.dayType=Optional.of(dayType);
	}
	void assignDescription(final String description) {
		this.description=description;
	}
	
	String description() {
		return description;
	}
	
}
