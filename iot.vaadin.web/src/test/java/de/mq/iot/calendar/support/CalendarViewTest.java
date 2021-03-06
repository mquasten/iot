package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StringUtils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;


import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.DayGroup;
import de.mq.iot.calendar.DayService;


import de.mq.iot.calendar.support.CalendarModel.Events;
import de.mq.iot.calendar.support.CalendarModel.Filter;
import de.mq.iot.calendar.support.CalendarModel.ValidationErrors;
import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;
import de.mq.iot.support.ButtonBox;



class CalendarViewTest {
	
	private static final String I18N_CALENDAR_VALIDATION = "calendar_validation_";


	private static final String I18N_CALENDAR_DELETE_RANGE = "calendar_delete_range";
	
	
	private static final String I18N_CALENDAR_ADD_RANGE = "calendar_add_range";
	
	private static final String I18N_CALENDAR_TABLE_HEADER  = "calendar_table_type_header";
	
	private static final String I18N_CALENDAR_INFO = "calendar_info";
	
	private static final String I18N_CALENDAR_RANGE_FROM ="calendar_range_from";
	private static final String I18N_CALENDAR_RANGE_TO ="calendar_range_to";
	private static final String I18N_CALENDAR_DAY_OF_WEEK ="calendar_dayofweek";
	
	private static final String I18N_DAYTYPE_VACATION="calendar_daytype_vacation" ;
	private static final String I18N_DAYTYPE_WORKINGDATE="calendar_daytype_workingdate"; 
	private static final String I18N_DAYTYPE_WORKINGDAY="calendar_daytype_workingday";
	private static final String I18N_DAYTYPE_HOLIDAY="calendar_daytype_holiday";

	private final CalendarModel calendarModel = Mockito.mock(CalendarModel.class);
	

	private final DayService dayService = Mockito.mock(DayService.class);
	
	private final MessageSource messageSource = Mockito.mock(MessageSource.class);
	
	private CalendarView calendarView;
	
	private final Map<String, Object> fields = new HashMap<>();
	
	
	@SuppressWarnings("rawtypes")
	private final Day  specialday = Mockito.mock(Day.class);
	
	private final DayGroup dayGroup = Mockito.mock(DayGroup.class);
	
	private final Map<CalendarModel.Events, Observer> observers = new HashMap<>();
	
	private final Subject<?, ?> subject = Mockito.mock(Subject.class);
	
	@BeforeEach
	void setup() {
		Mockito.when(dayService.days()).thenReturn(Arrays.asList(specialday));
		Mockito.when(calendarModel.isChangeCalendarAllowed()).thenReturn(true);
		Mockito.when(calendarModel.locale()).thenReturn(Locale.GERMAN);
		Mockito.when(calendarModel.daysOfWeek()).thenReturn(Arrays.asList(DayOfWeek.values()));
		Arrays.asList(I18N_DAYTYPE_VACATION,I18N_DAYTYPE_WORKINGDATE, I18N_DAYTYPE_WORKINGDAY,I18N_DAYTYPE_HOLIDAY, I18N_CALENDAR_DELETE_RANGE, I18N_CALENDAR_ADD_RANGE, I18N_CALENDAR_TABLE_HEADER, I18N_CALENDAR_INFO, I18N_CALENDAR_RANGE_FROM,I18N_CALENDAR_RANGE_TO, I18N_CALENDAR_DAY_OF_WEEK , I18N_CALENDAR_VALIDATION + ValidationErrors.Invalid.name().toLowerCase()).forEach(key -> Mockito.doReturn(key).when(messageSource).getMessage(key, null, "???", Locale.GERMAN));
		Mockito.doReturn((Predicate<Day<?>>) day -> true).when(calendarModel).filter();
		Mockito.doReturn(Mockito.mock(Comparator.class)).when(calendarModel).comparator();
		Mockito.doAnswer(answer -> {

			final CalendarModel.Events event = (CalendarModel.Events ) answer.getArguments()[0];
			final Observer observer = (Observer) answer.getArguments()[1];
			observers.put(event, observer);
			return null;

		}).when(calendarModel).register(Mockito.any(), Mockito.any());
		
		
		Mockito.doAnswer(answer-> { 
			   observers.get(Events.DatesChanged).process();
			   return null;
		   }).when(calendarModel).assign(CalendarModel.Filter.Vacation);
		   
		
		
		Mockito.when(specialday.value()).thenReturn(LocalDate.of(1968, Month.MAY, 28));
	
		Mockito.when(dayService.days()).thenReturn(Arrays.asList(specialday));
		
		
		
		calendarView = new CalendarView(calendarModel, dayService, messageSource,  new ButtonBox(subject));
		
		Arrays.asList(CalendarView.class.getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).forEach(field -> fields.put(field.getName(), ReflectionTestUtils.getField(calendarView, field.getName())));
	
	    assertEquals(21, fields.size() ); 
	    
	    
	    
	    
	   assertEquals(3, observers.size());
	   
	   observers.get(CalendarModel.Events.ChangeLocale).process();
	   
	   
	   
	  

	}
	
	
	@Test
	void init() {
		Mockito.verify(dayService, Mockito.atLeastOnce()).days();
		
	
		
		final Grid<?> grid = (Grid<?>) fields.get("grid");
		final ListDataProvider<?>  dates = (ListDataProvider<?>) grid.getDataProvider();
		assertEquals(1, dates.getItems().size());
	
		assertNotNull(specialday.value());
		assertEquals(specialday, dates.getItems().stream().findFirst().get());
		
		
		
		final ComboBox<Filter> vacationOnlyCheckbox   = filterComboBox();
		assertEquals(Filter.Vacation, vacationOnlyCheckbox.getValue());
		
		
		
		final Button deleteButton = (Button) fields.get("deleteButton");
		assertFalse(deleteButton.isEnabled());
		
		final Button saveButton = (Button) fields.get("saveButton");
		assertFalse(saveButton.isEnabled());
		final Label stateInfoLabel = (Label) fields.get("stateInfoLabel");
		final Label typeColumLabel = (Label) fields.get("typeColumnLabel");
		
		final Label fromLabel = (Label) fields.get("fromLabel");
		final Label toLabel = (Label) fields.get("toLabel");
		final Label dayOfWeek =  (Label) fields.get("dayOfWeekLabel");
		final TextField from = (TextField) fields.get("fromTextField");
		final TextField to = (TextField) fields.get("toTextField");
		final Label dayTypeVacation = (Label) fields.get("typeVacation");
		final Label dayTypeWorkingDate = (Label) fields.get("typeWorkingDate");
		final Label dayTypeWorkingDay = (Label) fields.get("typeWorkingDay");
		final Label dayTypeHoliday = (Label) fields.get("typeHoliday");
		final ComboBox<?> dayOfWeekComboBox = (ComboBox<?>) fields.get("dayOfWeekComboBox");
		assertEquals(I18N_CALENDAR_DELETE_RANGE, deleteButton.getText());
		assertEquals(I18N_CALENDAR_ADD_RANGE, saveButton.getText());
		assertEquals(I18N_DAYTYPE_VACATION, dayTypeVacation.getText());
		assertEquals(I18N_DAYTYPE_WORKINGDATE, dayTypeWorkingDate.getText());
		assertEquals(I18N_DAYTYPE_WORKINGDAY, dayTypeWorkingDay.getText());
		assertEquals(I18N_DAYTYPE_HOLIDAY, dayTypeHoliday.getText());
		
		assertEquals(I18N_CALENDAR_TABLE_HEADER, typeColumLabel.getText());
		assertEquals(I18N_CALENDAR_INFO, stateInfoLabel.getText());
		assertEquals(I18N_CALENDAR_RANGE_FROM, fromLabel.getText());
		assertEquals(I18N_CALENDAR_RANGE_TO, toLabel.getText());
		assertEquals(I18N_CALENDAR_DAY_OF_WEEK, dayOfWeek.getText());
		
		assertTrue(fromLabel.isVisible());
		assertTrue(toLabel.isVisible());
		assertFalse(dayOfWeek.isVisible());
		assertTrue(from.isVisible());
		assertTrue(to.isVisible());
		assertFalse(dayOfWeekComboBox.isVisible());
		
		
		assertTrue(getEditorLayout(saveButton).isVisible());
		assertTrue(stateInfoLabel.isVisible());
		
	}
	
	@Test
	void editorFields() {
		final ComboBox<?> dayOfWeekComboBox = (ComboBox<?>) fields.get("dayOfWeekComboBox");
		final  Label fromLabel = (Label) fields.get("fromLabel");
		final Label toLabel = (Label) fields.get("toLabel");
		final Label dayOfWeek =  (Label) fields.get("dayOfWeekLabel");
		final TextField from = (TextField) fields.get("fromTextField");
		final TextField to = (TextField) fields.get("toTextField");
		assertTrue(fromLabel.isVisible());
		assertTrue(toLabel.isVisible());
		assertFalse(dayOfWeek.isVisible());
		assertTrue(from.isVisible());
		assertTrue(to.isVisible());
		assertFalse(dayOfWeekComboBox.isVisible());
		
		Mockito.when(calendarModel.isDayOfWeek()).thenReturn(true);
		observers.get(Events.DatesChanged).process();
		
		
		assertFalse(fromLabel.isVisible());
		assertFalse(toLabel.isVisible());
		assertTrue(dayOfWeek.isVisible());
		assertFalse(from.isVisible());
		assertFalse(to.isVisible());
		assertTrue(dayOfWeekComboBox.isVisible());
	}


	private Component getEditorLayout(final Button saveButton) {
		assertTrue(saveButton.getParent().isPresent());
		assertTrue(saveButton.getParent().get().getParent().isPresent());
		return saveButton.getParent().get().getParent().get();
	}
	
	@Test
	void fromTextFieldInvalid() {
		final TextField fromText = (TextField) fields.get("fromTextField");
		assertNotNull(fromText);
		
		Mockito.when(calendarModel.validateFrom(Mockito.anyString())).thenReturn(ValidationErrors.Invalid);
		fromText.setValue("x");
		
		
		assertTrue(fromText.isInvalid());
		assertEquals(I18N_CALENDAR_VALIDATION + ValidationErrors.Invalid.name().toLowerCase(), fromText.getErrorMessage());
		
	}
	
	
	@Test
	void fromTextField() {
		final TextField fromText = (TextField) fields.get("fromTextField");
		assertNotNull(fromText);
		
		Mockito.when(calendarModel.validateFrom(Mockito.anyString())).thenReturn(ValidationErrors.Ok);
		fromText.setValue("31.12.2018");
		
		
		assertFalse(fromText.isInvalid());
		assertFalse(StringUtils.hasText(fromText.getErrorMessage()));
		
		Mockito.verify(calendarModel).assignFrom(fromText.getValue());
		
		
		
	}
	
	
	@Test
	void toTextFieldInvalid() {
		final TextField toText = (TextField) fields.get("toTextField");
		assertNotNull(toText);
		
		Mockito.when(calendarModel.validateTo(Mockito.anyString())).thenReturn(ValidationErrors.Invalid);
		toText.setValue("x");
		
		
		assertTrue(toText.isInvalid());
		assertEquals(I18N_CALENDAR_VALIDATION + ValidationErrors.Invalid.name().toLowerCase(), toText.getErrorMessage());
		
	}
	
	
	@Test
	void toTextField() {
		final TextField toText = (TextField) fields.get("toTextField");
		assertNotNull(toText);
		
		Mockito.when(calendarModel.validateTo(Mockito.anyString())).thenReturn(ValidationErrors.Ok);
		toText.setValue("31.12.2018");
		
		
		assertFalse(toText.isInvalid());
		assertFalse(StringUtils.hasText(toText.getErrorMessage()));
		
		Mockito.verify(calendarModel).assignTo(toText.getValue());
		
	}
	
	@Test
	void enableButton() {
		
		Mockito.when(calendarModel.valid()).thenReturn(true);
		final Button deleteButton = (Button) fields.get("deleteButton");
		assertFalse(deleteButton.isEnabled());
		
		final Button saveButton = (Button) fields.get("saveButton");
		assertFalse(saveButton.isEnabled());
		
		final Observer observer =  observers.get(CalendarModel.Events.ValuesChanged);
		observer.process();
		
		assertTrue(deleteButton.isEnabled());
		assertTrue(saveButton.isEnabled());
		
	}
	
	@Test
	void filtersComboBox() {
		
		final ComboBox<Filter> filtersComboBox =  filterComboBox();
		assertEquals(Filter.Vacation, filtersComboBox.getValue());

		
		filtersComboBox.setValue(Filter.WorkingDate);
		
		Mockito.verify(calendarModel).assign(Filter.WorkingDate);
		
		filtersComboBox.setValue(Filter.Vacation);
		
		Mockito.verify(calendarModel, Mockito.atLeast(1)).assign(Filter.Vacation);
		
		
	}
	
	@Test
	void filtersComboBoxLabels() {
		
		
		final ComboBox<Filter> filtersComboBox =  filterComboBox();
		assertEquals(I18N_DAYTYPE_HOLIDAY, filtersComboBox.getItemLabelGenerator().apply(Filter.Holiday));
		assertEquals(I18N_DAYTYPE_VACATION, filtersComboBox.getItemLabelGenerator().apply(Filter.Vacation));
		assertEquals(I18N_DAYTYPE_WORKINGDATE, filtersComboBox.getItemLabelGenerator().apply(Filter.WorkingDate));
		assertEquals(I18N_DAYTYPE_WORKINGDAY, filtersComboBox.getItemLabelGenerator().apply(Filter.WorkingDay));
		
		
	}

	@SuppressWarnings("unchecked")
	private ComboBox<Filter> filterComboBox() {
		return (ComboBox<Filter>) fields.get("filtersComboBox");
	}
	@Test
	void daysOfWeekLabels() {
		
		final ComboBox<DayOfWeek> dayOfWeekComboBox =   dayOfWeekComboBox();
		Arrays.asList(DayOfWeek.values()).stream().forEach(value -> assertEquals(value.getDisplayName(TextStyle.FULL, Locale.GERMAN), dayOfWeekComboBox.getItemLabelGenerator().apply(value)));
		
	}

	@SuppressWarnings("unchecked")
	private ComboBox<DayOfWeek> dayOfWeekComboBox() {
		return (ComboBox<DayOfWeek>) fields.get("dayOfWeekComboBox");
	}
	
	
	@Test
	void enableEditorFields() {
		
	
		final ComboBox<Filter> filtersComboBox =  filterComboBox();
		final TextField from = (TextField) fields.get("fromTextField");
		final TextField to = (TextField) fields.get("toTextField");
		
		assertEquals(Filter.Vacation, filtersComboBox.getValue());

		
		filtersComboBox.setValue(null);
		assertFalse(from.isEnabled());
		assertFalse(to.isEnabled());
		
		Arrays.asList(Filter.values()).forEach(value -> {
			Mockito.when(calendarModel.editable()).thenReturn(value.editable());
			filtersComboBox.setValue(value);
			
			assertEquals(value.editable(), from.isEnabled());
			assertEquals(value.editable(), to.isEnabled());
		});
		
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void deleteVactions() {
		Mockito.when(calendarModel.dayGroup()).thenReturn(dayGroup);
		
		Mockito.when(dayService.newLocalDateDay(dayGroup, LocalDate.now(), LocalDate.now().plusDays(1))).thenReturn(Arrays.asList(specialday));
		
		prepareForButtons(ValidationErrors.Ok);
		
		final Button deleteButton = (Button) fields.get("deleteButton");
		
		final ComponentEventListener<?> listener = listener(deleteButton);
		
		
		listener.onComponentEvent(null);
		
		Mockito.verify(dayService).delete(specialday);
		
		Mockito.verify(calendarModel, Mockito.times(2)).notifyObservers(CalendarModel.Events.DatesChanged);
		
	}
	
	@Test
	void deleteVactionsValidationError() {
		prepareForButtons(ValidationErrors.FromBeforeTo);
		
		final Button deleteButton = (Button) fields.get("deleteButton");
		
		final ComponentEventListener<?> listener = listener(deleteButton);
		
		
		listener.onComponentEvent(null);
		
		Mockito.verify(dayService, Mockito.never()).delete(specialday);
		
		Mockito.verify(calendarModel, Mockito.never()).notifyObservers(CalendarModel.Events.DatesChanged);
		
	}



	@SuppressWarnings("unchecked")
	private void prepareForButtons(ValidationErrors error) {
		final LocalDate from = LocalDate.now();
		final LocalDate to = LocalDate.now().plusDays(1);
		
		Mockito.when(calendarModel.from()).thenReturn(from);
		Mockito.when(calendarModel.to()).thenReturn(to);
		Mockito.when(dayService.newLocalDateDay(dayGroup , from, to)).thenReturn(Arrays.asList(specialday));;
		
		//Mockito.when(specialdayService.vacationOrSpecialWorkingDates(from, to, false)).thenReturn(Arrays.asList(specialday));
		
		
		Mockito.when(calendarModel.vaidate(Mockito.anyInt())).thenReturn(error);
	}
	
	@SuppressWarnings("unchecked")
	private ComponentEventListener<?> listener(final Component saveButton) {
		final ComponentEventBus eventBus = (ComponentEventBus) ReflectionTestUtils.getField(saveButton, "eventBus");
		final Map<Class<?>, ?> map = (Map<Class<?>, ?>) ReflectionTestUtils.getField(eventBus, "componentEventData");
		return DataAccessUtils.requiredSingleResult((Collection<ComponentEventListener<?>>) ReflectionTestUtils.getField(map.values().iterator().next(), "listeners"));
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	void saveVactions() {
		
		Mockito.when(calendarModel.dayGroup()).thenReturn(dayGroup);
		
		
		Mockito.when(dayService.newLocalDateDay(dayGroup, LocalDate.now(), LocalDate.now().plusDays(1))).thenReturn(Arrays.asList(specialday));
		prepareForButtons(ValidationErrors.Ok);
		
		final Button saveButton = (Button) fields.get("saveButton");
		
		final ComponentEventListener<?> listener = listener(saveButton);
		
		
		listener.onComponentEvent(null);
		
		Mockito.verify(dayService).save(specialday);
		
		Mockito.verify(calendarModel, Mockito.times(2)).notifyObservers(CalendarModel.Events.DatesChanged);
		
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	void saveDayOfWeek() {
		prepareForButtons(ValidationErrors.Ok);
		Mockito.when(calendarModel.isDayOfWeek()).thenReturn(true);
		
		Mockito.when(calendarModel.dayOfWeek()).thenReturn(specialday);
		
		final Button saveButton = (Button) fields.get("saveButton");
		
		final ComponentEventListener<?> listener = listener(saveButton);
		
		
		listener.onComponentEvent(null);
		
		Mockito.verify(dayService).save(specialday);
		
		Mockito.verify(calendarModel, Mockito.times(2)).notifyObservers(CalendarModel.Events.DatesChanged);
		
	}
	
	
	@Test
	void saveVactionsVacations() {
		prepareForButtons(ValidationErrors.FromBeforeTo);
		
		final Button saveButton = (Button) fields.get("saveButton");
		
		final ComponentEventListener<?> listener = listener(saveButton);
		
		
		listener.onComponentEvent(null);
		
		Mockito.verify(dayService, Mockito.never()).save(specialday);
		
		Mockito.verify(calendarModel, Mockito.never()).notifyObservers(CalendarModel.Events.DatesChanged);
		
	}
	@Test
	void dateValueProvider() {
		final LocalDate  date = LocalDate.now();
		final Day<?> specialday = Mockito.mock(Day.class);
		
		final String expectedDateString = date.format(DateTimeFormatter.ofPattern(CalendarModelImpl.DATE_PATTERN));
		Mockito.when(calendarModel.convert(specialday, Year.of(date.getYear()))).thenReturn(expectedDateString);
		
		assertEquals(expectedDateString, calendarView.dateValueProvider().apply(specialday));
	}
	
	@Test
	void typeValueProvider() {
		final Day<?> specialday = Mockito.mock(Day.class);
		
		Mockito.when(calendarModel.filter(specialday)).thenReturn(Filter.Vacation);
		assertEquals(I18N_DAYTYPE_VACATION,  calendarView.typeValueProvider().apply(specialday));
		
		Mockito.when(calendarModel.filter(specialday)).thenReturn(Filter.Holiday);
		assertEquals(I18N_DAYTYPE_HOLIDAY,  calendarView.typeValueProvider().apply(specialday));
		
		Mockito.when(calendarModel.filter(specialday)).thenReturn(Filter.WorkingDate);
		assertEquals(I18N_DAYTYPE_WORKINGDATE,  calendarView.typeValueProvider().apply(specialday));
		
		Mockito.when(calendarModel.filter(specialday)).thenReturn(Filter.WorkingDay);
		assertEquals(I18N_DAYTYPE_WORKINGDAY,  calendarView.typeValueProvider().apply(specialday));
		
	}
	
	
	@Test
	void dayOfWeekComboBoxListenerValidationError() {
		Mockito.when(calendarModel.validateDayofWeek(DayOfWeek.MONDAY)).thenReturn(CalendarModel.ValidationErrors.Invalid);
		
	
		final ComboBox<DayOfWeek> dayOfWeekComboBox =  dayOfWeekComboBox();
		
		dayOfWeekComboBox.setValue(DayOfWeek.MONDAY);
		
		assertEquals(I18N_CALENDAR_VALIDATION + CalendarModel.ValidationErrors.Invalid.name().toLowerCase() , dayOfWeekComboBox.getErrorMessage());
		assertTrue(dayOfWeekComboBox.isInvalid());
		
		Mockito.verify(calendarModel).assignDayOfWeek(DayOfWeek.MONDAY);
	}
	
	@Test
	void dayOfWeekComboBoxListenerValidationOk() {
		Mockito.when(calendarModel.validateDayofWeek(DayOfWeek.MONDAY)).thenReturn(CalendarModel.ValidationErrors.Ok);
		
		
		final ComboBox<DayOfWeek> dayOfWeekComboBox =  dayOfWeekComboBox();
		dayOfWeekComboBox.setInvalid(true);
		dayOfWeekComboBox.setErrorMessage(I18N_CALENDAR_VALIDATION);
		
		dayOfWeekComboBox.setValue(DayOfWeek.MONDAY);
		
		assertTrue( dayOfWeekComboBox.getErrorMessage().isEmpty());
		assertFalse(dayOfWeekComboBox.isInvalid());
		
		Mockito.verify(calendarModel).assignDayOfWeek(DayOfWeek.MONDAY);
	}
	
	

}
