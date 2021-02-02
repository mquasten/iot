package de.mq.iot.support;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcons;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import de.mq.iot.model.Subject;

public class ButtonBox extends HorizontalLayout {

	
	private static final long serialVersionUID = 1L;
	
	private final Button systemsVariablesButton = new Button();
	
	private final Button deviceButton = new Button();
	private final Button closeButton = new Button();
	
	private final Button specialdaysButton = new Button();
	
	

	private final Button rulesButton = new Button();
	
	private final Button usersButton = new Button();
	
	private final Button languageDeButton = new Button();
	
	private final Button languageEnButton = new Button();
	
	public ButtonBox (final Subject<?, ?> subject) {
	
		
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
		
		languageDeButton.addClickListener(event -> {
			System.out.println("***DE***");
			
		});
		
		languageEnButton.addClickListener( event -> {
			System.out.println("***En***");
		});
		
	}
	
	private void invalidateSession(final UI ui) {
		ui.getSession().getSession().invalidate();
		ui.getPage().reload();
	}

}
