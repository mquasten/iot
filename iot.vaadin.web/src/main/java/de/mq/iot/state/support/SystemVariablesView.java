package de.mq.iot.state.support;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;

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
	private final ComboBox<Object> valueComboBox = new ComboBox<>();
	private  final Button resetButton = new Button();
	private  final Button saveButton = new Button();
	private  final Label stateInfoLabel = new Label();
	
	private FormItem textFieldFormItem ; 
	private FormItem comboBoxFormItem ; 
	private final Grid<State<?>> grid = new Grid<>();
	private final Map<Class<? extends State<?>>, Consumer<StateModel>> stateCommands = new HashMap<>();
	final FormLayout formLayout = new FormLayout();
	
	private String stateInfoLabelPattern="";

	private final Converter<State<?>, String> stateValueConverter;
	
	
	SystemVariablesView(final StateService stateService, final StateModel stateModel, @Qualifier("stateValueConverter") final Converter<State<?>, String> stateValueConverter ) {
		this.stateValueConverter=stateValueConverter;
		createUI(stateService);	
		grid.asSingleSelect().addValueChangeListener(selectionEvent -> stateModel.assign(selectionEvent.getValue()));
		stateModel.register(StateModel.Events.AssignState, () ->  assignState(stateModel));	
		initStateCommands(stateModel);
		grid.setItems(stateService.states());
	}

	private void initStateCommands(StateModel stateModel) {
		stateInfoLabelPattern = "%s-Variable id=%s ändern";
		stateCommands.put(BooleanStateImpl.class, model -> initBooleanValueField(stateModel));
		stateCommands.put(ItemsStateImpl.class, model -> initListValueField(stateModel));
	}

	private void initListValueField(StateModel stateModel) {
		comboBoxFormItem.setVisible(true);	
		textFieldFormItem.setVisible(false);
		comboBoxFormItem.getElement().getStyle().set("width", "100%");
		comboBoxFormItem.getElement().getStyle().set("margin", "0");
		final ItemsStateImpl itemState = (ItemsStateImpl) stateModel.selectedState().get();
		final Map<Integer, String> items = itemState.items().stream().collect(Collectors.toMap( Entry::getKey,  Entry::getValue));
		valueComboBox.setItems(items.keySet().stream().sorted().collect(Collectors.toList()));
		valueComboBox.setItemLabelGenerator( value -> items.get(value));
		valueComboBox.setValue(itemState.value());
		stateInfoLabel.setText(String.format(stateInfoLabelPattern, "List", itemState.id()));
	}

	private void initBooleanValueField(StateModel stateModel) {
		comboBoxFormItem.setVisible(true);	
		textFieldFormItem.setVisible(false);
		comboBoxFormItem.getElement().getStyle().set("width", "100%");
		comboBoxFormItem.getElement().getStyle().set("margin", "0");
		valueComboBox.setItems(Arrays.asList(Boolean.FALSE, Boolean.TRUE));
		valueComboBox.setItemLabelGenerator( value  ->  value.toString());
		valueComboBox.setValue(stateModel.selectedState().get().value());
		stateInfoLabel.setText(String.format(stateInfoLabelPattern, "Boolean", stateModel.selectedState().get().id()));
	}

	private void initTextValueField(final StateModel stateModel) {
		textFieldFormItem.setVisible(true);
		comboBoxFormItem.setVisible(false);	
		valueTextField.setReadOnly(false);
		final String variableName = stateModel.selectedState().get().value().getClass().getSimpleName();
		valueTextField.setValue( stateValueConverter.convert(stateModel.selectedState().get()));
		stateInfoLabel.setText(String.format(stateInfoLabelPattern,variableName, stateModel.selectedState().get().id()));
	}

	private void createUI(StateService stateService) {
		saveButton.setText("speichern");
		resetButton.setText("verwerfen");
		saveButton.setEnabled(false);
		resetButton.setEnabled(false);
		
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
		grid.addColumn((ValueProvider<State<?>, String>) state -> stateValueConverter.convert(state)).setHeader("Wert").setResizable(true);
		grid.setSelectionMode(SelectionMode.SINGLE);
		
		add(layout, stateInfoLabel, editorLayout);
		setHorizontalComponentAlignment(Alignment.CENTER, stateInfoLabel);
	
		
		layout.setSizeFull();
	
		setHorizontalComponentAlignment(Alignment.CENTER, layout);
	
	
		grid.setHeight("50vH");
		
		
	
	
		
	}
	
	private void assignState(final StateModel stateModel) {
		if(stateModel.selectedState().isPresent()) {
			nameTextField.setValue(stateModel.selectedState().get().name());
			lastUpdateTextField.setValue(stateModel.selectedState().get().lastupdate().toString());
			final Optional<Consumer<StateModel>> consumer = Optional.ofNullable(stateCommands.get(stateModel.selectedState().get().getClass()));
			
			
			
			consumer.orElse( value -> initTextValueField(stateModel)).accept(stateModel);
			
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
