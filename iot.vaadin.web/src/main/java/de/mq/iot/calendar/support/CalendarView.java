package de.mq.iot.calendar.support;

import java.time.DayOfWeek;
import java.time.Year;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.context.MessageSource;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.calendar.support.CalendarModel.Events;
import de.mq.iot.calendar.support.CalendarModel.Filter;
import de.mq.iot.calendar.support.CalendarModel.ValidationErrors;
import de.mq.iot.model.I18NKey;
import de.mq.iot.model.LocalizeView;
import de.mq.iot.support.ButtonBox;

@Route("calendar")
@Theme(Lumo.class)
@I18NKey("calendar_")
class CalendarView extends VerticalLayout implements LocalizeView {

	private static final long serialVersionUID = 1L;

	@I18NKey("range_from")
	private final Label fromLabel = new Label();
	private final TextField fromTextField = new TextField();

	@I18NKey("range_to")
	private final Label toLabel = new Label();
	private final TextField toTextField = new TextField();
	
	@I18NKey("dayofweek")
	private final Label dayOfWeekLabel = new Label();
	
	private  ComboBox<DayOfWeek> dayOfWeekComboBox = new ComboBox<>();

	@I18NKey("delete_range")
	private final Button deleteButton = new Button();

	@I18NKey("add_range")
	private final Button saveButton = new Button();

	@I18NKey("info")
	private final Label stateInfoLabel = new Label();

	@I18NKey("table_type_header")
	private final Label typeColumnLabel = new Label();

	
	
	
	
	private ComboBox<Filter> filtersComboBox =  new ComboBox<Filter>();
	
	private final Grid<Specialday> grid = new Grid<>();

	private final FormLayout formLayout = new FormLayout();

	private final CalendarModel calendarModel;

	private final MessageSource messageSource;
	
	private Map<ValidationErrors, String> validationErrors = new HashMap<>();

	CalendarView(final CalendarModel calendarModel, final SpecialdayService specialdayService, final MessageSource messageSource, final ButtonBox buttonBox) {

		this.messageSource = messageSource;

		this.calendarModel = calendarModel;

		createUI(specialdayService, buttonBox);

		calendarModel.notifyObservers(CalendarModel.Events.ChangeLocale);
		

	}

	private void createUI(final SpecialdayService specialdayService, final ButtonBox buttonBox) {

		dayOfWeekComboBox.setVisible(false);
		dayOfWeekLabel.setVisible(false);
		saveButton.setEnabled(false);
		deleteButton.setEnabled(false);

		dayOfWeekComboBox.setItems(calendarModel.daysOfWeek());
	
		filtersComboBox.setItems(Arrays.asList(Filter.values()));
		filtersComboBox.setAllowCustomValue(false);
	
		filtersComboBox.setValue(Filter.Vacation);

		final HorizontalLayout layout = new HorizontalLayout(grid);
		grid.getElement().getStyle().set("overflow", "auto");

		fromTextField.setSizeFull();

		toTextField.setSizeFull();

		formLayout.addFormItem(fromTextField, fromLabel);
		formLayout.addFormItem(toTextField, toLabel);
		formLayout.addFormItem(dayOfWeekComboBox, dayOfWeekLabel);
		dayOfWeekComboBox.getElement().getStyle().set("width", "100%");

		formLayout.setSizeFull();

		formLayout.setResponsiveSteps(new ResponsiveStep("10vH", 1));

		final VerticalLayout buttonLayout = new VerticalLayout(deleteButton, saveButton);

		final HorizontalLayout editorLayout = new HorizontalLayout(formLayout, buttonLayout);
		

		editorLayout.setVerticalComponentAlignment(Alignment.CENTER, buttonLayout);

		editorLayout.setSizeFull();
		
		setVisibleIfCalendarRoleAware(editorLayout);
		

		grid.setSelectionMode(SelectionMode.SINGLE);

		add(buttonBox, layout, stateInfoLabel, editorLayout);
		setHorizontalComponentAlignment(Alignment.CENTER, stateInfoLabel);
	
		layout.setSizeFull();

		setHorizontalComponentAlignment(Alignment.CENTER, layout);

		grid.setHeight("50vH");

		grid.addColumn(dateValueProvider()).setResizable(true).setHeader(filtersComboBox);
		grid.addColumn(typeValueProvider()).setHeader(typeColumnLabel).setResizable(true);
		
		
		grid.setSelectionMode(SelectionMode.SINGLE);
		
	
		
		fromTextField.addValueChangeListener(value -> {
			
			
			fromTextField.setErrorMessage("" );
			fromTextField.setInvalid(false);
			final ValidationErrors error = calendarModel.validateFrom(value.getValue());
			if( error != ValidationErrors.Ok) {
				fromTextField.setInvalid(true);
				fromTextField.setErrorMessage(validationErrors.get(error));
				return;
			}
			
			calendarModel.assignFrom(value.getValue());
		    
		});
		
		
		toTextField.addValueChangeListener(value -> {
		
			toTextField.setErrorMessage("" );
			toTextField.setInvalid(false);
			final ValidationErrors error = calendarModel.validateTo(value.getValue());
			if( error != ValidationErrors.Ok) {
				toTextField.setInvalid(true);
				toTextField.setErrorMessage(validationErrors.get(error));
				return;
			}
			
			calendarModel.assignTo(value.getValue());
			
		
		});
		
		
		dayOfWeekComboBox.addValueChangeListener(event -> {
			
			dayOfWeekComboBox.setErrorMessage("" );
			dayOfWeekComboBox.setInvalid(false);
			final ValidationErrors error = calendarModel.validateDayofWeek(event.getValue());
			if( error != ValidationErrors.Ok) {
				dayOfWeekComboBox.setInvalid(true);
				dayOfWeekComboBox.setErrorMessage(validationErrors.get(error));
			}
			calendarModel.assignDayOfWeek(event.getValue());
			
		});

		calendarModel.register(CalendarModel.Events.ChangeLocale, () -> {
			localize(messageSource, calendarModel.locale());
			
			Arrays.asList(ValidationErrors.values()).stream().filter(validationError -> validationError!= ValidationErrors.Ok).forEach(validationError -> validationErrors.put( validationError, messageSource.getMessage("calendar_validation_" + validationError.name().toLowerCase() , null, "???", calendarModel.locale())));
			
		});
		
		calendarModel.register(CalendarModel.Events.ValuesChanged, () -> {	
			deleteButton.setEnabled(calendarModel.valid());
			saveButton.setEnabled(calendarModel.valid());
			
			
			
		});
		
		calendarModel.register(CalendarModel.Events.DatesChanged, () -> {
			grid.setItems(readDates(specialdayService));
			setEditorFieldsVisible(calendarModel.isDayOfWeek());
			
		});
		

		filtersComboBox.addValueChangeListener(event -> {
			
			calendarModel.assign(event.getValue());
			
			//grid.setItems(readDates(specialdayService));
			
		});
		
		
		deleteButton.addClickListener(event -> process(specialday -> specialdayService.delete(specialday), specialdayService));
		
		
		saveButton.addClickListener(event -> process(specialday -> specialdayService.save(specialday), specialdayService)); 
		calendarModel.assign(CalendarModel.Filter.Vacation);
			
	}

	private void setEditorFieldsVisible(final boolean isDayOfWeek) {
		fromTextField.setVisible(! isDayOfWeek);
		toTextField.setVisible(! isDayOfWeek);
		fromLabel.setVisible(! isDayOfWeek);
		toLabel.setVisible(! isDayOfWeek);
		dayOfWeekComboBox.setVisible(isDayOfWeek);
		dayOfWeekLabel.setVisible(isDayOfWeek);	;
	}

	private void setVisibleIfCalendarRoleAware(final HorizontalLayout editorLayout) {
		editorLayout.setVisible(calendarModel.isChangeCalendarAllowed());
		stateInfoLabel.setVisible(calendarModel.isChangeCalendarAllowed());
	}

	private void process(final Consumer<Specialday> consumer, final SpecialdayService specialdayService) {
		
		final ValidationErrors error = calendarModel.vaidate(60);
		if ( error != ValidationErrors.Ok ) {
			toTextField.setErrorMessage(validationErrors.get(error));
			toTextField.setInvalid(true);
			return;
		} 
		specialdayService.vacation(calendarModel.from(),  calendarModel.to()).forEach(day -> consumer.accept(day));
		
	
		//grid.setItems(readDates(specialdayService));
		
		calendarModel.notifyObservers(CalendarModel.Events.DatesChanged);
		
		toTextField.setValue("");
		fromTextField.setValue("");
		fromTextField.setValue("");
		toTextField.setInvalid(false);
		fromTextField.setInvalid(false);
		toTextField.setErrorMessage("");
		fromTextField.setErrorMessage("");
		calendarModel.assignFrom(null);
		calendarModel.assignTo(null);
		calendarModel.notifyObservers(Events.DatesChanged);
	}

	private Collection<Specialday> readDates(final SpecialdayService specialdayService) {
	
		return specialdayService.specialdays(calendarModel.filter());
		
	}
	
	ValueProvider<Specialday, String> dateValueProvider() {
		return specialday -> calendarModel.convert(specialday, Year.now());
	}
	
	ValueProvider<Specialday, String> typeValueProvider() {
		return specialday -> specialday.type().name();
	}

	
}
