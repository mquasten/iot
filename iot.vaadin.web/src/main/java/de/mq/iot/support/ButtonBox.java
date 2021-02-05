package de.mq.iot.support;

import java.util.Locale;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcons;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import de.mq.iot.model.Subject;


public class ButtonBox extends HorizontalLayout {
	

	
	private static final String CHANGE_LOCALE_EVENT = "ChangeLocale";

	private static final long serialVersionUID = 1L;
	
	private final Button systemsVariablesButton = new Button();
	
	private final Button deviceButton = new Button();
	private final Button closeButton = new Button();
	
	private final Button specialdaysButton = new Button();
	
	

	private final Button rulesButton = new Button();
	
	private final Button usersButton = new Button();
	
	private final Button languageDeButton = new Button();
	
	private final Button languageEnButton = new Button();
	
	private final Subject<String, Subject<?,?>> subject;
	
	
	public ButtonBox (final Subject<?,?> subject) {
	
		this.subject=subject(subject);
		systemsVariablesButton.setIcon(VaadinIcons.ABACUS.create());
		systemsVariablesButton.addClickListener( event ->  ((Component) event.getSource()).getUI().ifPresent(ui -> {
			ui.navigate("");
		}));
		add(systemsVariablesButton);
		
		specialdaysButton.setIcon(VaadinIcons.CALENDAR.create());
		
		
		
		specialdaysButton.addClickListener( event ->  ((Component) event.getSource()).getUI().ifPresent(ui -> ui.navigate("calendar")) ); 
		
		add(specialdaysButton);
		
		
		
		deviceButton.setIcon(VaadinIcons.AUTOMATION.create());
		
		
		
		deviceButton.addClickListener( event ->  ((Component) event.getSource()).getUI().ifPresent(ui -> ui.navigate("devices")) ); 
		
		add(deviceButton);
		
		rulesButton.setIcon(VaadinIcons.BUILDING.create());
		rulesButton.addClickListener( event ->  ((Component) event.getSource()).getUI().ifPresent(ui -> ui.navigate("rules")) ); 
		
		add(rulesButton);
		
		
		
		usersButton.setIcon(VaadinIcons.USER.create());
		usersButton.addClickListener( event ->  ((Component) event.getSource()).getUI().ifPresent(ui -> ui.navigate("users")) ); 
		
		add(usersButton);
		
		languageDeButton.setIcon(new Image("de.png", "de"));
		add(languageDeButton);
		
		languageEnButton.setIcon(new Image("en.png", "en"));
		add(languageEnButton);
		
		closeButton.setIcon(VaadinIcons.CLOSE.create());
		add(closeButton);
		
		closeButton.addClickListener( event -> ((Component) event.getSource()).getUI().ifPresent(ui -> invalidateSession(ui)));
		
		languageDeButton.addClickListener(event -> subject.assign(Locale.GERMAN));
		
		languageEnButton.addClickListener( event -> subject.assign(Locale.ENGLISH));
		
		
		this.subject.register(CHANGE_LOCALE_EVENT , () -> setLanguageButtonsVisible(subject));
		setLanguageButtonsVisible(subject);
	}


	private void setLanguageButtonsVisible(final Subject<?, ?> subject) {
		languageEnButton.setVisible(subject.locale()!=Locale.ENGLISH);
		languageDeButton.setVisible(subject.locale()!=Locale.GERMAN);
	}
	
	
	
	@SuppressWarnings("unchecked")
	Subject<String, Subject<?,?>> subject(final Subject<?,?> subject){
		return  (Subject<String, Subject<?, ?>>) subject;
		
	}
	private void invalidateSession(final UI ui) {
		ui.getSession().getSession().invalidate();
		ui.getPage().reload();
	}

}
