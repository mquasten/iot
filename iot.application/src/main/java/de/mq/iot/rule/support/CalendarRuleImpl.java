package de.mq.iot.rule.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;

import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

@Rule(name="calendarRule", priority=1)
public class CalendarRuleImpl {
	
	

	 
	 private boolean isWorkingsday(final LocalDate date, final Collection<LocalDate> specialdates) {
		if (Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(date.getDayOfWeek())) {
			return false;
		}

		if (specialdates.contains(date)) {

			return false;
		}

		return true;
	}
	
	
	
	 @Condition
	 public void calculateCalendar(@Fact("ruleInput") final DefaultRuleInput ruleInput, @Fact("specialdates") final Collection<LocalDate> specialdates) {
		final int offset = ruleInput.isUpdateMode() ? 0 : 1;
		final LocalDate date =  LocalDate.now().plusDays(offset);
		final boolean workingday = isWorkingsday(date, specialdates);
	 }

}
