package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.textfield.TextArea;

class SimpleNotificationDialogTest {
	
	
	private static final String MESSAGE = "Es ist ein Fehler aufgetreten.";

	private final Dialog dialog = Mockito.mock(Dialog.class);
	
	private final  SimpleNotificationDialog notificationDialog = new SimpleNotificationDialog(dialog);
	
	private Map<String, Object> components = new HashMap<>();
	
	@BeforeEach
	final void setup() {
		Arrays.asList(notificationDialog.getClass().getDeclaredFields()).stream().filter(field -> ! Modifier.isStatic(field.getModifiers())).forEach(field -> components.put(field.getName(), ReflectionTestUtils.getField(notificationDialog, field.getName())));
	    assertEquals(6, components.size());
	}
	
	
	@Test
	final void init() {
	
		final TextArea textArea = (TextArea) components.get("textArea");
		assertTrue(textArea.isReadOnly());
		assertTrue(textArea.isInvalid());
		final Icon warningsIcon = (Icon) components.get("warningsIcon");
		assertFalse(warningsIcon.isVisible());
		final Icon infoIcon = (Icon) components.get("infoIcon");
		assertFalse(infoIcon.isVisible());
	}
	
	
	@Test
	final void calse() {
		final Button button =  (Button) components.get("closeButton");
		 listener(button).onComponentEvent(null);
		 
		 Mockito.verify(dialog).close();
		
	}
	
	@SuppressWarnings("unchecked")
	private ComponentEventListener<?> listener(final Button saveButton) {
		final ComponentEventBus eventBus = (ComponentEventBus) ReflectionTestUtils.getField(saveButton, "eventBus");
		final Map<Class<?>, ?> map = (Map<Class<?>, ?>) ReflectionTestUtils.getField(eventBus, "componentEventData");
		return DataAccessUtils.requiredSingleResult((Collection<ComponentEventListener<?>>) ReflectionTestUtils.getField(map.values().iterator().next(), "listeners"));
	}
	
	@Test
	final void showError() {
		notificationDialog.showError(MESSAGE);
		
		final Icon warningsIcon = (Icon) components.get("warningsIcon");
		assertTrue(warningsIcon.isVisible());
		final Icon infoIcon = (Icon) components.get("infoIcon");
		assertFalse(infoIcon.isVisible());
		
		final TextArea textArea = (TextArea) components.get("textArea");
		assertTrue(textArea.isReadOnly());
		assertTrue(textArea.isInvalid());
		
		assertEquals(MESSAGE, textArea.getValue());
		
		Mockito.verify(dialog).open();
	}

}
