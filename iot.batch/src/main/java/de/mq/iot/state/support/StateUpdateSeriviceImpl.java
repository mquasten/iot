package de.mq.iot.state.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.openweather.MeteorologicalDataService;
import de.mq.iot.state.Command;
import de.mq.iot.state.Commands;
import de.mq.iot.state.StateService;
import de.mq.iot.state.StateUpdateService;

@Service
public class StateUpdateSeriviceImpl implements StateUpdateService {
	static final String MONTH_STATE_NAME = "Month";
	static final String SUMMER = "SUMMER";
	static final String WINTER = "WINTER";
	static final String WORKINGDAY_STATE_NAME = "Workingday";
	static final String TIME_STATE_NAME = "Time";
	private final SpecialdayService specialdayService;
	private final StateService stateService;
	private final MeteorologicalDataService meteorologicalDataService;
	@Autowired
	StateUpdateSeriviceImpl(final SpecialdayService specialdayService, final StateService stateService, final MeteorologicalDataService meteorologicalDataService) {

		this.specialdayService = specialdayService;
		this.stateService = stateService;
		this.meteorologicalDataService=meteorologicalDataService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.state.support.StateUpdateService#update(int)
	 */
	@Override
	@Commands(commands = { @Command(arguments = { "d" }, name = "updateWorkingday") })
	public void updateWorkingday(final int offsetDays) {
		Assert.isTrue(offsetDays >= 0, "Offset days should be greather or equals 0.");
		final LocalDate localDate = LocalDate.now().plusDays(offsetDays);
		

		@SuppressWarnings("unchecked")
		final State<Boolean> workingDayState = (State<Boolean>) stateService.states().stream().filter(state -> state.name().equals(WORKINGDAY_STATE_NAME)).findAny().orElseThrow(() -> new IllegalStateException("Workingday State expected."));
		final boolean expectedWorkingDayStateValue = isWorkingsday(localDate);
		if (!workingDayState.value().equals(expectedWorkingDayStateValue)) {
			System.out.println("update needed (WorkindDay) ...");
			workingDayState.assign(expectedWorkingDayStateValue);
			stateService.update(workingDayState);

			System.out.println("update workingday to:" + expectedWorkingDayStateValue);
		}

	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.StateUpdateService#updateTime(int)
	 */
	@Override
	@Commands(commands = { @Command(arguments = { "d" }, name = "updateCalendar") })
	public void updateTime(final int offsetDays) {
		Assert.isTrue(offsetDays >= 0, "Offset days should be greather or equals 0.");
		final LocalDate localDate = LocalDate.now().plusDays(offsetDays);
		
		final Collection<State<?>> states = stateService.states();
	
		@SuppressWarnings("unchecked")
		final State<Integer> timeState = (State<Integer>) states.stream().filter(state -> state.name().equals(TIME_STATE_NAME)).findAny().orElseThrow(() -> new IllegalStateException("Time State expected."));
		
		final Map<String, Integer> timeItemValues = ((ItemList)timeState).items().stream().collect(Collectors.toMap(Entry::getValue ,  Entry::getKey));
		
		final Integer expectedTimeStateValue = time(localDate, timeItemValues);
		if (!timeState.value().equals(expectedTimeStateValue)) {
			System.out.println("update needed (Time) ...");
			
		
			timeState.assign(expectedTimeStateValue);
			stateService.update(timeState);

			System.out.println("update time to:" + expectedTimeStateValue);
		}
		
		
		@SuppressWarnings("unchecked")
		final State<Integer> monthState = (State<Integer>) states.stream().filter(state -> state.name().equals(MONTH_STATE_NAME)).findAny().orElseThrow(() -> new IllegalStateException("Month State expected."));
	
		final Map<String, Integer> monthItemValues = ((ItemList)monthState).items().stream().collect(Collectors.toMap(Entry::getValue ,  Entry::getKey));
		
		
		final Integer expectedMonthStateValue = monthItemValues.get(localDate.getMonth().name());
		//System.out.println(localDate.getMonth().name());
		
		if (!monthState.value().equals(expectedMonthStateValue)) {
			System.out.println("update needed (Time) ...");
			
			monthState.assign(expectedMonthStateValue);
			stateService.update(monthState);
			System.out.println("update month to:" + expectedMonthStateValue);
		} 
		//System.out.println(monthItemValues);  
		
	}
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.StateUpdateService#updateTemperature(int)
	 */
	@Override
	@Commands(commands = { @Command(arguments = { "d" }, name = "updateTemperature") })
	public void updateTemperature(final int offsetDays) {
		Assert.isTrue(offsetDays >= 0, "Offset days should be greather or equals 0.");
		Assert.isTrue(offsetDays <= 5, "Offset days should be less or equals 5.");
		final LocalDate localDate = LocalDate.now().plusDays(offsetDays);
		final Collection<State<?>> states = stateService.states();
		
		@SuppressWarnings("unchecked")
		final State<Double> temperatureState = (State<Double>) states.stream().filter(state -> state.name().equals("Temperature")).findAny().orElseThrow(() -> new IllegalStateException("Time State expected."));
		
		final double expectedTemperatureStateValue = meteorologicalDataService.forecastMaxTemperature(localDate).temperature();
		if (!temperatureState.value().equals(expectedTemperatureStateValue)) {
			System.out.println("update needed (Temperature) ...");
			temperatureState.assign(expectedTemperatureStateValue);
			
			stateService.update(temperatureState);
			System.out.println("update temperature to:" + expectedTemperatureStateValue);

		}
		
		
		
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

	Integer time(final LocalDate date, final Map<String, Integer> items ) {

		final LocalDate startSummerTime = lastSundayInMonth(Year.of(date.getYear()), Month.MARCH);

		final LocalDate startWinterTime = lastSundayInMonth(Year.of(date.getYear()), Month.OCTOBER);

		if (afterEquals(date, startSummerTime) && date.isBefore(startWinterTime)) {
			return items.get(SUMMER);
		}

		return items.get(WINTER);
	}

	private boolean afterEquals(final LocalDate date, final LocalDate startSummerTime) {
		return date.isAfter(startSummerTime) || date.isEqual(startSummerTime);
	}

	LocalDate lastSundayInMonth(final Year year, final Month month) {
		final LocalDate start = LocalDate.of(year.getValue(), Month.of(month.getValue() + 1), 1);
		return IntStream.range(1, 8).mapToObj(i -> start.minusDays(i)).filter(date -> date.getDayOfWeek().equals(DayOfWeek.SUNDAY)).findFirst().get();

	}

}
