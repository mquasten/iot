package de.mq.iot.state.support;


import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.util.Assert;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;

import de.mq.iot.state.StateService.DeviceType;

public class DeviceStateValueField  extends FormLayout  {
	
	
	private static final long serialVersionUID = 1L;
	private final TextField textField = new TextField();
	private final FormItem textItem ;
	private final FormItem comboBoxItem ;
	private final ComboBox<Boolean> comboBox = new ComboBox<>();
	private final Label textLabel = new Label();
	private final Label comboBoxLabel = new Label();

	
	
	public DeviceStateValueField() {
		textField.setSizeFull();
		textItem = addFormItem(textField, textLabel);
		comboBoxItem = addFormItem(comboBox, comboBoxLabel);
		comboBox.setSizeFull();
		comboBox.setItems(Arrays.asList(Boolean.TRUE, Boolean.FALSE));
		comboBox.setAllowCustomValue(false);
		comboBox.setRequired(true);
		textItem.setVisible(false);
		comboBoxItem.setVisible(false);
		
	}
	
	public void localize(final String label) {
		this.textLabel.setText(label);
		this.comboBoxLabel.setText(label);
	}
	
	
	
	public final void setEnabled(final boolean enabled) {
		textField.setEnabled(enabled);
		comboBox.setEnabled(enabled);
		comboBoxItem.getElement().getStyle().set("width", "100%");
		comboBoxItem.getElement().getStyle().set("margin", "0");
		if(!enabled) {
			textField.clear();
			comboBox.clear();
		}
	}
	
	public final void setErrorMessage(Optional<String> message) {
		textField.setInvalid(message.isPresent());
		message.ifPresent(errorMessage -> textField.setErrorMessage(errorMessage));
		comboBox.setInvalid(message.isPresent());
		message.ifPresent(errorMessage -> comboBox.setErrorMessage(errorMessage));
	}
	
	public final void setValue( Object  value){
		if( textItem.isVisible()) {
			textField.setValue(value.toString());
		
		}
		if( comboBoxItem.isVisible() ) {
			comboBox.setValue((Boolean) value);
		}
	}

	public final void addConsumer(final Consumer<Object> consumer) {
		Assert.notNull(consumer , "Consumer is mandatory.");
		textField.addValueChangeListener(event -> consumer.accept(event.getValue()));
		comboBox.addValueChangeListener(event -> consumer.accept(event.getValue()));
	}
	
	public final void setDeviceType(final DeviceType deviceType) {
		Assert.notNull(deviceType, "DeviceType is required.");
		textItem.setVisible(deviceType==DeviceType.Level);
		comboBoxItem.setVisible(deviceType==DeviceType.State);
	}
	

}
