package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.flow.component.dialog.Dialog;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.SecurityContext;
import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;
import de.mq.iot.model.support.SubjectImpl;
import de.mq.iot.state.State;
import de.mq.iot.state.support.DeviceModel.Events;


class StateModelConfigurationTest {
	
	
	private final StateModelConfiguration stateModelConfiguration = new StateModelConfiguration();
	final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
	private final ConversionService conversionService = Mockito.mock(ConversionService.class);
	
	private final Subject<StateModel.Events, StateModel> subject = new SubjectImpl<>(securityContext);
	
	@Test
	void subject() {
		final Authentication currentUser = Mockito.mock(Authentication.class);
		Mockito.when(subject.currentUser()).thenReturn(Optional.of(currentUser));
		
		
		assertTrue(stateModelConfiguration.subject(securityContext) instanceof SubjectImpl);
		assertEquals(Optional.of(currentUser), stateModelConfiguration.subject(securityContext).currentUser());
	}
	
	
	@Test
	void  stateValueConverter() {
		final Converter<State<?>, String> converter = stateModelConfiguration.stateValueConverter(conversionService);
		
		assertTrue(converter instanceof StateValueConverterImpl);
		
		final Map<Class<?>, Object> dependencies = dependencies(converter, ConversionService.class);
		assertEquals(1, dependencies.size());
		assertEquals(conversionService, dependencies.get(ConversionService.class));
	}


	private Map<Class<?>, Object> dependencies(final Object target, Class<?> ... clazz) {
		final List<Class<?>> dependencyClasses = Arrays.asList(clazz);
		final Map<Class<?>, Object> dependencies = new HashMap<>();
		Arrays.asList(target.getClass().getDeclaredFields()).stream().filter(field -> dependencyClasses.contains(field.getType())).forEach(field -> dependencies.put(field.getType(), ReflectionTestUtils.getField(target, field.getName())) );;
	    return dependencies;
	}
	
	@Test
	void stateModel() {
		final StateModel stateModel = stateModelConfiguration.stateModel(subject, conversionService); 
		
		assertTrue(stateModel instanceof StateModelImpl);
		
		final Map<Class<?>, Object> dependencies = dependencies(stateModel, ConversionService.class, Subject.class);
		assertEquals(2, dependencies.size());
		assertEquals(conversionService, dependencies.get(ConversionService.class));
		assertEquals(subject, dependencies.get(Subject.class));
	}
	
	@Test
	void  messageSource() {
		final MessageSource messageSource = stateModelConfiguration.messageSource(); 
		assertTrue(messageSource instanceof ResourceBundleMessageSource);
		
		final List<String> basenames = new ArrayList<>( ((ResourceBundleMessageSource)messageSource).getBasenameSet());
		assertEquals(6, basenames.size());
		assertEquals(StateModelConfiguration.SYSTEM_VARIABLES_VIEW ,basenames.get(0));
		assertEquals(StateModelConfiguration.LOGIN_VIEW ,basenames.get(1));
		assertEquals(StateModelConfiguration.CALENDAR_VIEW ,basenames.get(2));
		assertEquals(StateModelConfiguration.DEVICE_VIEW ,basenames.get(3));
		assertEquals(StateModelConfiguration.RULES_VIEW ,basenames.get(4));
		assertEquals(StateModelConfiguration.USER_VIEW ,basenames.get(5));
		assertEquals(StateModelConfiguration.MESSAGE_SOURCE_ENCODING, ReflectionTestUtils.invokeMethod(messageSource, "getDefaultEncoding"));
	}
	
	@Test
	void notificationDialog() {
		final Dialog dialog = Mockito.mock(Dialog.class);
		final SimpleNotificationDialog notificationDialog = stateModelConfiguration.notificationDialog(dialog);
		assertEquals(dialog, DataAccessUtils.requiredSingleResult(Arrays.asList(SimpleNotificationDialog.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(Dialog.class)).map(field -> ReflectionTestUtils.getField(notificationDialog, field.getName())).collect(Collectors.toList())));
	}
	
	@Test
	void dialog() {
		
		Arrays.asList(StateModelConfiguration.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(Class.class)).forEach(field -> ReflectionTestUtils.setField(stateModelConfiguration, field.getName(), DialogMock.class));
		assertEquals(DialogMock.class, stateModelConfiguration.dialog().getClass());;
		
		
	}
	
	@Test
	void buttonBox() {
		assertNotNull(stateModelConfiguration.buttonBox());
	}
	
	
	@Test
	void deviceModel() {
		
		@SuppressWarnings("unchecked")
		final Subject<DeviceModel.Events , DeviceModel> subject = Mockito.mock(Subject.class);
		final Observer observer = Mockito.mock(Observer.class);
		final ConversionService conversionService = Mockito.mock(ConversionService.class);
		
		final DeviceModel model = stateModelConfiguration.deviceModel(subject, conversionService); 
		
		assertNotNull(model);
		model.register(Events.SeclectionChanged, observer);
		Mockito.verify(subject).register(Events.SeclectionChanged, observer);
		
		assertEquals(conversionService, ReflectionTestUtils.getField(model, "conversionService"));
	}
	
}

class DialogMock extends Dialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	  public void setOpened(boolean opened) {
	  
	  }
}


