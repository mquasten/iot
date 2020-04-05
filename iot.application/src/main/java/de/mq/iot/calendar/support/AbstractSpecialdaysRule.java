package de.mq.iot.calendar.support;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Optional;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.core.BasicRule;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService.DayType;


abstract  class AbstractSpecialdaysRule extends BasicRule implements Rule{
	static final String DAY_TYPE_INFO_FORMAT = "%s: %s";
	static final String DESCRIPTION_FORMAT = "rule=%s, priority=%s";
	private final int priority;
	
	AbstractSpecialdaysRule(final int priority ) {
		this.priority=priority;
	}
	
	
	

	@Override
	public final String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public final String getDescription() {
		return String.format(DESCRIPTION_FORMAT, getName(), getPriority());
	}

	@Override
	public  final int getPriority() {
		return this.priority;
	}

	@Override
	public final boolean evaluate(final Facts facts) {
		return true;
	}

	@Override
	public   final void execute(final Facts facts) throws Exception {
		final SpecialdaysRulesEngineResultImpl result = facts.get(SpecialdaysRulesEngineBuilder.RESULT);
		final Collection<Specialday> specialday =  facts.get(SpecialdaysRulesEngineBuilder.SPECIALDAYS_INPUT);
		final LocalDate date = facts.get(SpecialdaysRulesEngineBuilder.DATE_INPUT);
		
		
		execute(specialday, date).ifPresent(entry -> result.assign(entry.getKey(), entry.getValue()));
	}


	abstract Optional<Entry<DayType,String>> execute(final Collection<Specialday> specialday, final LocalDate date);
	
	

}
