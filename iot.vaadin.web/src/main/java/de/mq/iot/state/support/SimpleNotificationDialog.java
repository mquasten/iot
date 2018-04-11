package de.mq.iot.state.support;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcons;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.TextArea;

public class SimpleNotificationDialog {
	
	private final TextArea  textArea =new TextArea();
	private final Button close = new Button();
	private final Icon warningsIcon =  VaadinIcons.WARNING.create();
	private final Icon infoIcon =  VaadinIcons.INFO.create();
	private final HorizontalLayout layout = new HorizontalLayout(warningsIcon, infoIcon, textArea);
	private final VerticalLayout root = new VerticalLayout(layout,close);
	
	private final Dialog notification = new Dialog(root);
	
	SimpleNotificationDialog() {
		textArea.setReadOnly(true);
		textArea.setInvalid(true);
	
		close.setText("ok");
		root.setHorizontalComponentAlignment(Alignment.CENTER, close);
		textArea.setSizeFull();
		notification.setCloseOnEsc(true);
		notification.setCloseOnOutsideClick(true);
		close.addClickListener(event -> notification.close());

	}
	public final void showError(final String text) {
		infoIcon.setVisible(false);
		warningsIcon.setVisible(true);
		textArea.setValue(text);
		notification.open();
	}
	
}
