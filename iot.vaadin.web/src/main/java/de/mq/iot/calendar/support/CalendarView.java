package de.mq.iot.calendar.support;


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
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import de.mq.iot.model.LocalizeView;
import de.mq.iot.state.support.State;
import de.mq.iot.support.ButtonBox;

@Route("calendar")
@Theme(Lumo.class)
//@I18NKey("systemvariables_")
class  CalendarView extends VerticalLayout implements LocalizeView {
	
	

	
	private static final long serialVersionUID = 1L;
	
	private final Label nameLabel = new Label(); 
	private final TextField nameTextField = new TextField();
	
	private final Label dateLabel = new Label(); 
	private final TextField lastUpdateTextField = new TextField();
	private final TextField valueTextField = new TextField();
	private final ComboBox<Object> valueComboBox = new ComboBox<>();

	private  final Button resetButton = new Button();
	
	private  final Button saveButton = new Button();
	
	private  final Label stateInfoLabel = new Label();
	

	


	
	
	

	private final Grid<State<?>> grid = new Grid<>();
	
	
	private final FormLayout formLayout = new FormLayout();
	
	



	
	CalendarView(final ButtonBox buttonBox ) {
	

	
		
	
		
	   createUI(buttonBox);
	
		
	
		
	}



	
	
	


	private void createUI(final ButtonBox buttonBox) {
				
	
		saveButton.setEnabled(false);
		resetButton.setEnabled(false);
		
		valueComboBox.setRequired(true);
		valueTextField.setRequired(true);
	
		
		final HorizontalLayout layout = new HorizontalLayout(grid);
		grid.getElement().getStyle().set("overflow", "auto");
		

	
		nameTextField.setSizeFull();
		nameTextField.setReadOnly(true);
	
		lastUpdateTextField.setSizeFull();
		lastUpdateTextField.setReadOnly(true);
		
		valueTextField.setSizeFull();
		valueComboBox.setSizeFull();
		valueTextField.setReadOnly(true);
		formLayout.addFormItem(nameTextField, nameLabel);
		formLayout.addFormItem(lastUpdateTextField, dateLabel);
		
		
		

		formLayout.setSizeFull();

		formLayout.setResponsiveSteps(new ResponsiveStep("10vH",1));

		

		final VerticalLayout buttonLayout = new VerticalLayout(resetButton, saveButton);

		
		
		final HorizontalLayout editorLayout = new HorizontalLayout(formLayout, buttonLayout);

		editorLayout.setVerticalComponentAlignment(Alignment.CENTER, buttonLayout);

		editorLayout.setSizeFull();
		

		grid.setSelectionMode(SelectionMode.SINGLE);
		
		
		
		add(buttonBox, layout, stateInfoLabel, editorLayout);
		setHorizontalComponentAlignment(Alignment.CENTER, stateInfoLabel);
	
		
		layout.setSizeFull();
	
		setHorizontalComponentAlignment(Alignment.CENTER, layout);
	
		
	
	   grid.setHeight("50vH");
	 
		
		
	}


	
	



	


	


	


	
	
	
	

}

