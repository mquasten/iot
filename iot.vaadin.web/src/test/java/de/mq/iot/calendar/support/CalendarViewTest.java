package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.Specialday.Type;
import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.calendar.support.CalendarModel.Events;
import de.mq.iot.calendar.support.CalendarModel.Filter;
import de.mq.iot.calendar.support.CalendarModel.ValidationErrors;
import de.mq.iot.model.Observer;
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

	private final CalendarModel calendarModel = Mockito.mock(CalendarModel.class);
	
	private final SpecialdayService specialdayService = Mockito.mock(SpecialdayService.class);
	
	private final MessageSource messageSource = Mockito.mock(MessageSource.class);
	
	private CalendarView calendarView;
	
	private final Map<String, Object> fields = new HashMap<>();
	
	private final Specialday  specialday = Mockito.mock(Specialday.class);
	
	private final Map<CalendarModel.Events, Observer> observers = new HashMap<>();
	
	@BeforeEach
	void setup() {
		
		Mockito.when(calendarModel.isChangeCalendarAllowed()).thenReturn(true);
		Mockito.when(calendarModel.locale()).thenReturn(Locale.GERMAN);
		Arrays.asList(I18N_CALENDAR_DELETE_RANGE, I18N_CALENDAR_ADD_RANGE, I18N_CALENDAR_TABLE_HEADER, I18N_CALENDAR_INFO, I18N_CALENDAR_RANGE_FROM,I18N_CALENDAR_RANGE_TO, I18N_CALENDAR_DAY_OF_WEEK , I18N_CALENDAR_VALIDATION + ValidationErrors.Invalid.name().toLowerCase()).forEach(key -> Mockito.doReturn(key).when(messageSource).getMessage(key, null, "???", Locale.GERMAN));
		
		 Mockito.doReturn(Arrays.asList(Specialday.Type.Vacation)).when(calendarModel).filter();
		
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
		   
		
		
		Mockito.when(specialday.date(Year.now().getValue())).thenReturn(LocalDate.of(1968, Month.MAY, 28));
		Mockito.when(specialdayService.specialdays(Mockito.anyCollection())).thenReturn(Arrays.asList(specialday));
		
		
		
		calendarView = new CalendarView(calendarModel, specialdayService, messageSource,  new ButtonBox());
		
		Arrays.asList(CalendarView.class.getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).forEach(field -> fields.put(field.getName(), ReflectionTestUtils.getField(calendarView, field.getName())));
	
	    assertEquals(16, fields.size() ); 
	    
	    
	    
	    
	   assertEquals(3, observers.size());
	   
	   observers.get(CalendarModel.Events.ChangeLocale).process();
	   
	   
	   
	  

	}
	
	
	@Test
	void init() {
		Mockito.verify(specialdayService).specialdays(Arrays.asList(Type.Vacation));
		
		final Grid<?> grid = (Grid<?>) fields.get("grid");
		final ListDataProvider<?>  dates = (ListDataProvider<?>) grid.getDataProvider();
		assertEquals(1, dates.getItems().size());
		assertNotNull(specialday.date(Year.now().getValue()));
		assertEquals(specialday, dates.getItems().stream().findFirst().get());
		
		
		@SuppressWarnings("unchecked")
		final ComboBox<Filter> vacationOnlyCheckbox   = (ComboBox<Filter>) fields.get("filtersComboBox");
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
		final ComboBox<?> dayOfWeekComboBox = (ComboBox<?>) fields.get("dayOfWeekComboBox");
		assertEquals(I18N_CALENDAR_DELETE_RANGE, deleteButton.getText());
		assertEquals(I18N_CALENDAR_ADD_RANGE, saveButton.getText());
		
		
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
		
		@SuppressWarnings("unchecked")
		final ComboBox<Filter> filtersComboBox =  (ComboBox<Filter>) fields.get("filtersComboBox");
		assertEquals(Filter.Vacation, filtersComboBox.getValue());

		
		filtersComboBox.setValue(Filter.WorkingDate);
		
		Mockito.verify(calendarModel).assign(Filter.WorkingDate);
		
		filtersComboBox.setValue(Filter.Vacation);
		
		Mockito.verify(calendarModel, Mockito.atLeast(1)).assign(Filter.Vacation);
		
		
	}
	
	@Test
	void deleteVactions() {
		prepareForButtons(ValidationErrors.Ok);
		
		final Button deleteButton = (Button) fields.get("deleteButton");
		
		final ComponentEventListener<?> listener = listener(deleteButton);
		
		
		listener.onComponentEvent(null);
		
		Mockito.verify(specialdayService).delete(specialday);
		
		Mockito.verify(calendarModel, Mockito.times(2)).notifyObservers(CalendarModel.Events.DatesChanged);
		
	}
	
	@Test
	void deleteVactionsValidationError() {
		prepareForButtons(ValidationErrors.FromBeforeTo);
		
		final Button deleteButton = (Button) fields.get("deleteButton");
		
		final ComponentEventListener<?> listener = listener(deleteButton);
		
		
		listener.onComponentEvent(null);
		
		Mockito.verify(specialdayService, Mockito.never()).delete(specialday);
		
		Mockito.verify(calendarModel, Mockito.never()).notifyObservers(CalendarModel.Events.DatesChanged);
		
	}



	private void prepareForButtons(ValidationErrors error) {
		final LocalDate from = LocalDate.now();
		final LocalDate to = LocalDate.now().plusDays(1);
		
		Mockito.when(calendarModel.from()).thenReturn(from);
		Mockito.when(calendarModel.to()).thenReturn(to);
		
		Mockito.when(specialdayService.vacation(from, to)).thenReturn(Arrays.asList(specialday));
		
		
		Mockito.when(calendarModel.vaidate(Mockito.anyInt())).thenReturn(error);
	}
	
	@SuppressWarnings("unchecked")
	private ComponentEventListener<?> listener(final Component saveButton) {
		final ComponentEventBus eventBus = (ComponentEventBus) ReflectionTestUtils.getField(saveButton, "eventBus");
		final Map<Class<?>, ?> map = (Map<Class<?>, ?>) ReflectionTestUtils.getField(eventBus, "componentEventData");
		return DataAccessUtils.requiredSingleResult((Collection<ComponentEventListener<?>>) ReflectionTestUtils.getField(map.values().iterator().next(), "listeners"));
	}
	
	
	@Test
	void saveVactions() {
		prepareForButtons(ValidationErrors.Ok);
		
		final Button saveButton = (Button) fields.get("saveButton");
		
		final ComponentEventListener<?> listener = listener(saveButton);
		
		
		listener.onComponentEvent(null);
		
		Mockito.verify(specialdayService).save(specialday);
		
		Mockito.verify(calendarModel, Mockito.times(2)).notifyObservers(CalendarModel.Events.DatesChanged);
		
	}
	
	
	@Test
	void saveVactionsVacations() {
		prepareForButtons(ValidationErrors.FromBeforeTo);
		
		final Button saveButton = (Button) fields.get("saveButton");
		
		final ComponentEventListener<?> listener = listener(saveButton);
		
		
		listener.onComponentEvent(null);
		
		Mockito.verify(specialdayService, Mockito.never()).save(specialday);
		
		Mockito.verify(calendarModel, Mockito.never()).notifyObservers(CalendarModel.Events.DatesChanged);
		
	}
	@Test
	void dateValueProvider() {
		final LocalDate  date = LocalDate.now();
		final Specialday specialday = Mockito.mock(Specialday.class);
		
		final String expectedDateString = date.format(DateTimeFormatter.ofPattern(CalendarModelImpl.DATE_PATTERN));
		Mockito.when(calendarModel.convert(specialday, Year.of(date.getYear()))).thenReturn(expectedDateString);
		
		assertEquals(expectedDateString, calendarView.dateValueProvider().apply(specialday));
	}
	
	@Test
	void typeValueProvider() {
		final Specialday specialday = Mockito.mock(Specialday.class);
		Mockito.when(specialday.type()).thenReturn(Type.Vacation);
		
		assertEquals(Type.Vacation.name(),  calendarView.typeValueProvider().apply(specialday));
		
	}
	
	

}
