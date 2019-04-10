package de.mq.iot.rule.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;

import org.jeasy.rules.annotation.Rule;

import de.mq.iot.calendar.SpecialdayService;

@Rule(name="calendarRule", priority=1)
public class CalendarRuleImpl {
	private final SpecialdayService specialdayService;
	private final Supplier<LocalDate> dateSupplier;
	public CalendarRuleImpl(final SpecialdayService specialdayService, final Supplier<LocalDate> dateSupplier) {
		this.specialdayService=specialdayService;
		this.dateSupplier=dateSupplier;
	}
	
	 
	 private boolean isWorkingsday(final LocalDate date) {
		if (Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(date.getDayOfWeek())) {
			return false;
		}
		final Collection<LocalDate> specialdates = specialdayService.specialdays(Year.from(date)).stream().map(specialday -> specialday.date(date.getYear())).collect(Collectors.toSet());
		
		if (specialdates.contains(date)) {

			return false;
		}

		return true;
	}
	
	 @Condition
	 public boolean evaluate(@Fact("ruleInput") final DefaultRuleInput ruleInput) {
		 return ruleInput.valid();
	 }
	 
	
	 @Action
	 public void calculateCalendar(@Fact("ruleInput") final DefaultRuleInput ruleInput, @Fact("calendar") final Calendar calendar) {
		final int offset = ruleInput.isUpdateMode() ? 0 : 1;
		calendar.assignDate(dateSupplier.get().plusDays(offset));
		calendar.assignWorkingDay(isWorkingsday(calendar.date()));
		calendar.assignTime(time(calendar.date()));
		
		System.out.println("*calendarRule");
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
