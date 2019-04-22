package de.mq.iot.state.support;



import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.openweather.MeteorologicalDataService;
import de.mq.iot.rule.RulesDefinition;
import de.mq.iot.rule.RulesDefinition.Id;
import de.mq.iot.rule.support.RulesAggregate;
import de.mq.iot.rule.support.RulesAggregateResult;
import de.mq.iot.rule.support.RulesService;
import de.mq.iot.state.Command;
import de.mq.iot.state.Commands;
import de.mq.iot.state.State;
import de.mq.iot.state.StateService;
import de.mq.iot.state.StateUpdateService;
import de.mq.iot.support.SunDownCalculationService;

@Service
public class StateUpdateServiceImpl implements StateUpdateService {
	static final String LAST_BATCHRUN_DATE_FORMAT = "dd.MM.YYYY-HH:mm:ss";
	static final LocalTime UPDATE_TEMPERATURE_TIME = LocalTime.of(9, 30);
	static final int NEXT_DAY_DAYS_OFFSET = 1;
	static final int CURRENT_DAY_DAYS_OFFSET = 0;
	static final int OFFSET_HOURS_WT = 1;
	static final int OFFSET_HOURS_ST = 2;
	static final String TEMPERATURE_STATE_NAME = "Temperature";
	static final String LAST_BATCHRUN_STATE_NAME = "LastBatchrun";
	static final String MONTH_STATE_NAME = "Month";
	static final String SUMMER = "SUMMER";
	static final String WINTER = "WINTER";
	static final String WORKINGDAY_STATE_NAME = "Workingday";
	static final String TIME_STATE_NAME = "Time";
	private final SpecialdayService specialdayService;
	private final StateService stateService;
	private final MeteorologicalDataService meteorologicalDataService;
	
	private final SunDownCalculationService sunDownCalculationService; 
	
	private final  RulesService rulesService;
	@Autowired
	StateUpdateServiceImpl(final SpecialdayService specialdayService, final StateService stateService, final MeteorologicalDataService meteorologicalDataService,final SunDownCalculationService sunDownCalculationService,final  RulesService rulesService) {

		this.specialdayService = specialdayService;
		this.stateService = stateService;
		this.meteorologicalDataService=meteorologicalDataService;
		this.sunDownCalculationService=sunDownCalculationService;
		this.rulesService=rulesService;
	}
	
	@Override
	@Commands(commands = {  @Command(arguments = {"n", "u", "t" }, name = "processRules" ) })
	public void processRules(final String name, final boolean update, final boolean test) {
		final Id id = Id.valueOf(name);
		
		final RulesAggregate rulesAggregate = rulesService.rulesAggregate(id, Arrays.asList(new AbstractMap.SimpleImmutableEntry<>(RulesDefinition.UPDATE_MODE_KEY, String.valueOf(update)), new AbstractMap.SimpleImmutableEntry<>(RulesDefinition.TEST_MODE_KEY, String.valueOf(test))));
	
		System.out.println("Name: " +id.name());
		
		System.out.println("UpdateMode: "  + update);
		System.out.println("TestMode: "  + test);
		RulesAggregateResult result = rulesAggregate.fire();
		
		
		System.out.println("Errors: " +result.hasErrors());
		
		System.out.println("Verarbeitete Regeln: " + result.processedRules());
		
		
		result.exceptions().forEach(exception ->  {
		System.out.println(exception.getKey() +":");
		exception.getValue().printStackTrace();
		});
		
		result.states().forEach(state -> System.out.println(state.name() +": " + state.value()));
		
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
		final State<Double> temperatureState = (State<Double>) states.stream().filter(state -> state.name().equals(TEMPERATURE_STATE_NAME)).findAny().orElseThrow(() -> new IllegalStateException("Time State expected."));
		
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
	
	@Override
	@Commands(commands = {  @Command( name = "updateAll", arguments = {}) })
	public void update() {
		final LocalDateTime date = LocalDateTime.now();
		updateWorkingday(defaultWorkingDayOffset(date));
		updateTime(defaultUpdateTimeOffset(date));	
		updateTemperature(defaultUpdateTemperature(date));
		updateLastBatchrun();
	}

	 int defaultUpdateTemperature(final LocalDateTime date) {
		return date.toLocalTime().isBefore(UPDATE_TEMPERATURE_TIME)? 0 : 1;
	}

	 int defaultUpdateTimeOffset(final LocalDateTime date) {
		return date.toLocalTime().isBefore(sunDownCalculationService.sunDownTime(date.getMonth(), defaultTimeOffset(date))) ? 0 :1;
	}

	int defaultTimeOffset(final  LocalDateTime date) {
		final Map<String, Integer> map = new HashMap<>();
		map.put(SUMMER, 2);
		map.put(WINTER, OFFSET_HOURS_WT);
		return time(date.toLocalDate(), map );
	}

	int defaultWorkingDayOffset(final LocalDateTime date) {
		final Map<Boolean, LocalTime> workingDaysTimes = new HashMap<>();
		workingDaysTimes.put(Boolean.TRUE, LocalTime.of(5, 45 ));
		workingDaysTimes.put(Boolean.FALSE, LocalTime.of(7, 30 ));
		final int offset = date.toLocalTime().isBefore(workingDaysTimes.get(this.isWorkingsday(date.toLocalDate()))) ? CURRENT_DAY_DAYS_OFFSET :NEXT_DAY_DAYS_OFFSET;
		return offset;
	}
	
	void updateLastBatchrun() {
		final Collection<State<?>> states = stateService.states();
		@SuppressWarnings("unchecked")
		final State<String> lastBatchrunState = (State<String>) states.stream().filter(state -> state.name().equals(LAST_BATCHRUN_STATE_NAME)).findAny().orElseThrow(() -> new IllegalStateException("LastBatchrun State expected."));
		
		final String date = new SimpleDateFormat(LAST_BATCHRUN_DATE_FORMAT).format(new Date(System.currentTimeMillis()));
		lastBatchrunState.assign(date);

		stateService.update(lastBatchrunState);
		System.out.println("update lastBatchrun to:" + date);
	}

}
