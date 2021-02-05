package de.mq.iot.model.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.support.DataAccessUtils;

import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.SecurityContext;
import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;


class SubjectTest {
	
	private final Observer  observer  = Mockito.mock(Observer.class); 
	
	private final SecurityContext securityContext = Mockito.mock(SecurityContext.class);

	 private final Subject<Event, Object> subject = new SubjectImpl<>(securityContext, true);
	 
	 private final Authentication currentUser =  Mockito.mock(Authentication.class);
	 
	 final Observer observerLocaleCanged = Mockito.mock(Observer.class);
	 
	 @BeforeEach
	 void setup() {
		 Mockito.when(securityContext.authentication()).thenReturn(Optional.of(currentUser));
		 subject.register(Event.ChangeLocale, observerLocaleCanged);
	 }
	 
	 
	 @Test
	 void register() {
		 assertEquals(observer, subject.register(Event.UpdateModel, observer));
		
		final Map<Event,Set<Consumer<Object>>> observers = observersMap();
	 
		assertEquals(1, observers.get(Event.UpdateModel).size());
		assertEquals(1, observers.get(Event.UpdateModel).size());
		assertEquals(observer, observers.get(Event.UpdateModel).stream().findFirst().get());
		
		
		assertEquals(observer, subject.register(Event.UpdateModel, observer));
		
		assertEquals(1, observers.get(Event.UpdateModel).size());
		assertEquals(1, observers.get(Event.UpdateModel).size());
		assertEquals(observer, observers.get(Event.UpdateModel).stream().findFirst().get());
	 }

	 
	 @SuppressWarnings("unchecked")
	 private  Map<Event,Set<Consumer<Object>>> observersMap() {
		return  (Map<Event, Set<Consumer<Object>>>) DataAccessUtils.requiredSingleResult(Arrays.asList(SubjectImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(Map.class)).map(field -> ReflectionTestUtils.getField(subject, field.getName())).collect(Collectors.toList()));
		
	 }
	 
	 @Test
	 void notifyObservers() {
		 subject.register(Event.UpdateModel, observer);
		 subject.notifyObservers(Event.UpdateModel);
		 Mockito.verify(observer).process();
	 }
	 
	 @Test
	 void notifyObserversNotCalled() {
		 subject.register(Event.UpdateModel, observer);
		 subject.notifyObservers(Event.ChangeLocale);
		 Mockito.verify(observer, Mockito.never()).process();
		 
	 }
	 
	 @Test
	 void securityContext() {
		 assertEquals(Optional.of(currentUser), subject.currentUser());
	 }
	 @Test
	 void currentUser() {
		 assertEquals(Optional.of(currentUser), subject.currentUser());
	 }
	 
	 @Test
	 void locale() {
		 Mockito.doReturn(Locale.GERMAN).when(securityContext).locale();
		 assertEquals(Locale.GERMAN, subject.locale());
		 
		 Mockito.doReturn(Locale.ENGLISH).when(securityContext).locale();
		 assertEquals(Locale.ENGLISH, subject.locale());
	 }
	 
	 @Test
	 void assign() {
		subject.assign(Locale.ENGLISH);
		
		Mockito.verify(securityContext, Mockito.times(1)).assign(Locale.ENGLISH);
		
		Mockito.verify(observerLocaleCanged).process();
	 }
	 
	

}


enum Event {
	
	UpdateModel,
	ChangeLocale;
}