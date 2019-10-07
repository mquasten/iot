package de.mq.iot.authentication.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.Authority;
import de.mq.iot.authentication.support.UserModel.Events;
import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;

class UserModelTest {

	private static final String PASSWORD = "password";

	private static final String LOGIN = "kminogue";

	@SuppressWarnings("unchecked")
	private final Subject<UserModel.Events, UserModel> subject = Mockito.mock(Subject.class);

	private final UserModel userModel = new UserModelIml(subject);

	private final Observer observer = Mockito.mock(Observer.class);

	private final Authentication authentication = Mockito.mock(Authentication.class);

	@Test
	void create() {

		final Collection<Object> dependencies = Arrays.asList(userModel.getClass().getDeclaredFields()).stream()
				.filter(field -> field.getType().equals(Subject.class))
				.map(field -> ReflectionTestUtils.getField(userModel, field.getName())).collect(Collectors.toList());
		assertEquals(1, dependencies.size());
		assertEquals(subject, dependencies.stream().findAny().get());
	}

	@Test
	void register() {
		userModel.register(UserModel.Events.SeclectionChanged, observer);

		Mockito.verify(subject).register(UserModel.Events.SeclectionChanged, observer);
	}

	@Test
	void notifyObservers() {
		userModel.notifyObservers(UserModel.Events.AuthoritiesChanged);

		Mockito.verify(subject).notifyObservers(UserModel.Events.AuthoritiesChanged);
	}

	@Test
	void locale() {
		assertEquals(Locale.GERMAN, userModel.locale());
	}

	@Test
	void assign() {
		Mockito.when(authentication.authorities()).thenReturn(Arrays.asList(Authority.Users));

		userModel.assign(authentication);

		assertEquals(1, userModel.authorities().size());
		assertEquals(Arrays.asList(Authority.Users), userModel.authorities());
		assertEquals(Optional.of(authentication), userModel.authentication());

		Mockito.verify(subject).notifyObservers(Events.SeclectionChanged);
		Mockito.verify(subject).notifyObservers(Events.AuthoritiesChanged);

	}

	@Test
	void assignNUll() {

		userModel.assign((Authentication) null);

		assertEquals(0, userModel.authorities().size());

		assertEquals(Optional.empty(), userModel.authentication());

		Mockito.verify(subject).notifyObservers(Events.SeclectionChanged);
		Mockito.verify(subject).notifyObservers(Events.AuthoritiesChanged);

	}

	@Test
	void assignLogin() {
		assertNull(userModel.login());

		userModel.assignLogin(LOGIN);

		assertEquals(LOGIN, userModel.login());
	}

	@Test
	void assignPassword() {
		assertNull(userModel.password());

		userModel.assignPassword(PASSWORD);

		assertEquals(PASSWORD, userModel.password());
	}
}
