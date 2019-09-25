package de.mq.iot.authentication.support;


import org.springframework.context.MessageSource;

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

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.AuthentificationService;
import de.mq.iot.authentication.Authority;
import de.mq.iot.model.I18NKey;
import de.mq.iot.model.LocalizeView;
import de.mq.iot.support.ButtonBox;

@Route("users")
@Theme(Lumo.class)
@I18NKey("users_")
class  UsersView extends VerticalLayout implements LocalizeView {
	
	

	
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

	private FormItem comboBoxFormItem ; 
	private final Grid<Authentication> userGrid = new Grid<>();
	
	private final Grid<Authority> authorityGrid = new Grid<>();
	
	
	private final FormLayout formLayout = new FormLayout();
	
	


	
	
	private final MessageSource messageSource;

	UsersView(final AuthentificationService authentificationService, final UserModel userModel, final MessageSource messageSource,final ButtonBox buttonBox ) {
	
		
		
		this.messageSource=messageSource;
		
		createUI( buttonBox);	
		
	
		
		userModel.register(UserModel.Events.SeclectionChanged, () -> {
			
			authorityGrid.setItems(userModel.authorities());
			
		});
		
		
		userGrid.setItems(authentificationService.authentifications());
		
		
		userGrid.asSingleSelect().addValueChangeListener(selectionEvent -> {
			userModel.assign(selectionEvent.getValue());
		});
	}


	

	
	
	private void createUI(final ButtonBox buttonBox) {
				
	
		saveButton.setEnabled(false);
		resetButton.setEnabled(false);
		
		valueComboBox.setRequired(true);
		valueTextField.setRequired(true);
		
		
		final HorizontalLayout layout = new HorizontalLayout(userGrid,authorityGrid);
		userGrid.getElement().getStyle().set("overflow", "auto");
		
		authorityGrid.getElement().getStyle().set("overflow", "auto");
	
		nameTextField.setSizeFull();
		nameTextField.setReadOnly(true);
	
		lastUpdateTextField.setSizeFull();
		lastUpdateTextField.setReadOnly(true);
		
		valueTextField.setSizeFull();
		valueComboBox.setSizeFull();
		valueTextField.setReadOnly(true);
		formLayout.addFormItem(nameTextField, nameLabel);
		formLayout.addFormItem(lastUpdateTextField, dateLabel);
		
		comboBoxFormItem=formLayout.addFormItem(valueComboBox, listValueLabel);
		comboBoxFormItem.setVisible(false);
		

		formLayout.setSizeFull();

		formLayout.setResponsiveSteps(new ResponsiveStep("10vH",1));

		

		final VerticalLayout buttonLayout = new VerticalLayout(resetButton, saveButton);

		
		
		final HorizontalLayout editorLayout = new HorizontalLayout(formLayout, buttonLayout);

		editorLayout.setVerticalComponentAlignment(Alignment.CENTER, buttonLayout);

		editorLayout.setSizeFull();
		
		
	
		userGrid.setSelectionMode(SelectionMode.SINGLE);
		
		
		
		add(buttonBox, layout, stateInfoLabel, editorLayout);
		setHorizontalComponentAlignment(Alignment.CENTER, stateInfoLabel);
	
		
		layout.setSizeFull();
	
		setHorizontalComponentAlignment(Alignment.CENTER, layout);
	
		
	
	   userGrid.setHeight("50vH");
	   authorityGrid.setHeight("50vH");
	   userGrid.setSelectionMode(SelectionMode.SINGLE);
	   
	   userGrid.addColumn((ValueProvider<Authentication,String>) authentication -> {
		   return authentication.username();
	   }).setHeader("Benutzer");
		
	   
	   authorityGrid.addColumn((ValueProvider<Authority,String>) authority -> {
		   
		   return authority.name();
	   }).setHeader("Rollen");
		
	}


	

	


	


	


	
	
	
	

}

