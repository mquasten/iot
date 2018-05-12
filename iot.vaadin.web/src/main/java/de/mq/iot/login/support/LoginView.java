package de.mq.iot.login.support;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import de.mq.iot.model.I18NKey;
import de.mq.iot.model.SecurityContext;
import de.mq.iot.model.support.UserAuthentificationImpl;


@Route("login")
@Theme(Lumo.class)
@I18NKey("login_")
public class LoginView extends VerticalLayout  {
	
	private static final long serialVersionUID = 1L;
	@Autowired
	LoginView(final SecurityContext securityContext) {
	final FormLayout formLayout = new FormLayout();
	formLayout.setResponsiveSteps(new ResponsiveStep("10vH",1));
	final VerticalLayout layout = new VerticalLayout(formLayout);
	
	
	final TextField user = new TextField();

	
	
	user.setSizeFull();
	final TextField passwd = new TextField();
	
	passwd.setSizeFull();
	
	formLayout.addFormItem( user, new Label("Benutzer"));
	formLayout.addFormItem(passwd,new Label("Password"));
	
	
	final Button button = new Button("login");
	
	button.addClickListener(e -> {
		 getUI().ifPresent(ui -> {
			
			securityContext.assign(new UserAuthentificationImpl("kminogue",  Arrays.asList("user")));
			
		
			 ui.navigate("");
			 
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

}
