package de.mq.iot.calendar.support;


import java.util.Optional;

import org.springframework.util.Assert;

import de.mq.iot.calendar.SpecialdayService.DayType;


class SpecialdaysRulesEngineResultImpl implements SpecialdaysRulesEngineResult {
	
	static final String ERROR_MESSAGE = "Error specialdays rulesengine, rule: ";

	private Optional<DayType> dayType=Optional.empty();
	
	private Optional<IllegalStateException> exception = Optional.empty();
	
	private String description;
	
	@Override
	public DayType dayType() {
		throwExceptionIfExists();
		return dayType.orElseThrow(() -> new IllegalStateException());
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
		this.exception=Optional.of(new IllegalStateException(ERROR_MESSAGE+rule, exception));
	}
	void assign(final DayType dayType) {
		this.dayType=Optional.of(dayType);
	}
	
	
	@Override
	public String description() {
		return description;
	}

	void assignDescription(String description) {
		this.description=description;
		
	}
	
}
