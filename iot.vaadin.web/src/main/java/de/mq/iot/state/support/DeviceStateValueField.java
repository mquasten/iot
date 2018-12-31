package de.mq.iot.state.support;


import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.util.Assert;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;

public class DeviceStateValueField  extends FormLayout  {
	
	
	private static final long serialVersionUID = 1L;
	private final TextField textField = new TextField();
	private final Label label = new Label();

	
	
	public DeviceStateValueField() {
		textField.setSizeFull();
		addFormItem(textField, label);
	}
	
	public void localize(final String label) {
		this.label.setText(label);
	}
	
	
	
	public final void setEnabled(final boolean enabled) {
		textField.setEnabled(enabled);
		if(!enabled) {
			textField.clear();
		}
	}
	
	public final void setErrorMessage(Optional<String> message) {
		textField.setInvalid(message.isPresent());
		message.ifPresent(errorMessage -> textField.setErrorMessage(errorMessage));
	}
	
	public final void setValue( Object  value){
		textField.setValue(value.toString());
	}

	public final void addConsumer(final Consumer<Object> consumer) {
		Assert.notNull(consumer , "Consumer is mandatory.");
		textField.addValueChangeListener(event -> consumer.accept(event.getValue()));
	}
	

}
