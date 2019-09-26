package de.mq.iot.authentication.support;


import java.util.Collections;

import org.springframework.context.MessageSource;

import com.vaadin.flow.component.button.Button;
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
	
	private final TextField passwordTextField = new TextField();

	
	@I18NKey("save")
	private  final Button saveButton = new Button();
	
	private  final Label passworfInfoLabel = new Label("Passwort ändern");
	
	
	
	
	
	
	@I18NKey("value_column")
	private final Label passwordLabel = new Label();

	
	private final Grid<Authentication> userGrid = new Grid<>();
	
	private final Grid<Authority> authorityGrid = new Grid<>();
	
	
	private final FormLayout formLayout = new FormLayout();
	
	

	final VerticalLayout buttonLayout = new VerticalLayout(saveButton);

	
	
	final HorizontalLayout editorLayout = new HorizontalLayout(formLayout, buttonLayout);
	
	
	

	UsersView(final AuthentificationService authentificationService, final UserModel userModel, final MessageSource messageSource,final ButtonBox buttonBox ) {
	
		
		
		createUI( buttonBox);	
		
	
		
		userModel.register(UserModel.Events.SeclectionChanged, () -> selectionChangedObserver(userModel));
		
		
		userGrid.setItems(authentificationService.authentifications());
		
		
		userGrid.asSingleSelect().addValueChangeListener(selectionEvent -> {
			userModel.assign(selectionEvent.getValue());
		});
	}






	private void selectionChangedObserver(final UserModel userModel) {
		authorityGrid.setItems(Collections.emptyList());
		
		passworfInfoLabel.setVisible(false);
		editorLayout.getParent().ifPresent(parent -> remove(editorLayout));
		userModel.authentication().ifPresent(authentication -> {
			authorityGrid.setItems(authentication.authorities());
			nameTextField.setValue(authentication.username());
			passworfInfoLabel.setVisible(true);
			add(editorLayout);
		});
	}


	

	
	
	private void createUI(final ButtonBox buttonBox) {
				
		
		saveButton.setText("ändern");
		
		passworfInfoLabel.setVisible(false);
		
		passwordTextField.setRequired(true);
		
		
		final HorizontalLayout layout = new HorizontalLayout(userGrid,authorityGrid);
		userGrid.getElement().getStyle().set("overflow", "auto");
		
		authorityGrid.getElement().getStyle().set("overflow", "auto");
	
		nameTextField.setSizeFull();
		nameTextField.setReadOnly(true);
	
	
		
		passwordTextField.setSizeFull();
		
		//passwordTextField.setReadOnly(true);
		nameLabel.setText("Login");;
		passwordLabel.setText("Passwort");
		
		
		formLayout.addFormItem(nameTextField, nameLabel);
		formLayout.addFormItem(passwordTextField, passwordLabel);
		

		formLayout.setSizeFull();

		formLayout.setResponsiveSteps(new ResponsiveStep("10vH",1));

		

		

		editorLayout.setVerticalComponentAlignment(Alignment.CENTER, buttonLayout);

		editorLayout.setSizeFull();
		
		
	
		userGrid.setSelectionMode(SelectionMode.SINGLE);
		
		
		
		add(buttonBox, layout, passworfInfoLabel);
		setHorizontalComponentAlignment(Alignment.CENTER, passworfInfoLabel);
	
		
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

