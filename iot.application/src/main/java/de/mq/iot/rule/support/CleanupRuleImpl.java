package de.mq.iot.rule.support;

import java.time.LocalDate;
import java.util.Collection;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.support.DayService;
import de.mq.iot.rule.RulesDefinition;

@Rule(name = RulesDefinition.CLEANUP_RULE_NAME, priority = 2)
public class CleanupRuleImpl {

	static final String SUCCESS_MESSAGE = "%s specialdays <= %s deleted.";
	
	static final String SUCCESS_MESSAGE_TEST = "%s specialdays <= %s selected.";
	private final DayService specialdayService;

	CleanupRuleImpl(final DayService specialdayService) {
		this.specialdayService = specialdayService;
	}

	@Condition
	public boolean evaluate(@Fact(RulesAggregate.RULE_INPUT) final EndOfDayRuleInput ruleInput) {

		return ruleInput.valid();
	}

	@Action
	public void cleanup(@Fact(RulesAggregate.RULE_INPUT) final EndOfDayRuleInput ruleInput, @Fact(RulesAggregate.RULE_OUTPUT_MAP_FACT) final Collection<String> results) {
		final Collection<Day<LocalDate>> toBeDeleted = specialdayService.localDateDaysBeforeOrEquals(ruleInput.minDeletiondate());
				
		if(ruleInput.isTestMode() ) {
			results.add(String.format(SUCCESS_MESSAGE_TEST, toBeDeleted.size(), ruleInput.minDeletiondate()));
			return;
		}
		toBeDeleted.stream().forEach(specialDay -> specialdayService.delete(specialDay));
		results.add(String.format(SUCCESS_MESSAGE, toBeDeleted.size(), ruleInput.minDeletiondate()));
	}

}
