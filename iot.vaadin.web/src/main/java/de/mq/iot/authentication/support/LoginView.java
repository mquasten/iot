package de.mq.iot.authentication.support;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.StringUtils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.AuthentificationService;
import de.mq.iot.authentication.SecurityContext;
import de.mq.iot.model.I18NKey;



@Route("login")
@Theme(Lumo.class)
@I18NKey("login_")
public class LoginView extends VerticalLayout  {
	
	
	
	private static final long serialVersionUID = 1L;
	
	private final Label message = new Label("error");
	final SecurityContext securityContext;
	final AuthentificationService authentificationService;
	final LoginModel loginModel;
	final UI ui;
	
	@Autowired
	LoginView(final SecurityContext securityContext, final AuthentificationService authentificationService, final LoginModel loginModel, final UI ui) {
	this.securityContext=securityContext;
	this.authentificationService=authentificationService;
	this.loginModel=loginModel;
	this.ui=ui;
		
	final FormLayout formLayout = new FormLayout();
	formLayout.setResponsiveSteps(new ResponsiveStep("10vH",1));
	
	
	final VerticalLayout layout = new VerticalLayout(formLayout);
	
	
	
	final TextField user = new TextField();
	
	user.setSizeFull();
	
	

	
	final PasswordField passwd = new PasswordField();
	
	passwd.setSizeFull();
	
	formLayout.addFormItem( user, new Label("Benutzer"));
	formLayout.addFormItem(passwd,new Label("Password"));
	
	
	final Binder<LoginModel> binder = new Binder<>();

	
	binder.forField(user).withValidator( value ->  StringUtils.hasText(value), "required").bind(LoginModel::login, LoginModel::assignLogin);
	
	binder.forField(passwd).withValidator( value ->  StringUtils.hasText(value),"required").bind(LoginModel::password, LoginModel::assignPassword);  
	
	
	final Button button = new Button("login");
	
	button.addClickListener(e -> {
			message.setVisible(false);
			if( binder.writeBeanIfValid(loginModel) ) {
				login();
			} 
			
		
			 
			 
	});
		 
		 
		
		
		
	
	
	//message.setVisible(false);
	message.getStyle().set("color", "red");
	message.setVisible(false);
	layout.add(message);
	layout.add(button);
	
	layout.setHorizontalComponentAlignment(Alignment.CENTER, message);
	layout.setHorizontalComponentAlignment(Alignment.CENTER, button);
	
	
	add(layout);
	

	setHorizontalComponentAlignment(Alignment.CENTER, layout);
	
	}
	private void login() {
		
		final Optional<Authentication> authentication =  authentificationService.authentification(loginModel.login());
		
		if( ! authentication.isPresent()) {
			message.setVisible(true);
			message.setText("Benutzer nicht vorhanden.");
			return;
		}
		
		if( ! loginModel.authenticate(authentication.get()) ) {
			message.setVisible(true);
			message.setText("Passwort ungültig.");
			return;
		}
		
		
		securityContext.assign(authentication.get());
		ui.navigate("");
	}
	

}
