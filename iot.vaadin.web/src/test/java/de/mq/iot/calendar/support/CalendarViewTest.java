package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Arrays;
import java.util.Collection;
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
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.calendar.support.CalendarModel.Events;
import de.mq.iot.calendar.support.CalendarModel.ValidationErrors;
import de.mq.iot.model.Observer;
import de.mq.iot.support.ButtonBox;

class CalendarViewTest {
	
	private static final String I18N_CALENDAR_VALIDATION = "calendar_validation_";


	private static final String I18N_CALENDAR_DELETE_RANGE = "calendar_delete_range";
	
	
	private static final String I18N_CALENDAR_ADD_RANGE = "calendar_add_range";
	
	private static final String I18N_CALENDAR_TABLE_HEADER  = "calendar_table_header";
	
	private static final String I18N_CALENDAR_INFO = "calendar_info";
	
	private static final String I18N_CALENDAR_RANGE_FROM ="calendar_range_from";
	private static final String I18N_CALENDAR_RANGE_TO ="calendar_range_to";

	private final CalendarModel calendarModel = Mockito.mock(CalendarModel.class);
	
	private final SpecialdayService specialdayService = Mockito.mock(SpecialdayService.class);
	
	private final MessageSource messageSource = Mockito.mock(MessageSource.class);
	
	private CalendarView calendarView;
	
	private final Map<String, Object> fields = new HashMap<>();
	
	private final Specialday  specialday = Mockito.mock(Specialday.class);
	
	private final Map<CalendarModel.Events, Observer> observers = new HashMap<>();
	
	@BeforeEach
	void setup() {
		Mockito.when(calendarModel.locale()).thenReturn(Locale.GERMAN);
		Arrays.asList(I18N_CALENDAR_DELETE_RANGE, I18N_CALENDAR_ADD_RANGE, I18N_CALENDAR_TABLE_HEADER, I18N_CALENDAR_INFO, I18N_CALENDAR_RANGE_FROM,I18N_CALENDAR_RANGE_TO , I18N_CALENDAR_VALIDATION + ValidationErrors.Invalid.name().toLowerCase()).forEach(key -> Mockito.doReturn(key).when(messageSource).getMessage(key, null, "???", Locale.GERMAN));
		
		 Mockito.doReturn((Predicate<Specialday>) day -> true).when(calendarModel).filter();
		
		Mockito.doAnswer(answer -> {

			final CalendarModel.Events event = (CalendarModel.Events ) answer.getArguments()[0];
			final Observer observer = (Observer) answer.getArguments()[1];
			observers.put(event, observer);
			return null;

		}).when(calendarModel).register(Mockito.any(), Mockito.any());
		
		
		Mockito.doAnswer(answer-> { 
			   observers.get(Events.DatesChanged).process();
			   return null;
		   }).when(calendarModel).assign(CalendarModel.Filter.All);
		   
		
		
		Mockito.when(specialday.date(Year.now().getValue())).thenReturn(LocalDate.of(1968, Month.MAY, 28));
		Mockito.when(specialdayService.specialdays(Year.now())).thenReturn(Arrays.asList(specialday));
		
		
		
		calendarView = new CalendarView(calendarModel, specialdayService, messageSource,  new ButtonBox());
		
		Arrays.asList(CalendarView.class.getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).forEach(field -> fields.put(field.getName(), ReflectionTestUtils.getField(calendarView, field.getName())));
	
	    assertEquals(14, fields.size() ); 
	    
	    
	    
	    
	   assertEquals(3, observers.size());
	   
	   observers.get(CalendarModel.Events.ChangeLocale).process();
	   
	   
	   
	  

	}
	
	
	@Test
	void init() {
		Mockito.verify(specialdayService).specialdays(Year.now());
		
		final Grid<?> grid = (Grid<?>) fields.get("grid");
		final ListDataProvider<?>  dates = (ListDataProvider<?>) grid.getDataProvider();
		assertEquals(1, dates.getItems().size());
		assertNotNull(specialday.date(Year.now().getValue()));
		assertEquals(specialday.date(Year.now().getValue()), dates.getItems().stream().findFirst().get());
		
		
		final Checkbox vacationOnlyCheckbox   = (Checkbox) fields.get("vacationOnlyCheckbox");
		assertFalse(vacationOnlyCheckbox.getValue());
		
		
		
		final Button deleteButton = (Button) fields.get("deleteButton");
		assertFalse(deleteButton.isEnabled());
		
		final Button saveButton = (Button) fields.get("saveButton");
		assertFalse(saveButton.isEnabled());
		final Label stateInfoLabel = (Label) fields.get("stateInfoLabel");
		
		
		final Label fromLabel = (Label) fields.get("fromLabel");
		final Label toLabel = (Label) fields.get("toLabel");
		
		assertEquals(I18N_CALENDAR_DELETE_RANGE, deleteButton.getText());
		assertEquals(I18N_CALENDAR_ADD_RANGE, saveButton.getText());
		assertEquals(I18N_CALENDAR_TABLE_HEADER, vacationOnlyCheckbox.getLabel());
		assertEquals(I18N_CALENDAR_INFO, stateInfoLabel.getText());
		assertEquals(I18N_CALENDAR_RANGE_FROM, fromLabel.getText());
		assertEquals(I18N_CALENDAR_RANGE_TO, toLabel.getText());
		
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
	
	@SuppressWarnings("unchecked")
	private ComponentEventListener<?> listener(final Component saveButton) {
		final ComponentEventBus eventBus = (ComponentEventBus) ReflectionTestUtils.getField(saveButton, "eventBus");
		final Map<Class<?>, ?> map = (Map<Class<?>, ?>) ReflectionTestUtils.getField(eventBus, "componentEventData");
		return DataAccessUtils.requiredSingleResult((Collection<ComponentEventListener<?>>) ReflectionTestUtils.getField(map.values().iterator().next(), "listeners"));
	}

}
