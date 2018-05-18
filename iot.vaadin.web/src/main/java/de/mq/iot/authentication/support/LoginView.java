package de.mq.iot.authentication.support;

import java.util.Arrays;
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
import com.vaadin.flow.data.binder.Binder.BindingBuilder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.AuthentificationService;
import de.mq.iot.authentication.SecurityContext;
import de.mq.iot.authentication.support.UserAuthenticationImpl;
import de.mq.iot.model.I18NKey;



@Route("login")
@Theme(Lumo.class)
@I18NKey("login_")
public class LoginView extends VerticalLayout  {
	
	
	
	private static final long serialVersionUID = 1L;
	@Autowired
	LoginView(final SecurityContext securityContext, final AuthentificationService authentificationService) {
	final FormLayout formLayout = new FormLayout();
	formLayout.setResponsiveSteps(new ResponsiveStep("10vH",1));
	final VerticalLayout layout = new VerticalLayout(formLayout);
	
	
	final TextField user = new TextField();
	
	user.setSizeFull();
	
	final LoginModel loginModel = new LoginModelImpl();

	
	final PasswordField passwd = new PasswordField();
	
	passwd.setSizeFull();
	
	formLayout.addFormItem( user, new Label("Benutzer"));
	formLayout.addFormItem(passwd,new Label("Password"));
	
	
	final Binder<LoginModel> binder = new Binder<>();

	
	binder.forField(user).withValidator( value ->  StringUtils.hasText(value), "required").bind(LoginModel::getLogin, LoginModel::setLogin);
	
	binder.forField(passwd).withValidator( value ->  StringUtils.hasText(value),"required").bind(LoginModel::getPassword, LoginModel::setPassword);  
	
	
	final Button button = new Button("login");
	
	button.addClickListener(e -> {
		 getUI().ifPresent(ui -> {
			
			if( binder.writeBeanIfValid(loginModel) ) {
				login(securityContext, authentificationService, loginModel, ui);
			} 
			
		
			 
			 
	});
		 
		 
		
		
		
	});
	Label message = new Label("error");
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
	private void login(final SecurityContext securityContext, final AuthentificationService authentificationService, final LoginModel loginModel, final UI ui) {
		final Optional<Authentication> authentication =  authentificationService.authentification(loginModel.getLogin());
		securityContext.assign(new UserAuthenticationImpl(loginModel.getLogin(),loginModel.getPassword(),   Arrays.asList()));
		ui.navigate("");
	}
	

}
