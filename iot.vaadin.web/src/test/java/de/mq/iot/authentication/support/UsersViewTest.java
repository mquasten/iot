package de.mq.iot.authentication.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.Query;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.AuthentificationService;
import de.mq.iot.model.Observer;
import de.mq.iot.state.support.SimpleNotificationDialog;
import de.mq.iot.support.ButtonBox;


class UsersViewTest {
	
	
	private final AuthentificationService authentificationService = Mockito.mock(AuthentificationService.class);
	
	private final UserModel userModel= Mockito.mock(UserModel.class);
	
	private final MessageSource messageSource = Mockito.mock(MessageSource.class);
	
	private final SimpleNotificationDialog notificationDialog = Mockito.mock(SimpleNotificationDialog.class);
	
	private UsersView userView;
	
	private final Map<UserModel.Events, Observer> observers = new HashMap<>();
	
	private final Map<String, Object> fields = new HashMap<>();

	private final Authentication user = Mockito.mock(Authentication.class);
	
	private final Authentication otherUser = Mockito.mock(Authentication.class);
	
	
	@BeforeEach
	void setup() {
		
		
		Mockito.doReturn(Arrays.asList(user,otherUser)).when(authentificationService).authentifications();
		
		Mockito.doAnswer(answer -> {

			final UserModel.Events event = (UserModel.Events) answer.getArguments()[0];
			final Observer observer = (Observer) answer.getArguments()[1];
			observers.put(event, observer);
			return null;

		}).when(userModel).register(Mockito.any(), Mockito.any());

		
		 userView = new UsersView(authentificationService, userModel, messageSource, new ButtonBox(), notificationDialog);
		 
		fields.putAll(Arrays.asList(userView.getClass().getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).collect(Collectors.toMap(Field::getName, field -> ReflectionTestUtils.getField(userView, field.getName()))));
	}
	
	
	@Test
	public final void init() {
		
		Mockito.doReturn(true, false).when(userModel).isPasswordChangeAllowed();
		
		assertEquals(25, fields.size());
		assertEquals(3, observers.size());
		
		observers.keySet().forEach(key -> Arrays.asList(UserModel.Events.values()).contains(key));
		
		
		final Grid<Authentication> userGrid = userGrid();
		
		assertNotNull(userGrid);
		
		final Collection<Authentication> users = fetchAll(userGrid);
		
		assertEquals(2, users.size());
		
	}


	private <T> Collection<T> fetchAll(final Grid<T> userGrid) {
		return (Collection<T>) userGrid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList());
	}


	@SuppressWarnings("unchecked")
	private <T>  Grid<T> userGrid() {
		return (Grid<T>) fields.get("userGrid");
		
	}

}
