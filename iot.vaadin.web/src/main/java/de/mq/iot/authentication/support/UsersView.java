package de.mq.iot.authentication.support;


import java.util.Collections;

import org.springframework.context.MessageSource;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcons;
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
import de.mq.iot.authentication.support.UserModel.Events;
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

	
	private final Button addUserButton = new Button();
	
	private final Button deleteUserButton = new Button();
	
	@I18NKey("save")
	private  final Button saveButton = new Button();
	
	
	@I18NKey("info_change")
	private final Label changePasswordInfoLabel = new Label();
	
	
	private  final Label infoLabel = new Label();
	
	@I18NKey("roles_column")
	private final Label rolesColumnLabel = new Label();
	
	
	@I18NKey("user_column")
	private final Label userColumnLabel = new Label();
	
	
	@I18NKey("password")
	private final Label passwordLabel = new Label();

	
	private final Grid<Authentication> userGrid = new Grid<>();
	
	private final Grid<Authority> authorityGrid = new Grid<>();
	
	
	private final FormLayout formLayout = new FormLayout();
	
	

	final VerticalLayout buttonLayout = new VerticalLayout(saveButton);

	
	
	final HorizontalLayout editorLayout = new HorizontalLayout(formLayout, buttonLayout);
	
	
	

	UsersView(final AuthentificationService authentificationService, final UserModel userModel, final MessageSource messageSource,final ButtonBox buttonBox ) {
	
		

		createUI( buttonBox);	
		
	
		
		
		
		
		userGrid.setItems(authentificationService.authentifications());
		
		
		userGrid.asSingleSelect().addValueChangeListener(selectionEvent -> {
			userModel.assign(selectionEvent.getValue());
		});
		
		
		userModel.register(UserModel.Events.ChangeLocale, () -> {
			
			 localize(messageSource, userModel.locale());
			
		});
		
		userModel.register(UserModel.Events.SeclectionChanged, () -> selectionChangedObserver(userModel));
		
		
		userModel.notifyObservers(Events.ChangeLocale);
		
		
	}






	private void selectionChangedObserver(final UserModel userModel) {
		authorityGrid.setItems(Collections.emptyList());
		
		infoLabel.setVisible(false);
		editorLayout.getParent().ifPresent(parent -> remove(editorLayout));
		userModel.authentication().ifPresent(authentication -> {
			infoLabel.setText(changePasswordInfoLabel.getText());
			authorityGrid.setItems(authentication.authorities());
			nameTextField.setValue(authentication.username());
			infoLabel.setVisible(true);
			
			add(editorLayout);
		});
	}


	

	
	
	private void createUI(final ButtonBox buttonBox) {
				
		
		
		
		infoLabel.setVisible(false);
		
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
		
		
		
		add(buttonBox, layout, infoLabel);
		setHorizontalComponentAlignment(Alignment.CENTER, infoLabel);
	
		
		layout.setSizeFull();
	
		setHorizontalComponentAlignment(Alignment.CENTER, layout);
	
		
	
	   userGrid.setHeight("50vH");
	   authorityGrid.setHeight("50vH");
	   userGrid.setSelectionMode(SelectionMode.SINGLE);
	   
	   
	  
		
		final HorizontalLayout userGridFooter = new HorizontalLayout(addUserButton, deleteUserButton);
	
		
		addUserButton.setIcon(VaadinIcons.FILE_ADD.create());
		deleteUserButton.setIcon(VaadinIcons.FILE_REMOVE.create());
	   
	   userGrid.addColumn((ValueProvider<Authentication,String>) authentication -> {
		   return authentication.username();
		   
		   
		   
	   }).setHeader(userColumnLabel).setFooter(userGridFooter);
		
	
	   authorityGrid.addColumn((ValueProvider<Authority,String>) authority -> {
		   
		   return authority.name();
	   }).setHeader(rolesColumnLabel);
		
	}


	

	


	


	


	
	
	
	

}

