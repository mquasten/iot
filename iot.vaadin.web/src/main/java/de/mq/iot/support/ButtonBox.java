package de.mq.iot.support;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcons;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class ButtonBox extends HorizontalLayout {

	
	private static final long serialVersionUID = 1L;
	
	private final Button systemsVariablesButton = new Button();
	
	private final Button deviceButton = new Button();
	private final Button closeButton = new Button();
	
	private final Button specialdaysButton = new Button();
	
	

	private final Button rulesButton = new Button();
	
	private final Button usersButton = new Button();
	
	public ButtonBox () {
	
		
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
		
		closeButton.setIcon(VaadinIcons.CLOSE.create());
		add(closeButton);
		
		closeButton.addClickListener( event -> ((Component) event.getSource()).getUI().ifPresent(ui -> invalidateSession(ui)));
	}
	
	private void invalidateSession(final UI ui) {
		ui.getSession().getSession().invalidate();
		ui.getPage().reload();
	}

}
