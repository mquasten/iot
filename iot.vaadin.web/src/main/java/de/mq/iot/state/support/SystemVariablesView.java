package de.mq.iot.state.support;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

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

import de.mq.iot.model.I18NKey;
import de.mq.iot.model.LocalizeView;
import de.mq.iot.state.State;
import de.mq.iot.state.StateService;
import de.mq.iot.state.support.StateModel.ValidationErrors;
import de.mq.iot.support.ButtonBox;

@Route("")
@Theme(Lumo.class)
@I18NKey("systemvariables_")
class  SystemVariablesView extends VerticalLayout implements LocalizeView {
	
	private final SimpleNotificationDialog notificationDialog;

	static final String I18N_INFO_LABEL_PATTERN = "systemvariables_info";
	private static final String I18N_VALUE_NOT_CHANGED = "systemvariables_notchanged";
	private static final String I18N_VALUE_MANDATORY = "systemvariables_mandatory";
	static final String I18N_VALUE_INVALID = "systemvariables_invalid";
	static final String I18N_ERROR = "systemvariables_error";
	private static final long serialVersionUID = 1L;
	@I18NKey("name")
	private final Label nameLabel = new Label(); 
	private final TextField nameTextField = new TextField();
	@I18NKey("date")
	private final Label dateLabel = new Label(); 
	private final TextField lastUpdateTextField = new TextField();
	private final TextField valueTextField = new TextField();
	private final ComboBox<Object> valueComboBox = new ComboBox<>();
	@I18NKey("reset")
	private  final Button resetButton = new Button();
	@I18NKey("save")
	private  final Button saveButton = new Button();
	
	private  final Label stateInfoLabel = new Label();
	
	@I18NKey("value")
	private final Label textValueLabel = new Label();
	
	@I18NKey("value")
	private final Label listValueLabel = new Label();
	
	@I18NKey("name_column")
	private final Label nameColumnLabel = new Label();
	
	
	@I18NKey("value_column")
	private final Label valueColumnLabel = new Label();
	private FormItem textFieldFormItem ; 
	private FormItem comboBoxFormItem ; 
	private final Grid<State<?>> grid = new Grid<>();
	private final Map<Class<? extends State<? extends Object>>, Consumer<StateModel>> stateCommands = new HashMap<>();
	private final Map<Class<? extends State<? extends Object>>, Supplier<Object>> stateValueSuppliers = new HashMap<>();
	
	private final FormLayout formLayout = new FormLayout();
	
	
	private final Map<ValidationErrors, String> dialogMessageKeys = new HashMap<>() ;

	private final Converter<State<?>, String> stateValueConverter;
	
	private final MessageSource messageSource;
	private final StateService stateService; 
	SystemVariablesView(final StateService stateService, final StateModel stateModel, @Qualifier("stateValueConverter") final Converter<State<?>, String> stateValueConverter, final MessageSource messageSource, final SimpleNotificationDialog notificationDialog, final ButtonBox buttonBox ) {
	
		this.stateValueConverter=stateValueConverter;
		this.stateService=stateService;
		this.messageSource=messageSource;
		this.notificationDialog=notificationDialog;
		createUI(stateModel, stateService, buttonBox);	
		grid.asSingleSelect().addValueChangeListener(selectionEvent -> stateModel.assign(selectionEvent.getValue()));
		stateModel.register(StateModel.Events.AssignState, () ->  assignState(stateModel));	
		initStateCommands(stateModel);
		initSuppliers();
		
		saveButton.addClickListener(event ->stateModel.selectedState().ifPresent(state -> updateState(stateModel)));
		resetButton.addClickListener(event -> stateModel.selectedState().ifPresent(state -> stateModel.reset()));
		grid.setItems(stateService.states());
	
		
		stateModel.register(StateModel.Events.ChangeLocale, () -> {
			
			 localize(messageSource, stateModel.locale());
			 loacalizeStateInfoLabel(stateModel);
			 System.out.println("*** SystemVariablesView: ChangeLocale ***");
			
		});
		initDialogMessageKeys();
		
		stateModel.notifyObservers(StateModel.Events.ChangeLocale);
		
		
	}


	private void initDialogMessageKeys() {
		dialogMessageKeys.put(ValidationErrors.NotChanged, I18N_VALUE_NOT_CHANGED);
		dialogMessageKeys.put(ValidationErrors.Mandatory, I18N_VALUE_MANDATORY);
		dialogMessageKeys.put(ValidationErrors.Invalid, I18N_VALUE_INVALID);
		
		
	}

	
	private  void updateState(StateModel model)  {
			
		final Object newValue = Optional.ofNullable(stateValueSuppliers.get(model.selectedState().get().getClass())).orElse(() -> emptyTextAsNull()).get();
		
		
	
		//final SimpleNotificationDialog notification = notificationDialogSupplier.get();
	
		 final ValidationErrors validationErrors = model.validate(newValue);
		 if( validationErrors != ValidationErrors.Ok) {
			 
			 final String key =dialogMessageKeys.get(validationErrors);
			 notificationDialog.showError(message(model, key));
			 return;
		 }
		
		try {		
			stateService.update(model.convert(newValue));
			grid.setItems(stateService.states());	
		} catch (final Exception ex) {
			notificationDialog.showError(messageSource.getMessage(I18N_ERROR, new String[] {ex.getMessage()},"???",  model.locale()));
		} 
		
	}


	private String message(final StateModel stateModel, final String key) {
		return messageSource.getMessage(key, null,"???",  stateModel.locale());
	}

	
	
	

	private void initStateCommands(StateModel stateModel) {
		stateCommands.put(BooleanStateImpl.class, model -> initBooleanValueField(stateModel));
		stateCommands.put(ItemsStateImpl.class, model -> initListValueField(stateModel));
	}
	
	private void initSuppliers() {
	
		stateValueSuppliers.put(BooleanStateImpl.class, () -> valueComboBox.getValue() );
		stateValueSuppliers.put(ItemsStateImpl.class, () -> valueComboBox.getValue());
		//stateValueSuppliers.put(DoubleStateImpl.class, () -> emptyTextAsNull());
		//stateValueSuppliers.put(StringStateImpl.class, () -> emptyTextAsNull());
	}

	private Object emptyTextAsNull() {
		return StringUtils.hasText(valueTextField.getValue()) ? valueTextField.getValue().trim() : null;
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
		loacalizeStateInfoLabel(stateModel);
	}


	private void loacalizeStateInfoLabel(final StateModel stateModel) {
		stateInfoLabel.setText("");
		if( stateModel.selectedState().isPresent()) {
			stateInfoLabel.setText(messageSource.getMessage(I18N_INFO_LABEL_PATTERN, stateModel.stateInfoParameters(),"???",  stateModel.locale()));
		}
	}

	private void initBooleanValueField(StateModel stateModel) {
		comboBoxFormItem.setVisible(true);	
		textFieldFormItem.setVisible(false);
		comboBoxFormItem.getElement().getStyle().set("width", "100%");
		comboBoxFormItem.getElement().getStyle().set("margin", "0");
		valueComboBox.setItems(Arrays.asList(Boolean.FALSE, Boolean.TRUE));
		valueComboBox.setItemLabelGenerator( value  ->  value.toString());
		valueComboBox.setValue(stateModel.selectedState().get().value());
		
		loacalizeStateInfoLabel(stateModel);
	}

	private void initTextValueField(final StateModel stateModel) {
		textFieldFormItem.setVisible(true);
		comboBoxFormItem.setVisible(false);	
		valueTextField.setReadOnly(false);
		
		valueTextField.setValue( stateValueConverter.convert(stateModel.selectedState().get()));
		
		loacalizeStateInfoLabel(stateModel);
		
	}

	private void createUI(final StateModel stateModel,final StateService stateService, final ButtonBox buttonBox) {
				
		
	
		saveButton.setVisible(stateModel.isChangeVariableAllowed());
		saveButton.setEnabled(false);
		resetButton.setEnabled(false);
		
		valueComboBox.setRequired(true);
		valueTextField.setRequired(true);
		//final HorizontalLayout buttonBox = new HorizontalLayout();
		//final Button loginButton = new Button();
		//loginButton.setIcon(VaadinIcons.USERS.create());
		//buttonBox.add(loginButton);
		
		//final Button logoutButton = new Button();
		//logoutButton.setIcon(VaadinIcons.CLOSE.create());
		//buttonBox.add(logoutButton);
	
		//logoutButton.addClickListener( event -> ((Component) event.getSource()).getUI().ifPresent(ui -> invalidateSession(ui)));
		
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
		textFieldFormItem=formLayout.addFormItem(valueTextField, textValueLabel);
		comboBoxFormItem=formLayout.addFormItem(valueComboBox, listValueLabel);
		comboBoxFormItem.setVisible(false);
		

		formLayout.setSizeFull();

		formLayout.setResponsiveSteps(new ResponsiveStep("10vH",1));

		

		final VerticalLayout buttonLayout = new VerticalLayout(resetButton, saveButton);

		
		
		final HorizontalLayout editorLayout = new HorizontalLayout(formLayout, buttonLayout);

		editorLayout.setVerticalComponentAlignment(Alignment.CENTER, buttonLayout);

		editorLayout.setSizeFull();
		
		
	//	grid.addColumn((ValueProvider<State<?>, Long>) state -> state.id()).setVisible(false);
		grid.addColumn(stateNameValueProvider()).setHeader(nameColumnLabel).setResizable(true);
		grid.addColumn(stateValueProvider()).setHeader(valueColumnLabel).setResizable(true);
		grid.setSelectionMode(SelectionMode.SINGLE);
		
		
		
		add(buttonBox, layout, stateInfoLabel, editorLayout);
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
			saveButton.setVisible(stateModel.isChangeVariableAllowed());
			saveButton.setEnabled(true);
			resetButton.setEnabled(true);
			return;
		}
	
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
	
	ValueProvider<State<?>, String>  stateNameValueProvider() {
		return state -> state.name();
		
	}
	
	ValueProvider<State<?>, String>  stateValueProvider() {
		return state -> stateValueConverter.convert(state);
		
	}


	


	


	


	
	
	
	

}

