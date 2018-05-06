package de.mq.iot.state.support;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcons;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;

class SimpleNotificationDialog {
	
	private final TextArea  textArea =new TextArea();
	private final Button closeButton = new Button();
	private final Icon warningsIcon =  VaadinIcons.WARNING.create();
	private final Icon infoIcon =  VaadinIcons.INFO.create();

	private final VerticalLayout root = new VerticalLayout(warningsIcon, infoIcon, textArea,closeButton);
	
	private final Dialog notification;
	
	
	SimpleNotificationDialog(final Dialog notification) {
		
		this.notification=notification;
		this.notification.add(root);
		
		textArea.setReadOnly(true);
		textArea.setInvalid(true);
	
		closeButton.setText("ok");
		root.setHorizontalComponentAlignment(Alignment.CENTER, closeButton);
		root.setSizeUndefined();
		textArea.setSizeFull();
		//layout.setSizeFull();
		
		notification.setCloseOnEsc(true);
		notification.setCloseOnOutsideClick(true);
		root.setWidth("40vh");
	
		warningsIcon.setVisible(false);
		infoIcon.setVisible(false);
		
		closeButton.addClickListener(event -> notification.close());

	}
	public  void showError(final String text) {
		infoIcon.setVisible(false);
		warningsIcon.setVisible(true);
		textArea.setValue(text);
		notification.open();
	}
	
}
