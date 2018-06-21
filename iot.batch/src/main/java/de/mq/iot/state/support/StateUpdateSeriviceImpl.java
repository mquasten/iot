package de.mq.iot.state.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.state.Command;
import de.mq.iot.state.Commands;
import de.mq.iot.state.StateService;
import de.mq.iot.state.StateUpdateService;

@Service
public class StateUpdateSeriviceImpl implements StateUpdateService {
	static final String SUMMER = "SUMMER";
	static final String WINTER = "WINTER";
	static final String WORKINGDAY_STATE_NAME = "Workingday";
	private final SpecialdayService specialdayService;
	private final StateService stateService;

	@Autowired
	StateUpdateSeriviceImpl(final SpecialdayService specialdayService, final StateService stateService) {

		this.specialdayService = specialdayService;
		this.stateService = stateService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.state.support.StateUpdateService#update(int)
	 */
	@Override
	@Commands(commands = { @Command(arguments = { "d" }, name = "updateWorkingday"), @Command(arguments = { "d" }, name = "updateCalendar", order = 1) })
	public void updateWorkingday(final int offsetDays) {
		final LocalDate localDate = LocalDate.now().plusDays(offsetDays);
		Assert.isTrue(offsetDays >= 0, "Offset days should be greather or equals 0.");

		@SuppressWarnings("unchecked")
		final State<Boolean> workingDayState = (State<Boolean>) stateService.states().stream().filter(state -> state.name().equals(WORKINGDAY_STATE_NAME)).findAny().orElseThrow(() -> new IllegalStateException("Workingday State expected."));
		final boolean expectedWorkingDayStateValue = isWorkingsday(localDate);
		if (!workingDayState.value().equals(expectedWorkingDayStateValue)) {
			System.out.println("update needed ...");
			workingDayState.assign(expectedWorkingDayStateValue);
			stateService.update(workingDayState);

			System.out.println("update workingday to:" + expectedWorkingDayStateValue);
		}

	}

	@Commands(commands = { @Command(arguments = { "d" }, name = "updateTime"), @Command(arguments = { "d" }, name = "updateCalendar") })
	public void updateTime(final int offsetDays) {

	}

	boolean isWorkingsday(final LocalDate date) {
		if (Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(date.getDayOfWeek())) {
			return false;
		}

		final Collection<LocalDate> specialdates = specialdayService.specialdays(Year.from(date)).stream().map(specialday -> specialday.date(date.getYear())).collect(Collectors.toSet());

		if (specialdates.contains(date)) {

			return false;
		}

		return true;
	}

	String time(final LocalDate date) {

		final LocalDate startSummerTime = lastSundayInMonth(Year.of(date.getYear()), Month.MARCH);

		final LocalDate startWinterTime = lastSundayInMonth(Year.of(date.getYear()), Month.OCTOBER);

		if (afterEquals(date, startSummerTime) && date.isBefore(startWinterTime)) {
			return SUMMER;
		}

		return WINTER;
	}

	private boolean afterEquals(final LocalDate date, final LocalDate startSummerTime) {
		return date.isAfter(startSummerTime) || date.isEqual(startSummerTime);
	}

	LocalDate lastSundayInMonth(final Year year, final Month month) {
		final LocalDate start = LocalDate.of(year.getValue(), Month.of(month.getValue() + 1), 1);
		return IntStream.range(1, 8).mapToObj(i -> start.minusDays(i)).filter(date -> date.getDayOfWeek().equals(DayOfWeek.SUNDAY)).findFirst().orElseThrow(() -> new IllegalArgumentException("Date is required"));

	}

}
