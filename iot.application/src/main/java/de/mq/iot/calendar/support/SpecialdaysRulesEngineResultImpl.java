package de.mq.iot.calendar.support;


import java.util.Optional;

import org.springframework.util.Assert;

import de.mq.iot.calendar.SpecialdayService.DayType;


class SpecialdaysRulesEngineResultImpl implements SpecialdaysRulesEngineResult {
	
	static final String ERROR_MESSAGE = "Error specialdays rulesengine, rule: ";

	private Optional<DayType> dayType=Optional.empty();
	
	private Optional<IllegalStateException> exception = Optional.empty();
	
	private Optional<String> description= Optional.empty();;
	
	private Optional<String> successRule = Optional.empty();
	
	@Override
	public DayType dayType() {
		throwExceptionIfExists();
		return dayType.orElseThrow(() -> new IllegalStateException("Result is not aware."));
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
	void assign(final DayType dayType, final String description) {
		Assert.notNull(dayType , "DayType is mandatory.");
		Assert.notNull(description, "Description is mandatory.");
		this.dayType=Optional.of(dayType);
		this.description=Optional.of(description);
	}
	
	
	
	@Override
	public String description() {
		throwExceptionIfExists();
		return description.orElseThrow(() -> new IllegalStateException("Result is not aware."));
	}

	@Override
	public Optional<String> successRule() {
		return successRule;
	}
	void assignSuccessRule(final String successRule) {
		Assert.hasText(successRule, "SuccessRule is mandatory.");
		this.successRule=Optional.of(successRule);
	}
	
}
