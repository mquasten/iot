package de.mq.iot.authentication.support;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import de.mq.iot.model.LocalizeView;




@Route("login")
@Theme(Lumo.class)
@I18NKey("login_")
public class LoginView extends VerticalLayout  implements LocalizeView {
	
	
	
	private static final String I18N_PASSWORD_INVALID = "login_password_invalid";

	private static final String I18N_USER_NOT_FOUND = "login_user_not_found";

	static final String I18N_REQUIRED = "login_required";

	private static final long serialVersionUID = 1L;
	
	private final Label message = new Label("error");
	private final SecurityContext securityContext;
	private final AuthentificationService authentificationService;
	private final LoginModel loginModel;
	@I18NKey("user")
	private final Label userLabel = new Label();
	
	@I18NKey("password")
	private final Label passwordLabel = new Label();
	
	@I18NKey("login")
	private final Button button = new Button();
	private final UI ui;
	
	private MessageSource messageSource;
	
	@Autowired
	LoginView(final SecurityContext securityContext, final AuthentificationService authentificationService, final LoginModel loginModel, final UI ui, final MessageSource messageSource) {
	this.securityContext=securityContext;
	this.authentificationService=authentificationService;
	this.loginModel=loginModel;
	this.ui=ui;
	this.messageSource=messageSource;
		
	final FormLayout formLayout = new FormLayout();
	formLayout.setResponsiveSteps(new ResponsiveStep("10vH",1));
	
	
	final VerticalLayout layout = new VerticalLayout(formLayout);
	
	
	
	final TextField user = new TextField();
	
	user.setSizeFull();
	
	

	
	final PasswordField passwd = new PasswordField();
	
	passwd.setSizeFull();
	
	formLayout.addFormItem( user, userLabel);
	formLayout.addFormItem(passwd,passwordLabel);
	
	
	final Binder<LoginModel> binder = new Binder<>();

	
	binder.forField(user).withValidator( value ->  StringUtils.hasText(value), message(I18N_REQUIRED)).bind(LoginModel::login, LoginModel::assignLogin);
	
	binder.forField(passwd).withValidator( value ->  StringUtils.hasText(value),message(I18N_REQUIRED)).bind(LoginModel::password, LoginModel::assignPassword);  
	
	
	
	
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
	
	loginModel.register(LoginModel.Events.ChangeLocale, () -> localize(messageSource, loginModel.locale()));
	
	loginModel.notifyObservers(LoginModel.Events.ChangeLocale);
	
	}
	private String message(final String key) {
		return messageSource.getMessage(key, null, loginModel.locale());
	}
	private void login() {
		
		final Optional<Authentication> authentication =  authentificationService.authentification(loginModel.login());
		
		if( ! authentication.isPresent()) {
			message.setVisible(true);
			message.setText(message(I18N_USER_NOT_FOUND));
			return;
		}
		
		if( ! loginModel.authenticate(authentication.get()) ) {
			message.setVisible(true);
			message.setText(message(I18N_PASSWORD_INVALID));
			return;
		}
		
		
		securityContext.assign(authentication.get());
		ui.navigate("");
	}
	

}
