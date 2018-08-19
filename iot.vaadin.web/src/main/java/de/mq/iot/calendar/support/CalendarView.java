package de.mq.iot.calendar.support;

import java.time.LocalDate;
import java.time.Year;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.ColumnBase;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
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

	@I18NKey("delete_range")
	private final Button deleteButton = new Button();

	@I18NKey("add_range")
	private final Button saveButton = new Button();

	@I18NKey("info")
	private final Label stateInfoLabel = new Label();

	@I18NKey("table_header")
	private final Label dateColumnLabel = new Label();

	private Checkbox vacationOnlyCheckbox = new Checkbox();
	
	private final Grid<LocalDate> grid = new Grid<>();

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

		
		saveButton.setEnabled(false);
		deleteButton.setEnabled(false);

		
	

		final HorizontalLayout layout = new HorizontalLayout(grid);
		grid.getElement().getStyle().set("overflow", "auto");

		fromTextField.setSizeFull();

		toTextField.setSizeFull();

		formLayout.addFormItem(fromTextField, fromLabel);
		formLayout.addFormItem(toTextField, toLabel);

		formLayout.setSizeFull();

		formLayout.setResponsiveSteps(new ResponsiveStep("10vH", 1));

		final VerticalLayout buttonLayout = new VerticalLayout(deleteButton, saveButton);

		final HorizontalLayout editorLayout = new HorizontalLayout(formLayout, buttonLayout);
		

		editorLayout.setVerticalComponentAlignment(Alignment.CENTER, buttonLayout);

		editorLayout.setSizeFull();

		grid.setSelectionMode(SelectionMode.SINGLE);

		add(buttonBox, layout, stateInfoLabel, editorLayout);
		setHorizontalComponentAlignment(Alignment.CENTER, stateInfoLabel);
	
		layout.setSizeFull();

		setHorizontalComponentAlignment(Alignment.CENTER, layout);

		grid.setHeight("50vH");

		final ColumnBase<Column<LocalDate>> dateColumnBase = grid.addColumn((ValueProvider<LocalDate, String>) date -> date.getDayOfMonth() + "." + date.getMonthValue() + "." + date.getYear()).setResizable(true);
		grid.setSelectionMode(SelectionMode.SINGLE);
		
		grid.setItems(readDates(specialdayService));
		
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

		calendarModel.register(CalendarModel.Events.ChangeLocale, () -> {
			localize(messageSource, calendarModel.locale());
		
			
			dateColumnBase.setHeader(vacationOnlyCheckbox);
			
			Arrays.asList(ValidationErrors.values()).stream().filter(validationError -> validationError!= ValidationErrors.Ok).forEach(validationError -> validationErrors.put( validationError, messageSource.getMessage("calendar_validation_" + validationError.name().toLowerCase() , null, "???", calendarModel.locale())));
			vacationOnlyCheckbox.setLabel(dateColumnLabel.getText());
			
		});
		
		calendarModel.register(CalendarModel.Events.ValuesChanged, () -> {	
			deleteButton.setEnabled(calendarModel.valid());
			saveButton.setEnabled(calendarModel.valid());
		});
		

		
		deleteButton.addClickListener(event -> process(specialday -> specialdayService.delete(specialday), specialdayService));
		
		
		saveButton.addClickListener(event -> process(specialday -> specialdayService.save(specialday), specialdayService)); 
			
			
	}

	private void process(final Consumer<Specialday> consumer, final SpecialdayService specialdayService) {
		
		
		final ValidationErrors error = calendarModel.vaidate(60);
		if ( error != ValidationErrors.Ok ) {
			toTextField.setErrorMessage(validationErrors.get(error));
			toTextField.setInvalid(true);
			return;
		} 
		specialdayService.vacation(calendarModel.from(),  calendarModel.to()).forEach(day -> consumer.accept(day));
		
	
		grid.setItems(readDates(specialdayService));
		
		toTextField.setValue("");
		fromTextField.setValue("");
		fromTextField.setValue("");
		toTextField.setInvalid(false);
		fromTextField.setInvalid(false);
		toTextField.setErrorMessage("");
		fromTextField.setErrorMessage("");
		calendarModel.assignFrom(null);
		calendarModel.assignTo(null);
	}

	private List<LocalDate> readDates(final SpecialdayService specialdayService) {
		return specialdayService.specialdays(Year.now()).stream().map(day -> day.date(Year.now().getValue())).sorted().collect(Collectors.toList());
	}

	
}
