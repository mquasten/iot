package de.mq.iot.calendar.support;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.core.BasicRule;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

abstract  class AbstractSpecialdaysRule extends BasicRule implements Rule{

	private final int priority;
	
	AbstractSpecialdaysRule(final int priority ) {
		this.priority=priority;
	}
	

	@Override
	public final String getName() {
		return StringUtils.uncapitalize(getClass().getSimpleName().replaceFirst("Impl$", ""));
	}

	@Override
	public final String getDescription() {
		return "rule="+ getName() + ", priority=" + getPriority();
	}

	@Override
	public  final int getPriority() {
		return this.priority;
	}

	@Override
	public final boolean evaluate(final Facts facts) {
		final SpecialdaysRulesEngineResultImpl result = facts.get(SpecialdaysRulesEngineBuilder.RESULT);
		Assert.notNull(result , "Result should be aware.");
		return ! result.finished();
	}

	@Override
	public  abstract void execute(final Facts facts) throws Exception;
	

}
