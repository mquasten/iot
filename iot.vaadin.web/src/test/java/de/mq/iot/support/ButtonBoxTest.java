package de.mq.iot.support;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedHttpSession;



class ButtonBoxTest {
	
	private final ButtonBox buttonBox = new ButtonBox();
	
	private final Map<String, Button> fields = new HashMap<>();
	
	@BeforeEach
	void setup() {
		Arrays.asList(ButtonBox.class.getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).forEach(field -> fields.put(field.getName(), (Button) ReflectionTestUtils.getField(buttonBox, field.getName())));

	}
	
	@SuppressWarnings("unchecked")
	@Test
	void navigateToCalendar() {
		
		final Button specialdaysButton =  fields.get("specialdaysButton");
		assertNotNull(specialdaysButton);
	
		
		
		final Button.ClickEvent<?> event = clickEvent();
		
		@SuppressWarnings("rawtypes")
		final ComponentEventListener listener = listener(specialdaysButton);
		listener.onComponentEvent(event);
		
		Mockito.verify(event.getSource().getUI().get()).navigate("calendar");
		
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Test
	void navigateToDevices() {
		
		final Button specialdaysButton =  fields.get("deviceButton");
		assertNotNull(specialdaysButton);
		
		final Button.ClickEvent<?> event = clickEvent();
		
		@SuppressWarnings("rawtypes")
		final ComponentEventListener listener = listener(specialdaysButton);
		listener.onComponentEvent(event);
		
		Mockito.verify(event.getSource().getUI().get()).navigate("devices");
		
	}

	@SuppressWarnings("rawtypes")
	private Button.ClickEvent clickEvent() {
		final Button.ClickEvent event = Mockito.mock(Button.ClickEvent.class);
		
		final Button component = Mockito.mock(Button.class);
		final UI ui = Mockito.mock(UI.class);
		Mockito.when(component.getUI()).thenReturn(Optional.of(ui));
		Mockito.when(event.getSource()).thenReturn(component);
		final VaadinSession vaadinSession = Mockito.mock(VaadinSession.class);
		WrappedHttpSession seesion = Mockito.mock(WrappedHttpSession.class);
		Mockito.when(vaadinSession.getSession()).thenReturn(seesion);
		Mockito.when(ui.getSession()).thenReturn(vaadinSession);
		Page page = Mockito.mock(Page.class);
		Mockito.when(ui.getPage()).thenReturn(page);
		return event;
	}
	
	
	
	@SuppressWarnings("unchecked")
	private ComponentEventListener<?> listener(final Component saveButton) {
		final ComponentEventBus eventBus = (ComponentEventBus) ReflectionTestUtils.getField(saveButton, "eventBus");
		final Map<Class<?>, ?> map = (Map<Class<?>, ?>) ReflectionTestUtils.getField(eventBus, "componentEventData");
		return DataAccessUtils.requiredSingleResult((Collection<ComponentEventListener<?>>) ReflectionTestUtils.getField(map.values().iterator().next(), "listeners"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void systemsVariablesButton() {
		final Button systemsVariablesButton =  fields.get("systemsVariablesButton");
		assertNotNull(systemsVariablesButton);
	
		
		
		final Button.ClickEvent<?> event = clickEvent();
		
		@SuppressWarnings("rawtypes")
		final ComponentEventListener listener = listener(systemsVariablesButton);
		listener.onComponentEvent(event);
		
		Mockito.verify(event.getSource().getUI().get()).navigate("");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void closeButton() {
		final Button closeButton =  fields.get("closeButton");
		assertNotNull(closeButton);
	
		
		
		final Button.ClickEvent<?> event = clickEvent();
		
		@SuppressWarnings("rawtypes")
		final ComponentEventListener listener = listener(closeButton);
		listener.onComponentEvent(event);
		
		Mockito.verify(event.getSource().getUI().get().getSession().getSession()).invalidate();
		Mockito.verify(event.getSource().getUI().get().getPage()).reload();
		
	}
	
	
	@SuppressWarnings("unchecked")
	@Test	
	void rules() {
		final Button rulesButton =  fields.get("rulesButton");
		assertNotNull(rulesButton);
		
		@SuppressWarnings("rawtypes")
		final ComponentEventListener listener = listener(rulesButton);
		final Button.ClickEvent<?> event = clickEvent();
		listener.onComponentEvent(event);
		
		Mockito.verify(event.getSource().getUI().get()).navigate("rules");
	}
	
	@SuppressWarnings("unchecked")
	@Test	
	void users() {
		final Button usersButton =  fields.get("usersButton");
		assertNotNull(usersButton);
		
		@SuppressWarnings("rawtypes")
		final ComponentEventListener listener = listener(usersButton);
		final Button.ClickEvent<?> event = clickEvent();
		listener.onComponentEvent(event);
		
		Mockito.verify(event.getSource().getUI().get()).navigate("users");
	}
	
	
	
}
