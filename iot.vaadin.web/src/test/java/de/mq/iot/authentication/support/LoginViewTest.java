package de.mq.iot.authentication.support;

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

import org.springframework.context.MessageSource;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.AuthentificationService;
import de.mq.iot.authentication.SecurityContext;
import de.mq.iot.model.Observer;

class LoginViewTest {

	private static final String PASSWD_FIELD_NAME = "passwd";

	private static final String USER_FIELD_NAME = "user";

	private static final String PASSWORD = "fever";

	private static final String USER = "kminogue";

	private static final String MESSAGE_FIELD_NAME = "message";

	private static final String I18N_LOGIN_BUTTON = "login_login";

	private static final String BUTTON_FIELD_NAME = "button";

	private static final String PASSWORD_LABEL_FIELD_NAME = "passwordLabel";

	private static final String USER_LABEL_FIELD_NAME = "userLabel";

	private static final String I18N_LOGIN_USER = "login_user";

	private static final String I18N_LOGIN_PASSWORD = "login_password";

	private static final String MESSAGE_SOURCE_FIELD_NAME = "messageSource";

	private static final String UI_FIELD_NAME = "ui";

	private static final String LOGIN_MODEL_FIELD_NAME = "loginModel";

	private static final String AUTHENTIFICATION_SERVICE_FIELD_NAME = "authentificationService";

	private static final String SECURITY_CONTEXT_FIELD_NAME = "securityContext";

	private final SecurityContext securityContext = Mockito.mock(SecurityContext.class);

	private final AuthentificationService authentificationService = Mockito.mock(AuthentificationService.class);
	private final LoginModel loginModel = Mockito.mock(LoginModel.class);
	private final UI ui = Mockito.mock(UI.class);
	private final MessageSource messageSource = Mockito.mock(MessageSource.class);

	private LoginView loginView;

	private final Map<LoginModel.Events, Observer> observers = new HashMap<>();

	private final Map<String, Object> fields = new HashMap<>();

	private final Authentication authentication = Mockito.mock(Authentication.class);

	@BeforeEach
	void setup() {

		Mockito.doReturn(Locale.GERMAN).when(loginModel).locale();
		Mockito.doReturn(LoginView.I18N_REQUIRED).when(messageSource).getMessage(LoginView.I18N_REQUIRED, null, Locale.GERMAN);
		Mockito.doReturn(LoginView.I18N_PASSWORD_INVALID).when(messageSource).getMessage(LoginView.I18N_PASSWORD_INVALID, null, Locale.GERMAN);
		Mockito.doReturn(LoginView.I18N_USER_NOT_FOUND).when(messageSource).getMessage(LoginView.I18N_USER_NOT_FOUND, null, Locale.GERMAN);
		Mockito.doReturn(I18N_LOGIN_PASSWORD).when(messageSource).getMessage(I18N_LOGIN_PASSWORD, null, "???", Locale.GERMAN);
		Mockito.doReturn(I18N_LOGIN_USER).when(messageSource).getMessage(I18N_LOGIN_USER, null, "???", Locale.GERMAN);

		Mockito.doReturn(I18N_LOGIN_BUTTON).when(messageSource).getMessage(I18N_LOGIN_BUTTON, null, "???", Locale.GERMAN);

		Mockito.doAnswer(answer -> {

			final LoginModel.Events event = (LoginModel.Events) answer.getArguments()[0];
			final Observer observer = (Observer) answer.getArguments()[1];
			observers.put(event, observer);
			return null;

		}).when(loginModel).register(Mockito.any(), Mockito.any());

		loginView = new LoginView(securityContext, authentificationService, loginModel, ui, messageSource);
		Arrays.asList(LoginView.class.getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).forEach(field -> fields.put(field.getName(), ReflectionTestUtils.getField(loginView, field.getName())));

		observers.get(LoginModel.Events.ChangeLocale).process();
		;

		Mockito.when(loginModel.login()).thenReturn(USER);
		Mockito.when(authentificationService.authentification(USER)).thenReturn(Optional.of(authentication));
		Mockito.when(loginModel.authenticate(authentication)).thenReturn(true);
	}

	@Test
	void fieldsDefaultValues() {
		assertEquals(11, fields.size());

		assertEquals(securityContext, fields.get(SECURITY_CONTEXT_FIELD_NAME));
		assertEquals(authentificationService, fields.get(AUTHENTIFICATION_SERVICE_FIELD_NAME));
		assertEquals(loginModel, fields.get(LOGIN_MODEL_FIELD_NAME));
		assertEquals(ui, fields.get(UI_FIELD_NAME));
		assertEquals(messageSource, fields.get(MESSAGE_SOURCE_FIELD_NAME));

		final Label passwordLabel = (Label) fields.get(PASSWORD_LABEL_FIELD_NAME);
		assertNotNull(passwordLabel);
		assertEquals(I18N_LOGIN_PASSWORD, passwordLabel.getText());

		final Label userLabel = (Label) fields.get(USER_LABEL_FIELD_NAME);
		assertNotNull(userLabel);
		assertEquals(I18N_LOGIN_USER, userLabel.getText());

		final Button loginButton = (Button) fields.get(BUTTON_FIELD_NAME);
		assertNotNull(loginButton);
		assertEquals(I18N_LOGIN_BUTTON, loginButton.getText());

		final Label message = (Label) fields.get(MESSAGE_FIELD_NAME);
		assertNotNull(message);
		assertFalse(message.isVisible());

	}

	@Test
	void login() {

		assignUserPassordAndLogin();

		Mockito.verify(loginModel).assignLogin(USER);

		Mockito.verify(loginModel).assignPassword(PASSWORD);
		Mockito.verify(authentificationService).authentification(USER);

		Mockito.verify(securityContext).assign(authentication);
		Mockito.verify(ui).navigate("");

		final Label message = (Label) fields.get(MESSAGE_FIELD_NAME);

		assertFalse(message.isVisible());

	}

	private void assignUserPassordAndLogin() {
		final TextField user = (TextField) fields.get(USER_FIELD_NAME);
		assertNotNull(user);
		user.setValue(USER);
		final PasswordField passwd = (PasswordField) fields.get(PASSWD_FIELD_NAME);
		assertNotNull(passwd);
		passwd.setValue(PASSWORD);
		doLogin();
	}

	protected void doLogin() {
		final Button loginButton = (Button) fields.get(BUTTON_FIELD_NAME);
		assertNotNull(loginButton);
		listener(loginButton).onComponentEvent(null);
	}

	@SuppressWarnings("unchecked")
	private ComponentEventListener<?> listener(final Button saveButton) {
		final ComponentEventBus eventBus = (ComponentEventBus) ReflectionTestUtils.getField(saveButton, "eventBus");
		final Map<Class<?>, ?> map = (Map<Class<?>, ?>) ReflectionTestUtils.getField(eventBus, "componentEventData");
		return DataAccessUtils.requiredSingleResult((Collection<ComponentEventListener<?>>) ReflectionTestUtils.getField(map.values().iterator().next(), "listeners"));
	}

	@Test
	void loginUserNotFound() {
		Mockito.when(authentificationService.authentification(USER)).thenReturn(Optional.empty());

		assignUserPassordAndLogin();

		Mockito.verify(loginModel).assignLogin(USER);

		Mockito.verify(loginModel).assignPassword(PASSWORD);
		Mockito.verify(authentificationService).authentification(USER);

		Mockito.verify(securityContext, Mockito.never()).assign(authentication);
		Mockito.verify(ui, Mockito.never()).navigate("");

		final Label message = (Label) fields.get(MESSAGE_FIELD_NAME);

		assertTrue(message.isVisible());
		assertEquals(LoginView.I18N_USER_NOT_FOUND, message.getText());

	}

	@Test
	void loginInvalidPassword() {

		Mockito.when(loginModel.authenticate(authentication)).thenReturn(false);

		assignUserPassordAndLogin();

		Mockito.verify(loginModel).assignLogin(USER);

		Mockito.verify(loginModel).assignPassword(PASSWORD);
		Mockito.verify(authentificationService).authentification(USER);

		Mockito.verify(securityContext, Mockito.never()).assign(authentication);
		Mockito.verify(ui, Mockito.never()).navigate("");

		final Label message = (Label) fields.get(MESSAGE_FIELD_NAME);

		assertTrue(message.isVisible());
		assertEquals(LoginView.I18N_PASSWORD_INVALID, message.getText());

	}

	@Test
	void loginMissingValues() {
		doLogin();

		Mockito.verify(loginModel, Mockito.never()).assignLogin(USER);
		Mockito.verify(loginModel, Mockito.never()).assignPassword(PASSWORD);

		final TextField user = (TextField) fields.get(USER_FIELD_NAME);
		assertNotNull(user);

		final PasswordField passwd = (PasswordField) fields.get(PASSWD_FIELD_NAME);
		assertNotNull(passwd);

		assertEquals(LoginView.I18N_REQUIRED, user.getErrorMessage());

		assertEquals(LoginView.I18N_REQUIRED, passwd.getErrorMessage());
	}

}
