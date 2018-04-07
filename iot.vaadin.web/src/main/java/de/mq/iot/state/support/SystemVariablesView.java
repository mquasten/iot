package de.mq.iot.state.support;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
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

import de.mq.iot.state.StateService;

@Route("")
@Theme(Lumo.class)
class SystemVariablesView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	private final TextField nameTextField = new TextField();
	private final TextField lastUpdateTextField = new TextField();
	private final TextField valueTextField = new TextField();
	private final ComboBox<Integer> valueComboBox = new ComboBox<>();
	private  final Button resetButton = new Button();
	private  final Button saveButton = new Button();
	private  final Label stateInfoLabel = new Label();
	
	private FormItem textFieldFormItem ; 
	private FormItem comboBoxFormItem ; 
	
	private final Map<Class<? extends State<?>>,String> stateNameKeys = new HashMap<>();
	final FormLayout formLayout = new FormLayout();

	SystemVariablesView(StateService stateService, StateModel stateModel) {
	
		saveButton.setText("speichern");
		resetButton.setText("verwerfen");
		saveButton.setEnabled(false);
		resetButton.setEnabled(false);
		final Grid<State<?>> grid = new Grid<>(50);
		final HorizontalLayout layout = new HorizontalLayout(grid);
		grid.getElement().getStyle().set("overflow", "auto");
		

	
		nameTextField.setSizeFull();
		nameTextField.setReadOnly(true);
	
		lastUpdateTextField.setSizeFull();
		lastUpdateTextField.setReadOnly(true);
		
		valueTextField.setSizeFull();
		valueComboBox.setSizeFull();
		valueTextField.setReadOnly(true);
		formLayout.addFormItem(nameTextField, "Name");
		formLayout.addFormItem(lastUpdateTextField, "Änderungsdatum");
		textFieldFormItem=formLayout.addFormItem(valueTextField, "Wert");
		comboBoxFormItem=formLayout.addFormItem(valueComboBox, "Wert");
		comboBoxFormItem.setVisible(false);
		// valueTextField.setErrorMessage("sucks");
		 //valueTextField.setInvalid(true);

		

		formLayout.setSizeFull();

		formLayout.setResponsiveSteps(new ResponsiveStep("95vH", 1));

		

		final VerticalLayout buttonLayout = new VerticalLayout(resetButton, saveButton);

		final HorizontalLayout editorLayout = new HorizontalLayout(formLayout, buttonLayout);

		editorLayout.setVerticalComponentAlignment(Alignment.CENTER, buttonLayout);

		editorLayout.setSizeFull();
		
		
		grid.addColumn((ValueProvider<State<?>, Long>) state -> state.id()).setVisible(false);
		grid.addColumn((ValueProvider<State<?>, String>) state -> state.name()).setHeader("Name").setResizable(true);
		grid.addColumn((ValueProvider<State<?>, String>) state -> String.valueOf(state.value())).setHeader("Wert").setResizable(true);
		grid.setSelectionMode(SelectionMode.SINGLE);
		
		add(layout, stateInfoLabel, editorLayout);
		setHorizontalComponentAlignment(Alignment.CENTER, stateInfoLabel);
	
		
		layout.setSizeFull();
	
		setHorizontalComponentAlignment(Alignment.CENTER, layout);
	
	
		grid.setHeight("50vH");
		
		
	
		grid.setItems(stateService.states());
		
		
		grid.asSingleSelect().addValueChangeListener(selectionEvent -> stateModel.assign(selectionEvent.getValue()));

	
		stateModel.register(StateModel.Events.AssignState, () ->  assignState(stateModel));
		
		stateNameKeys.put(BooleanStateImpl.class, "Boolean");
		stateNameKeys.put(DoubleStateImpl.class, "Double");
		stateNameKeys.put(ItemsStateImpl.class, "Text");
		stateNameKeys.put(StringStateImpl.class, "List");
		
		
	}
	
	private void assignState(final StateModel stateModel) {
		if(stateModel.selectedState().isPresent()) {
			
			nameTextField.setValue(stateModel.selectedState().get().name());
			lastUpdateTextField.setValue(stateModel.selectedState().get().lastupdate().toString());
			
		
			//valueComboBox.setSizeFull();
		
			textFieldFormItem.setVisible(false);
			comboBoxFormItem.setVisible(true);	
			
			comboBoxFormItem.getElement().getStyle().set("width", "100%");
			comboBoxFormItem.getElement().getStyle().set("margin", "0");
				
		
			valueTextField.setValue(stateModel.selectedState().get().value().toString());
			
			formLayout.setSizeFull();
			valueTextField.setReadOnly(false);
			valueComboBox.setReadOnly(false);
		
			
		
		
		//	
			
			
			stateInfoLabel.setText(String.format("%s-Variable id=%s ändern", stateNameKeys.get(stateModel.selectedState().get().getClass()), stateModel.selectedState().get().id()));
		
			
			saveButton.setEnabled(true);
			resetButton.setEnabled(true);
			return;
		}
	
		stateInfoLabel.setText("Variable bearbeiten");
		nameTextField.setValue("");
		lastUpdateTextField.setValue("");
		lastUpdateTextField.setValue("");
		valueTextField.setValue("");
		valueTextField.setReadOnly(true);
		stateInfoLabel.setText("");
		comboBoxFormItem.setVisible(false);
		textFieldFormItem.setVisible(true);
		resetButton.setEnabled(false);
		saveButton.setEnabled(false);
	
	}
	
	


}
