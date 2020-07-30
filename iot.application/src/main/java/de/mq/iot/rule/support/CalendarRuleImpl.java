package de.mq.iot.rule.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import de.mq.iot.calendar.DayGroup;
import de.mq.iot.calendar.support.DayService;

@Rule(name="calendarRule", priority=1)
public class CalendarRuleImpl {
	
	private final Supplier<LocalDate> dateSupplier;
	
	private  final DayService dayService;
	public CalendarRuleImpl(final DayService dayService, final Supplier<LocalDate> dateSupplier) {
		this.dayService=dayService;
		this.dateSupplier=dateSupplier;
	}
	
	 
	
	
	 @Condition
	 public boolean evaluate(@Fact(RulesAggregate.RULE_INPUT) final DefaultRuleInput ruleInput) {
		 return ruleInput.valid();
	 }
	 
	
	 @Action
	 public void calculateCalendar(@Fact(RulesAggregate.RULE_INPUT) final DefaultRuleInput ruleInput, @Fact(RulesAggregate.RULE_CALENDAR) final Calendar calendar) {
		final int offset = ruleInput.isUpdateMode() ? 0 : 1;
		
		calendar.assignDate(dateSupplier.get().plusDays(offset));
		
		final DayGroup dayGroup = dayService.dayGroup(calendar.date());
		//final SpecialdaysRulesEngineResult specialdaysRulesEngineResult = specialdayService.specialdaysRulesEngineResult(calendar.date());
		//calendar.assignDayType(specialdaysRulesEngineResult.dayType());
		calendar.assignDayGroup(dayGroup);
		System.out.println("DayGroup :" + dayGroup);
		calendar.assignTime(time(calendar.date()));
	 }
	 
	 
	 private Calendar.Time time(final LocalDate date) {

			final LocalDate startSummerTime = lastSundayInMonth(Year.of(date.getYear()), Month.MARCH);

			final LocalDate startWinterTime = lastSundayInMonth(Year.of(date.getYear()), Month.OCTOBER);

			if (afterEquals(date, startSummerTime) && date.isBefore(startWinterTime)) {
				return Calendar.Time.Summer;
			}

			return Calendar.Time.Winter;
		}

		private boolean afterEquals(final LocalDate date, final LocalDate startSummerTime) {
			return date.isAfter(startSummerTime) || date.isEqual(startSummerTime);
		}

		LocalDate lastSundayInMonth(final Year year, final Month month) {
			final LocalDate start = LocalDate.of(year.getValue(), Month.of(month.getValue() + 1), 1);
			return IntStream.range(1, 8).mapToObj(i -> start.minusDays(i)).filter(date -> date.getDayOfWeek().equals(DayOfWeek.SUNDAY)).findFirst().get();

		}

}
