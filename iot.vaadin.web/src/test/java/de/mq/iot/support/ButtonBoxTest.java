package de.mq.iot.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
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

import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;




class ButtonBoxTest {
	private final Subject<?, ?> subject = Mockito.mock(Subject.class);
	private  ButtonBox buttonBox;
	
	private final Map<String, Button> fields = new HashMap<>();
	
	private final Map<Object,Observer> observers = new HashMap<>(); 
	
	@BeforeEach
	void setup() {
		Mockito.when(subject.locale()).thenReturn(Locale.GERMAN);
		Mockito.doAnswer(answer -> {

			final Object event =  answer.getArguments()[0];
			final Observer observer = (Observer) answer.getArguments()[1];
			observers.put(event, observer);
			return null;

		}).when(subject).register(Mockito.any(), Mockito.any());
		
		buttonBox = new ButtonBox(subject);
		Arrays.asList(ButtonBox.class.getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())&& field.getType().equals(Button.class)).forEach(field -> fields.put(field.getName(), (Button) ReflectionTestUtils.getField(buttonBox, field.getName())));
		
		
		

		
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
	
	
	@SuppressWarnings("unchecked")
	@Test
	void assignLanguageDe() {
		final Button languageDeButton =  languageDeButton();
		
	
		@SuppressWarnings("rawtypes")
		final ComponentEventListener listener = listener(languageDeButton);
		final Button.ClickEvent<?> event = Mockito.mock(Button.ClickEvent.class);
		listener.onComponentEvent(event);
		Mockito.verify(subject).assign(Locale.GERMAN);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void assignLanguageEn() {
		final Button languageDeButton =  languageEnButton();
		
	
		@SuppressWarnings("rawtypes")
		final ComponentEventListener listener = listener(languageDeButton);
		final Button.ClickEvent<?> event = Mockito.mock(Button.ClickEvent.class);
		listener.onComponentEvent(event);
		Mockito.verify(subject).assign(Locale.ENGLISH);
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
	@Test	
	void defaultLangue() {
		assertFalse(languageDeButton().isVisible());
		assertTrue(languageEnButton().isVisible());
	}

	private Button languageEnButton() {
		final Button result = fields.get("languageEnButton");
		assertNotNull(result);
		return result;
	}

	private Button languageDeButton() {
		final Button result = fields.get("languageDeButton");
		assertNotNull(result);
		return result;
	}
	
	@Test
	void languageButtonsVisible() {
		assertEquals(observers.size(), 1);
		assertEquals(Optional.of(ButtonBox.CHANGE_LOCALE_EVENT), observers.keySet().stream().findAny());
		final Observer observer =observers.get(ButtonBox.CHANGE_LOCALE_EVENT);
		final Button languageDe= languageDeButton();
		final Button languageEn= languageEnButton();
		assertFalse(languageDe.isVisible());
		assertTrue(languageEn.isVisible());
		
		Mockito.when(subject.locale()).thenReturn(Locale.ENGLISH);
		observer.process();
		
		assertTrue(languageDe.isVisible());
		assertFalse(languageEn.isVisible());
	}
	
}
