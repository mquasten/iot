package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.withSettings;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.openweather.MeteorologicalData;
import de.mq.iot.openweather.MeteorologicalDataService;
import de.mq.iot.state.State;
import de.mq.iot.state.StateService;
import de.mq.iot.state.StateUpdateService;
import de.mq.iot.support.SunDownCalculationService;

public class StateUpdateSeriviceTest {

	private static final double NEW_TEMPERATURE_STATE = 27.27d;
	private final SpecialdayService specialdayService = Mockito.mock(SpecialdayService.class);
	private final StateService stateService = Mockito.mock(StateService.class);

	private final MeteorologicalDataService meteorologicalDataService = Mockito.mock(MeteorologicalDataService.class);
	
	private final SunDownCalculationService sunDownCalculationService = Mockito.mock(SunDownCalculationService.class);
	
	private final StateUpdateService stateUpdateService = new StateUpdateServiceImpl(specialdayService, stateService, meteorologicalDataService, sunDownCalculationService);
	
	private final Map<Integer, String> timeItems = new HashMap<>();
	
	private final Map<Integer, String> monthItems = new HashMap<>();
	
	
	
	static final Integer SUMMER_VALUE = 1;
	static final Integer WINTER_VALUE = 0;

	@SuppressWarnings("unchecked")
	private final State<Boolean> state = Mockito.mock(State.class);

	
	@SuppressWarnings("unchecked")
	private final State<Integer> timeState = Mockito.mock(State.class, withSettings().extraInterfaces(ItemList.class));
	@SuppressWarnings("unchecked")
	private final State<Integer> monthState = Mockito.mock(State.class, withSettings().extraInterfaces(ItemList.class));
	
	private final MeteorologicalData meteorologicalData = Mockito.mock(MeteorologicalData.class);
	@SuppressWarnings("unchecked")
	private final State<Double> temperatureState = Mockito.mock(State.class);
	
	
	@SuppressWarnings("unchecked")
	private final State<String> lastBatchrunState = Mockito.mock(State.class);
	@BeforeEach
	void setup() {
		timeItems.put(WINTER_VALUE, "WINTER");
		timeItems.put(SUMMER_VALUE, "SUMMER");
		
		
		Mockito.doReturn(StateUpdateServiceImpl.TIME_STATE_NAME).when(timeState).name();
		Mockito.doReturn(timeItems.entrySet()).when((ItemList)timeState).items();
		
		
		Mockito.doReturn(StateUpdateServiceImpl.MONTH_STATE_NAME).when(monthState).name();
		monthItems.putAll(Arrays.asList(Month.values()).stream().collect(Collectors.toMap(Month::ordinal, Month::name)));
		Mockito.doReturn(monthItems.entrySet()).when((ItemList)monthState).items();
	
		
		Mockito.doReturn(StateUpdateServiceImpl.TEMPERATURE_STATE_NAME).when(temperatureState).name();
		
		
		Mockito.doReturn(Arrays.asList(state, timeState, monthState, temperatureState, lastBatchrunState)).when(stateService).states();
		
		
		
	
	
		Mockito.doReturn(StateUpdateServiceImpl.WORKINGDAY_STATE_NAME).when(state).name();
		Mockito.doReturn(Boolean.FALSE).when(state).value();
		Mockito.when(temperatureState.value()).thenReturn(25.55d);
		Mockito.when(meteorologicalData.temperature()).thenReturn(27.27d);
		Mockito.when(meteorologicalDataService.forecastMaxTemperature(LocalDate.now().plusDays(1))).thenReturn(meteorologicalData);
		
		
		
		Mockito.doReturn("LastBatchrun").when(lastBatchrunState).name();
		
		
		
		
	}

	@Test
	void updateWorkingday() {
		final LocalDate localdate = LocalDate.now();
		final Integer offset = workingDayOffset(localdate);

		stateUpdateService.updateWorkingday(offset);
		Mockito.verify(state).assign(Boolean.TRUE);

		Mockito.verify(stateService).update(state);

	}

	private int workingDayOffset(final LocalDate localdate) {
		return IntStream.range(0, 7).filter(i -> !(localdate.plusDays(i).getDayOfWeek().equals(DayOfWeek.SATURDAY) || localdate.plusDays(i).getDayOfWeek().equals(DayOfWeek.SUNDAY))).findFirst().getAsInt();
	}

	@Test
	void updateWorkingdayNothingTodo() {
		Mockito.doReturn(Boolean.TRUE).when(state).value();
		final LocalDate localdate = LocalDate.now();
		final Integer offset = workingDayOffset(localdate);

		stateUpdateService.updateWorkingday(offset);
		Mockito.verify(state, Mockito.never()).assign(Boolean.TRUE);

		Mockito.verify(stateService, Mockito.never()).update(state);

	}

	@Test
	void updateWorkingdayInvalidOffset() {
		assertThrows(IllegalArgumentException.class, () -> stateUpdateService.updateWorkingday(-1));
	}

	@Test
	void updateWorkingdayStateMissing() {
		Mockito.doReturn("???").when(state).name();
		Mockito.doReturn(Arrays.asList(state)).when(stateService).states();

		assertThrows(IllegalStateException.class, () -> stateUpdateService.updateWorkingday(0));
	}

	@Test
	void isWorkingsdaySpecialDay() {
		final LocalDate testDate = prepareSpecialDays();

		assertFalse(((StateUpdateServiceImpl) stateUpdateService).isWorkingsday(testDate));
	}

	private LocalDate prepareSpecialDays() {
		final LocalDate localdate = LocalDate.now();
		final Integer offset = workingDayOffset(localdate);
		final LocalDate testDate = localdate.plusDays(offset);
		final Specialday specialday = Mockito.mock(Specialday.class);

		Mockito.doReturn(testDate).when(specialday).date(testDate.getYear());
		Mockito.doReturn(Arrays.asList(specialday)).when(specialdayService).specialdays(Year.of(testDate.getYear()));
		return testDate;
	}

	private int workingWeekEndOffset(final LocalDate localdate) {
		return IntStream.range(0, 7).filter(i -> localdate.plusDays(i).getDayOfWeek().equals(DayOfWeek.SATURDAY) || localdate.plusDays(i).getDayOfWeek().equals(DayOfWeek.SUNDAY)).findFirst().getAsInt();
	}

	@Test
	void isWorkingsdayWeekend() {
		final LocalDate testDate = LocalDate.now().plusDays(workingWeekEndOffset(LocalDate.now()));

		assertFalse(((StateUpdateServiceImpl) stateUpdateService).isWorkingsday(testDate));
	}

	@Test
	void isWorkingsday() {
		final LocalDate testDate = LocalDate.now().plusDays(workingDayOffset(LocalDate.now()));
		assertTrue(((StateUpdateServiceImpl) stateUpdateService).isWorkingsday(testDate));
	}

	@Test
	void time() {
		final Map<Month, Integer> expectedValues = new HashMap<>();
		
		final Map<String,Integer> reverseItemItems =  reverseItemItems();
		
		expectedValues.putAll(Arrays.asList(Month.JANUARY, Month.FEBRUARY, Month.MARCH, Month.NOVEMBER, Month.DECEMBER).stream().collect(Collectors.toMap(month -> month, month -> WINTER_VALUE)));

		expectedValues.putAll(Arrays.asList(Month.values()).stream().filter(month -> !expectedValues.containsKey(month)).collect(Collectors.toMap(month -> month, month ->SUMMER_VALUE)));

		Arrays.asList(Month.values()).forEach(month -> {
			final LocalDate date = LocalDate.of(LocalDate.now().getYear(), month, 1);
			assertEquals(expectedValues.get(month), ((StateUpdateServiceImpl) stateUpdateService).time(date, reverseItemItems), month.name());
		});

		assertEquals(WINTER_VALUE, ((StateUpdateServiceImpl) stateUpdateService).time(LocalDate.of(2018, 3, 24),reverseItemItems));
		assertEquals(SUMMER_VALUE, ((StateUpdateServiceImpl) stateUpdateService).time(LocalDate.of(2018, 3, 25),reverseItemItems));
		assertEquals(SUMMER_VALUE, ((StateUpdateServiceImpl) stateUpdateService).time(LocalDate.of(2018, 10, 27), reverseItemItems));
		assertEquals(WINTER_VALUE, ((StateUpdateServiceImpl) stateUpdateService).time(LocalDate.of(2018, 10, 28),reverseItemItems));

	}

	private Map<String, Integer> reverseItemItems() {
		return timeItems.entrySet().stream().collect(Collectors.toMap(Entry::getValue, Entry::getKey));
	}
	
	@Test
	void updateTimeWrongDaysOffset() {
		Mockito.doReturn(0).when(timeState).value();
		Mockito.doReturn(0).when(monthState).value();
		
		assertThrows(IllegalArgumentException.class, () -> stateUpdateService.updateTime(-1));
	}
	
	
	@Test
	void updateTimeSummerWinterStateMissing() {
		Mockito.doReturn(0).when(timeState).value();
		Mockito.doReturn(0).when(monthState).value();
		
		Mockito.doReturn(Arrays.asList(state, monthState)).when(stateService).states();		
	
		
		assertThrows(IllegalStateException.class, () -> stateUpdateService.updateTime(0));
		
	}
	
	@Test
	void updateTimeSummerWinter() {
		final Map<String,Integer> reverseItemItems =  reverseItemItems();
		final Integer currentState = ((StateUpdateServiceImpl) stateUpdateService).time(LocalDate.now(), reverseItemItems).equals(SUMMER_VALUE) ? WINTER_VALUE: SUMMER_VALUE;
	
		
		Mockito.doReturn(currentState).when(timeState).value();
		
		final Integer currentMonth = monthItems.entrySet().stream().filter(entry -> entry.getValue().equals(LocalDate.now().getMonth().name())).map(entry -> entry.getKey()).findFirst().orElseThrow(() -> new IllegalStateException("Month not found"));
		
		
		Mockito.doReturn(currentMonth).when(monthState).value();
		
		stateUpdateService.updateTime(0);
		
		Mockito.verify(monthState, Mockito.never()).assign(Mockito.any());
		Mockito.verify(stateService, Mockito.never()).update(monthState);
		
	    Mockito.verify(timeState).assign(WINTER_VALUE);
		Mockito.verify(stateService).update(timeState);
	}
	
	@Test
	void updateMonth() {
		final Integer currentState = ((StateUpdateServiceImpl) stateUpdateService).time(LocalDate.now(), reverseItemItems());
		Mockito.doReturn(currentState).when(timeState).value();
		
		
		final Integer currentMonth = monthItems.entrySet().stream().filter(entry -> entry.getValue().equals(LocalDate.now().minusDays(31).getMonth().name())).map(entry -> entry.getKey()).findFirst().orElseThrow(() -> new IllegalStateException("Month not found"));
		Mockito.doReturn(currentMonth).when(monthState).value();
		
		stateUpdateService.updateTime(0);
		
		Mockito.verify(timeState,Mockito.never()).assign(Mockito.any());
		Mockito.verify(stateService, Mockito.never()).update(timeState);
		
		Mockito.verify(monthState).assign(LocalDate.now().getMonthValue()-1);
		Mockito.verify(stateService).update(monthState);
	}
	
	
	@Test
	void updateTimeMonthStateMissing() {
		Mockito.doReturn(0).when(timeState).value();
		Mockito.doReturn(0).when(monthState).value();
		
		Mockito.doReturn(Arrays.asList(state, timeState)).when(stateService).states();		
	
		
		assertThrows(IllegalStateException.class, () -> stateUpdateService.updateTime(0));
		
	}
	
	@Test
	void updateTemperature() {
		
	
		stateUpdateService.updateTemperature(1);
		
		Mockito.verify(temperatureState).assign(NEW_TEMPERATURE_STATE);

		Mockito.verify(stateService).update(temperatureState);
	}
	
	
	@Test
	void  updateTemperatureNotChanged() {
		
		Mockito.when(temperatureState.value()).thenReturn(NEW_TEMPERATURE_STATE);
		
		stateUpdateService.updateTemperature(1);
		
		Mockito.verify(temperatureState, Mockito.never()).assign(NEW_TEMPERATURE_STATE);

		Mockito.verify(stateService, Mockito.never()).update(temperatureState);
		Mockito.verify(meteorologicalDataService).forecastMaxTemperature(Mockito.any());
		
	}
	
	@Test
	void updateTemperatureTemperatureStateMissing() {
		Mockito.when(stateService.states()).thenReturn(Arrays.asList());
		assertThrows(IllegalStateException.class, () -> stateUpdateService.updateTemperature(1));
		
	}
	
	@Test
	void updateTemperatureOffsetLessThan0() {
		assertThrows(IllegalArgumentException.class, () -> stateUpdateService.updateTemperature(-1));
	}
	
	@Test
	void updateTemperatureOffsetMoreThan5() {
		assertThrows(IllegalArgumentException.class, () -> stateUpdateService.updateTemperature(6));
	}
	@Test
	void defaultTimeOffset() {
		assertEquals(StateUpdateServiceImpl.OFFSET_HOURS_WT, ((StateUpdateServiceImpl)stateUpdateService).defaultTimeOffset(LocalDateTime.of(2018, Month.MARCH, 1, 0, 0)));
		assertEquals(StateUpdateServiceImpl.OFFSET_HOURS_ST,((StateUpdateServiceImpl)stateUpdateService).defaultTimeOffset(LocalDateTime.of(2018, Month.MARCH, 31, 0, 0)));
	
		assertEquals(StateUpdateServiceImpl.OFFSET_HOURS_ST, ((StateUpdateServiceImpl)stateUpdateService).defaultTimeOffset(LocalDateTime.of(2018, Month.OCTOBER, 1, 0, 0)));
		assertEquals(StateUpdateServiceImpl.OFFSET_HOURS_WT,((StateUpdateServiceImpl)stateUpdateService).defaultTimeOffset(LocalDateTime.of(2018, Month.OCTOBER, 31, 0,0)));
	}
	
	
	@Test
	void defaultWorkingDayOffset() {
		final LocalDate sunday = LocalDate.of(2018, 7, 15);
		 final LocalDate normalDay = LocalDate.of(2018, 7, 16);
		 LocalDateTime.of(normalDay, LocalTime.of(5, 40));
		 assertEquals(StateUpdateServiceImpl.CURRENT_DAY_DAYS_OFFSET, (((StateUpdateServiceImpl)stateUpdateService).defaultWorkingDayOffset(LocalDateTime.of(normalDay, LocalTime.of(5, 40)))));
		 assertEquals(StateUpdateServiceImpl.NEXT_DAY_DAYS_OFFSET, (((StateUpdateServiceImpl)stateUpdateService).defaultWorkingDayOffset(LocalDateTime.of(normalDay, LocalTime.of(5, 50)))));
		 
		 assertEquals(StateUpdateServiceImpl.CURRENT_DAY_DAYS_OFFSET, (((StateUpdateServiceImpl)stateUpdateService).defaultWorkingDayOffset(LocalDateTime.of(sunday, LocalTime.of(7, 25)))));
		 assertEquals(StateUpdateServiceImpl.NEXT_DAY_DAYS_OFFSET, (((StateUpdateServiceImpl)stateUpdateService).defaultWorkingDayOffset(LocalDateTime.of(sunday, LocalTime.of(7, 35)))));
	}
	
	@Test
	void update() {
	
		final StateUpdateServiceImpl stateUpdateService = Mockito.mock(StateUpdateServiceImpl.class);
		final Map<Class<?>, Object> dependencies = new HashMap<>();
		dependencies.put(SunDownCalculationService.class, sunDownCalculationService);
		
		final LocalTime time = LocalTime.now();
		Mockito.when(sunDownCalculationService.sunDownTime(Mockito.any(Month.class), Mockito.anyInt())).thenReturn(time);
		
		Arrays.asList(StateUpdateServiceImpl.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType()) ).forEach(field -> ReflectionTestUtils.setField(stateUpdateService, field.getName(), dependencies.get(field.getType())));
		
		
		
		
		Mockito.doCallRealMethod().when(stateUpdateService).update();
		
		stateUpdateService.update();
		
		
		Mockito.verify(stateUpdateService).updateWorkingday(Mockito.anyInt());
		Mockito.verify(stateUpdateService).updateTime(Mockito.anyInt());
		Mockito.verify(stateUpdateService).updateTemperature(Mockito.anyInt());
		Mockito.verify(stateUpdateService).updateLastBatchrun();
	
	}
	
	
	
	@Test
	void defaultUpdateTemperature() {
		assertEquals(0, (((StateUpdateServiceImpl) stateUpdateService).defaultUpdateTemperature(LocalDateTime.of(LocalDate.now(), LocalTime.of(StateUpdateServiceImpl.UPDATE_TEMPERATURE_TIME.getHour(), StateUpdateServiceImpl.UPDATE_TEMPERATURE_TIME.getMinute()-1)))));
		assertEquals(1, (((StateUpdateServiceImpl) stateUpdateService).defaultUpdateTemperature(LocalDateTime.of(LocalDate.now(), LocalTime.of(StateUpdateServiceImpl.UPDATE_TEMPERATURE_TIME.getHour(), StateUpdateServiceImpl.UPDATE_TEMPERATURE_TIME.getMinute()+1)))));
	}
	
	
	@Test
	void defaultUpdateTimeOffset() {
		final  LocalTime sundownTime = LocalTime.of(21, 50);	
		Mockito.when(sunDownCalculationService.sunDownTime(Mockito.any(Month.class), Mockito.anyInt())).thenReturn(sundownTime);
		
	
		assertEquals(0,(((StateUpdateServiceImpl) stateUpdateService).defaultUpdateTimeOffset(LocalDateTime.of(LocalDate.now(), sundownTime.minusMinutes(1)))));
		assertEquals(1,(((StateUpdateServiceImpl) stateUpdateService).defaultUpdateTimeOffset(LocalDateTime.of(LocalDate.now(), sundownTime.plusMinutes(1)))));
	}

	@Test
	void updateLastBatchrun() {
		 ((StateUpdateServiceImpl) stateUpdateService).updateLastBatchrun();
		 
		 
		 final ArgumentCaptor<String>  valueCaptor = ArgumentCaptor.forClass(String.class);
		 Mockito.verify(lastBatchrunState).assign(valueCaptor.capture());
		 Mockito.verify(stateService).update(lastBatchrunState);
		 
		 final String startDateString = new SimpleDateFormat(StateUpdateServiceImpl.LAST_BATCHRUN_DATE_FORMAT.replaceFirst(":.*", "")).format(new Date());
		 
		 assertTrue(valueCaptor.getValue().startsWith(startDateString));		
	}
	
	@Test
	void updateLastBatchrunMissingSystemVariable() {
		Mockito.doReturn(Arrays.asList(state, timeState, monthState, temperatureState)).when(stateService).states();
		
		
		assertThrows(IllegalStateException.class, () -> ((StateUpdateServiceImpl) stateUpdateService).updateLastBatchrun());
	}
}
