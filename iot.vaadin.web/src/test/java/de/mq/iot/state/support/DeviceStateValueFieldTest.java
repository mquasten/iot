package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StringUtils;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;

import de.mq.iot.state.StateService.DeviceType;

public class DeviceStateValueFieldTest {
	
	
	private static final String DOUBLE_VALUE = "50";
	private static final String VALUE_LABEL = "Value";
	private final DeviceStateValueField deviceStateValueField = new DeviceStateValueField();
	
	
	private final Map<String, Object> fields = new HashMap<>();
	
	@BeforeEach
	final void setup() {
		deviceStateValueField.localize(VALUE_LABEL);
		Arrays.asList(DeviceStateValueField.class.getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).forEach(field -> fields.put(field.getName(), ReflectionTestUtils.getField(deviceStateValueField, field.getName())));
	}
	
	@Test
	void defaultValues() {
		assertEquals(6, fields.size());
		final Label label = (Label) fields.get("textLabel");
		assertNotNull(label);
		assertEquals(VALUE_LABEL, label.getText());
		
		final Label comboboxLabel = (Label) fields.get("comboBoxLabel");
		assertNotNull(comboboxLabel);
		assertEquals(VALUE_LABEL, comboboxLabel.getText());
		
		final TextField textField =  getTextField();
		assertNotNull(textField);
		
		
		final ComboBox<?> comboBox =   getComboBox();
		assertNotNull(comboBox);
		assertTrue(comboBox.isRequired());
		assertFalse(comboBox.isAllowCustomValue());
		
		final FormItem  textItem =   getTextItem();
		
		assertNotNull(textItem);
		assertFalse(textItem.isVisible());
		
		final FormItem  comboBoxItem =   getComboBoxItem();
		assertNotNull(comboBoxItem);
		assertFalse(comboBoxItem.isVisible());
	}

	protected FormItem getComboBoxItem() {
		return (FormItem) fields.get("comboBoxItem");
	}

	protected FormItem getTextItem() {
		return (FormItem) fields.get("textItem");
	}

	@SuppressWarnings("unchecked")
	protected ComboBox<Boolean> getComboBox() {
		return (ComboBox<Boolean>) fields.get("comboBox");
	}

	protected TextField getTextField() {
		return (TextField) fields.get("textField");
	}
	
	@Test
	void setEnabled() {
		final TextField textField = getTextField();
		final ComboBox<Boolean> comboBox = getComboBox();
		
		deviceStateValueField.setEnabled(true);
		assertTrue(textField.isEnabled());
		assertTrue(comboBox.isEnabled());
		
		textField.setValue("value");
		comboBox.setValue(Boolean.TRUE);
		
		deviceStateValueField.setEnabled(false);
		
		assertFalse(textField.isEnabled());
		assertFalse(comboBox.isEnabled());
		
		assertNull(comboBox.getValue());
		assertTrue(StringUtils.isEmpty(textField.getValue()));
		
	}
	
	@Test
	void  setDeviceType() {
		final FormItem  textItem =   getTextItem();
		final FormItem comboBoxItem = getComboBoxItem();
		assertNotNull(textItem);
		assertNotNull(comboBoxItem);
		
		deviceStateValueField.setDeviceType(DeviceType.Level);
		assertTrue(textItem.isVisible());
		assertFalse(comboBoxItem.isVisible());
		
		deviceStateValueField.setDeviceType(DeviceType.State);
		
		assertFalse(textItem.isVisible());
		assertTrue(comboBoxItem.isVisible());
	}
	
	@Test
	void  setValue() {
		final TextField textField = getTextField();
		final ComboBox<Boolean> comboBox = getComboBox();
		
		deviceStateValueField.setDeviceType(DeviceType.Level);
		deviceStateValueField.setValue(DOUBLE_VALUE);
		
		assertEquals(DOUBLE_VALUE, textField.getValue());
	
		deviceStateValueField.setDeviceType(DeviceType.State);
		deviceStateValueField.setValue(Boolean.TRUE);
			
		assertEquals(Boolean.TRUE, comboBox.getValue());
		
	}

}
