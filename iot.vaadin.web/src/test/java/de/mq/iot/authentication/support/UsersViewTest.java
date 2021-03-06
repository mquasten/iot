package de.mq.iot.authentication.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.AuthentificationService;
import de.mq.iot.authentication.Authority;
import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;
import de.mq.iot.state.support.SimpleNotificationDialog;
import de.mq.iot.support.ButtonBox;

class UsersViewTest {

	private static final String I18N_PASSWORD_LABEL = "password";
	private static final String I18N_USER_COLUMN = "user_column";
	private static final String I18N_ADMIN_REQUIRED = "admin_required";
	private static final String I18N_ROLES_COLUMN = "roles_column";
	private static final String I18N_INFO_NEW_LABEL = "info_new";
	private static final String I18N_INFO_CHANGE_LABEL = "info_change";
	private static final String I18N_EXISTS_LABEL = "exists";
	private static final String I18N_REQUIRED_LABEL = "required";
	private static final String I18N_SAVE_ROLES = "save_roles";
	private static final String I18N_SAVE_USERS = "save_users";
	private static final String I18N_NAME_LABEL = "name";
	private static final String I18N_USERS_ADMIN_REQUIRED = "users_admin_required";
	private static final String I18N_USER_EXISTS = "Bentzer bereits vorhanden";
	private static final String PASSWORD = "fever";
	private static final String I18N_NEW_INFO_LABEL = "Benutzer neu anlegen";
	private static final String I18N_CHANGE_INFO_LABEL = "Passwort ändern";
	private static final String USER_NAME = "kminogue";
	private final Authentication authentication = Mockito.mock(Authentication.class);
	private final AuthentificationService authentificationService = Mockito.mock(AuthentificationService.class);

	private final UserModel userModel = Mockito.mock(UserModel.class);

	private final MessageSource messageSource = Mockito.mock(MessageSource.class);

	private final SimpleNotificationDialog notificationDialog = Mockito.mock(SimpleNotificationDialog.class);

	private UsersView userView;

	private final Map<UserModel.Events, Observer> observers = new HashMap<>();

	private final Map<String, Object> fields = new HashMap<>();

	private final Authentication user = Mockito.mock(Authentication.class);

	private final Authentication otherUser = Mockito.mock(Authentication.class);
	
	private final Subject<?, ?> subject = Mockito.mock(Subject.class);

	@BeforeEach
	void setup() {

		Mockito.doReturn(true).when(userModel).isAdmin();
		Mockito.doReturn(true).when(userModel).isPasswordChangeAllowed();

		Mockito.doReturn(Arrays.asList(user, otherUser)).when(authentificationService).authentifications();

		Mockito.doAnswer(answer -> {

			final UserModel.Events event = (UserModel.Events) answer.getArguments()[0];
			final Observer observer = (Observer) answer.getArguments()[1];
			observers.put(event, observer);
			return null;

		}).when(userModel).register(Mockito.any(), Mockito.any());

		userView = new UsersView(authentificationService, userModel, messageSource, new ButtonBox(subject),
				notificationDialog);

		assignFields();
	}

	private void assignFields() {
		fields.clear();
		fields.putAll(Arrays.asList(userView.getClass().getDeclaredFields()).stream()
				.filter(field -> !Modifier.isStatic(field.getModifiers())).collect(Collectors.toMap(Field::getName,
						field -> ReflectionTestUtils.getField(userView, field.getName()))));
	}

	@Test
	public final void initAdminUser() {

		assertEquals(25, fields.size());
		assertEquals(3, observers.size());

		observers.keySet().forEach(key -> Arrays.asList(UserModel.Events.values()).contains(key));

		final Grid<Authentication> userGrid = userGrid();

		assertNotNull(userGrid);

		final Collection<Authentication> users = fetchAll(userGrid.getDataProvider());

		assertEquals(2, users.size());

		final Button deleteButton = deleteUserButton();
		assertNotNull(deleteButton);
		assertTrue(deleteButton.getParent().isPresent());

		assertFalse(deleteButton.isEnabled());

		final HorizontalLayout editorLayout = editorLayout();
		assertNotNull(editorLayout);

		assertTrue(editorLayout.isVisible());

		final Label infoLabel = infoLabel();
		assertNotNull(infoLabel);
		assertTrue(infoLabel.isVisible());

		final ComboBox<?> roleCombobox = roleComboBox();
		assertNotNull(roleCombobox);
		assertTrue(roleCombobox.getParent().isPresent());

		final Button addRoleButton = addRoleButton();
		assertNotNull(addRoleButton);
		assertTrue(addRoleButton.getParent().isPresent());

		final Button deleteRoleButton = deleteRoleButton();
		assertNotNull(deleteRoleButton);
		assertTrue(deleteRoleButton.getParent().isPresent());

		final Button saveRolesButton = saveRolesButton();
		assertNotNull(saveRolesButton);
		assertTrue(saveRolesButton.getParent().isPresent());

	}

	private Button saveRolesButton() {
		final Button saveRolesButton = (Button) fields.get("saveRolesButton");
		return saveRolesButton;
	}

	private Button deleteRoleButton() {
		final Button deleteRoleButton = (Button) fields.get("deleteRoleButton");
		return deleteRoleButton;
	}

	private Button addRoleButton() {
		return (Button) fields.get("addRoleButton");
	}

	@SuppressWarnings("unchecked")
	private <T> ComboBox<T> roleComboBox() {
		return (ComboBox<T>) fields.get("roleCombobox");

	}

	@Test
	public final void initNotAdminUser() {

		Mockito.doReturn(false).when(userModel).isAdmin();

		userView = new UsersView(authentificationService, userModel, messageSource, new ButtonBox(subject),
				notificationDialog);

		assignFields();

		final Grid<Authentication> userGrid = userGrid();

		assertNotNull(userGrid);

		final Collection<Authentication> users = fetchAll(userGrid.getDataProvider());

		assertEquals(2, users.size());

		final Button deleteButton = deleteUserButton();
		assertNotNull(deleteButton);
		assertFalse(deleteButton.getParent().isPresent());

		assertFalse(deleteButton.isEnabled());

		final HorizontalLayout editorLayout = editorLayout();
		assertNotNull(editorLayout);

		assertFalse(editorLayout.isVisible());

		final Label infoLabel = infoLabel();
		assertNotNull(infoLabel);
		assertFalse(infoLabel.isVisible());

		final ComboBox<?> roleCombobox = roleComboBox();
		assertNotNull(roleCombobox);
		assertFalse(roleCombobox.getParent().isPresent());

		final Button addRoleButton = addRoleButton();
		assertNotNull(addRoleButton);
		assertFalse(addRoleButton.getParent().isPresent());

		final Button deleteRoleButton = deleteRoleButton();
		assertNotNull(deleteRoleButton);
		assertFalse(deleteRoleButton.getParent().isPresent());

		final Button saveRolesButton = saveRolesButton();
		assertNotNull(saveRolesButton);
		assertFalse(saveRolesButton.getParent().isPresent());
	}

	private HorizontalLayout editorLayout() {
		final HorizontalLayout editorLayout = (HorizontalLayout) fields.get("editorLayout");
		return editorLayout;
	}

	private Label infoLabel() {
		final Label infoLabel = (Label) fields.get("infoLabel");
		return infoLabel;
	}

	private Button deleteUserButton() {
		final Button deleteButton = (Button) fields.get("deleteUserButton");
		return deleteButton;
	}

	private <T> Collection<T> fetchAll(final DataProvider<T, ?> dataProvider) {
		return (Collection<T>) dataProvider.fetch(new Query<>()).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private <T> Grid<T> userGrid() {
		return (Grid<T>) fields.get("userGrid");

	}

	@Test
	void userNameValueProvider() {
		Mockito.doReturn(USER_NAME).when(authentication).username();
		assertEquals(USER_NAME, userView.userNameValueProvider().apply(authentication));
	}

	@Test
	void authorityProvider() {
		assertEquals(Authority.Systemvariables.name(), userView.authorityProvider().apply(Authority.Systemvariables));
	}

	@Test
	void assign() {
		final Grid<Authentication> userGrid = userGrid();
		assertNotNull(userGrid);

		final Collection<Authentication> users = fetchAll(userGrid.getDataProvider());

		assertEquals(2, users.size());
		userGrid.select(user);

		Mockito.verify(userModel).assign(user);
	}

	@Test
	void selectionChangedObserver() {

		final Label infoLabel = infoLabel();
		assertNotNull(infoLabel);
		infoLabel.setVisible(false);

		final Label changeInfoLabel = changeInfoLabel();
		assertNotNull(changeInfoLabel);
		changeInfoLabel.setText(I18N_CHANGE_INFO_LABEL);

		final TextField nameTextField = nameTextField();
		assertNotNull(nameTextField);
		assertFalse(nameTextField.isReadOnly());
		nameTextField.setInvalid(true);

		final Button deleteUserButton = deleteUserButton();
		assertNotNull(deleteUserButton);
		assertFalse(deleteUserButton.isEnabled());

		final Button saveRolesButton = saveRolesButton();
		assertNotNull(saveRolesButton);
		assertFalse(saveRolesButton.isEnabled());

		final HorizontalLayout editorLayout = editorLayout();
		assertNotNull(editorLayout);
		editorLayout.setVisible(false);

		final Grid<Authority> authorityGrid = authorityGrid();
		assertNotNull(authorityGrid);
		assertFalse(authorityGrid.getParent().isPresent());

		Mockito.doReturn(Optional.of(user)).when(userModel).authentication();
		Mockito.doReturn(USER_NAME).when(user).username();

		observers.get(UserModel.Events.SeclectionChanged).process();

		assertEquals(I18N_CHANGE_INFO_LABEL, infoLabel.getText());
		assertTrue(infoLabel.isVisible());
		assertTrue(editorLayout.isVisible());
		assertTrue(authorityGrid.getParent().isPresent());
		assertEquals(USER_NAME, nameTextField.getValue());
		assertTrue(nameTextField.isReadOnly());
		assertTrue(deleteUserButton.isEnabled());
		assertTrue(saveRolesButton.isEnabled());
		assertFalse(nameTextField.isInvalid());
	}

	private Label changeInfoLabel() {
		return (Label) fields.get("changeInfoLabel");
	}

	@SuppressWarnings("unchecked")
	private Grid<Authority> authorityGrid() {
		return (Grid<Authority>) fields.get("authorityGrid");

	}

	private TextField nameTextField() {
		final TextField nameTextField = (TextField) fields.get("nameTextField");
		return nameTextField;
	}

	@Test
	void selectionChangedObserverUserNotSelected() {

		final Label infoLabel = infoLabel();
		assertNotNull(infoLabel);
		infoLabel.setVisible(false);

		final Label newInfoLabel = newInfoLabel();
		assertNotNull(newInfoLabel);

		newInfoLabel.setText(I18N_NEW_INFO_LABEL);

		final TextField nameTextField = nameTextField();
		assertNotNull(nameTextField);
		nameTextField.setValue(USER_NAME);
		nameTextField.setReadOnly(true);
		nameTextField.setInvalid(true);

		final Button deleteUserButton = deleteUserButton();
		assertNotNull(deleteUserButton);
		deleteUserButton.setEnabled(true);

		final Button saveRolesButton = saveRolesButton();
		assertNotNull(saveRolesButton);
		saveRolesButton.setEnabled(true);

		final HorizontalLayout editorLayout = editorLayout();
		assertNotNull(editorLayout);
		editorLayout.setVisible(false);

		final Grid<Authority> authorityGrid = authorityGrid();
		assertNotNull(authorityGrid);

		final HorizontalLayout layout = layout();
		;
		assertNotNull(authorityGrid);
		layout.add(authorityGrid);

		Mockito.doReturn(Optional.empty()).when(userModel).authentication();

		observers.get(UserModel.Events.SeclectionChanged).process();

		assertEquals(I18N_NEW_INFO_LABEL, infoLabel.getText());
		assertTrue(infoLabel.isVisible());
		assertTrue(editorLayout.isVisible());
		assertFalse(authorityGrid.getParent().isPresent());
		assertTrue(nameTextField.getValue().isEmpty());
		assertFalse(nameTextField.isReadOnly());
		assertFalse(deleteUserButton.isEnabled());
		assertFalse(saveRolesButton.isEnabled());
		assertFalse(nameTextField.isInvalid());
	}

	private Label newInfoLabel() {
		final Label newInfoLabel = (Label) fields.get("newInfoLabel");
		return newInfoLabel;
	}

	private HorizontalLayout layout() {
		return (HorizontalLayout) fields.get("layout");
	}

	@Test
	void authoritiesChanged() {
		Mockito.doReturn(Arrays.asList(Authority.Systemvariables, Authority.Users)).when(userModel).authorities();

		final ComboBox<Authority> roleComboBox = roleComboBox();
		assertNotNull(roleComboBox);
		assertEquals(Authority.values().length,
				roleComboBox.getDataProvider().fetch(new Query<>()).collect(Collectors.toList()).size());
		roleComboBox.setValue(Authority.Systemvariables);

		final Grid<Authority> authorityGrid = authorityGrid();
		assertNotNull(authorityGrid);
		assertEquals(0, authorityGrid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList()).size());

		observers.get(UserModel.Events.AuthoritiesChanged).process();

		assertEquals(Arrays.asList(Authority.Systemvariables, Authority.Users),
				authorityGrid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList()));
		assertNull(roleComboBox.getValue());

	}

	@Test
	void changeUser() {
		final TextField nameTextField = nameTextField();
		assertNotNull(nameTextField);
		nameTextField.setValue(USER_NAME);
		nameTextField.setInvalid(true);

		final TextField passwordTextField = passwordTextField();
		assertNotNull(passwordTextField);
		passwordTextField.setValue(PASSWORD);
		passwordTextField.setInvalid(true);

		final Button saveButton = saveButton();
		assertNotNull(saveButton);

		Mockito.doReturn(Optional.of(authentication)).when(userModel).authentication();
		Mockito.doReturn(USER_NAME).when(userModel).login();
		Mockito.doReturn(PASSWORD).when(userModel).password();

		listener(saveButton).onComponentEvent(null);

		Mockito.verify(userModel).assignLogin(USER_NAME);
		Mockito.verify(userModel).assignPassword(PASSWORD);
		Mockito.verify(authentificationService).changePassword(USER_NAME, PASSWORD);

		assertTrue(passwordTextField.getValue().isEmpty());
		assertFalse(passwordTextField.isInvalid());
		assertFalse(nameTextField.isInvalid());
	}

	private Button saveButton() {
		final Button saveButton = (Button) fields.get("saveButton");
		return saveButton;
	}

	@Test
	void changeUserNew() {
		final Grid<Authentication> userGrid = userGrid();
		assertNotNull(userGrid);
		userGrid.setItems(Arrays.asList());

		final TextField nameTextField = nameTextField();
		assertNotNull(nameTextField);
		nameTextField.setValue(USER_NAME);
		nameTextField.setInvalid(true);

		final TextField passwordTextField = passwordTextField();
		assertNotNull(passwordTextField);
		passwordTextField.setValue(PASSWORD);
		passwordTextField.setInvalid(true);

		final Button saveButton = saveButton();
		assertNotNull(saveButton);

		Mockito.doReturn(Optional.empty()).when(userModel).authentication();
		Mockito.doReturn(USER_NAME).when(userModel).login();
		Mockito.doReturn(PASSWORD).when(userModel).password();

		Mockito.doReturn(true).when(authentificationService).create(USER_NAME, PASSWORD);
		listener(saveButton).onComponentEvent(null);

		Mockito.verify(userModel).assignLogin(USER_NAME);
		Mockito.verify(userModel).assignPassword(PASSWORD);
		Mockito.verify(authentificationService).create(USER_NAME, PASSWORD);

		assertTrue(passwordTextField.getValue().isEmpty());
		assertFalse(passwordTextField.isInvalid());
		assertFalse(nameTextField.isInvalid());

		assertEquals(Arrays.asList(user, otherUser), fetchAll(userGrid.getDataProvider()));
	}

	private TextField passwordTextField() {
		final TextField passwordTextField = (TextField) fields.get("passwordTextField");
		return passwordTextField;
	}

	@SuppressWarnings("unchecked")
	private ComponentEventListener<?> listener(final Button saveButton) {
		final ComponentEventBus eventBus = (ComponentEventBus) ReflectionTestUtils.getField(saveButton, "eventBus");
		final Map<Class<?>, ?> map = (Map<Class<?>, ?>) ReflectionTestUtils.getField(eventBus, "componentEventData");
		return DataAccessUtils.requiredSingleResult((Collection<ComponentEventListener<?>>) ReflectionTestUtils
				.getField(map.values().iterator().next(), "listeners"));
	}

	@Test
	void changeUserNewAlreadyExists() {
		final Label userAlreadyExists = userAlreadyExists();
		assertNotNull(userAlreadyExists);
		userAlreadyExists.setText(I18N_USER_EXISTS);

		final TextField nameTextField = nameTextField();
		assertNotNull(nameTextField);
		nameTextField.setValue(USER_NAME);

		final TextField passwordTextField = passwordTextField();
		assertNotNull(passwordTextField);
		passwordTextField.setValue(PASSWORD);

		final Button saveButton = saveButton();
		assertNotNull(saveButton);

		Mockito.doReturn(Optional.empty()).when(userModel).authentication();
		Mockito.doReturn(USER_NAME).when(userModel).login();
		Mockito.doReturn(PASSWORD).when(userModel).password();

		Mockito.doReturn(false).when(authentificationService).create(USER_NAME, PASSWORD);
		listener(saveButton).onComponentEvent(null);

		Mockito.verify(userModel).assignLogin(USER_NAME);
		Mockito.verify(userModel).assignPassword(PASSWORD);
		Mockito.verify(authentificationService).create(USER_NAME, PASSWORD);

		assertEquals(I18N_USER_EXISTS, nameTextField.getErrorMessage());
		assertTrue(nameTextField.isInvalid());

	}

	private Label userAlreadyExists() {
		final Label userAlreadyExists = (Label) fields.get("userAlreadyExists");
		return userAlreadyExists;
	}

	@Test
	void changeUserInvalid() {
		final TextField nameTextField = nameTextField();
		assertNotNull(nameTextField);

		final TextField passwordTextField = passwordTextField();
		assertNotNull(passwordTextField);

		final Button saveButton = saveButton();
		assertNotNull(saveButton);

		Mockito.doReturn(Optional.of(authentication)).when(userModel).authentication();
		Mockito.doReturn(USER_NAME).when(userModel).login();
		Mockito.doReturn(PASSWORD).when(userModel).password();

		listener(saveButton).onComponentEvent(null);

		Mockito.verify(userModel, Mockito.never()).assignLogin(Mockito.any());
		Mockito.verify(userModel, Mockito.never()).assignPassword(Mockito.any());
		Mockito.verify(authentificationService, Mockito.never()).changePassword(Mockito.any(), Mockito.any());

		assertTrue(passwordTextField.isInvalid());
		assertTrue(nameTextField.isInvalid());
	}

	@Test
	void deleteUser() {
		final Button deleteUserButton = deleteUserButton();
		assertNotNull(deleteUserButton);

		final Grid<Authentication> userGrid = userGrid();
		assertNotNull(userGrid);
		userGrid.setItems(Arrays.asList());
		Mockito.doReturn(USER_NAME).when(authentication).username();
		Mockito.doReturn(Optional.of(authentication)).when(userModel).authentication();
		listener(deleteUserButton).onComponentEvent(null);

		Mockito.verify(authentificationService).delete(USER_NAME);

		assertEquals(Arrays.asList(user, otherUser), fetchAll(userGrid.getDataProvider()));
	}

	@Test
	void addRole() {
		final Button addRoleButton = addRoleButton();
		assertNotNull(addRoleButton);

		final ComboBox<Authority> roleComboBox = roleComboBox();
		assertNotNull(roleComboBox);

		roleComboBox.setValue(Authority.Systemvariables);

		listener(addRoleButton).onComponentEvent(null);

		Mockito.verify(userModel).assign(Authority.Systemvariables);
	}

	@Test
	void deleteRole() {
		final Button deleteRoleButton = deleteRoleButton();
		assertNotNull(deleteRoleButton);

		final Grid<Authority> authorityGrid = authorityGrid();
		authorityGrid.setItems((Arrays.asList(Authority.values())));

		authorityGrid.select(Authority.Systemvariables);

		listener(deleteRoleButton).onComponentEvent(null);

		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Collection<Authority>> authorityCaptor = ArgumentCaptor.forClass(Collection.class);

		Mockito.verify(userModel).delete(authorityCaptor.capture());
		assertEquals(1, authorityCaptor.getValue().size());
		assertEquals(Authority.Systemvariables, authorityCaptor.getValue().stream().findAny().get());

	}

	@Test
	void saveRoles() {

		Mockito.doReturn(Optional.of(authentication)).when(userModel).authentication();
		Mockito.when(authentication.username()).thenReturn(USER_NAME);
		Mockito.when(userModel.authorities()).thenReturn(Arrays.asList(Authority.values()));
		Mockito.doReturn(true).when(authentificationService).changeAuthorities(USER_NAME,
				Arrays.asList(Authority.values()));
		final Button saveRolesButton = saveRolesButton();
		assertNotNull(saveRolesButton);

		final Grid<Authentication> userGrid = userGrid();
		assertNotNull(userGrid);
		userGrid.setItems(Arrays.asList());

		listener(saveRolesButton).onComponentEvent(null);

		assertEquals(Arrays.asList(user, otherUser), fetchAll(userGrid.getDataProvider()));
		Mockito.verify(userModel).assign((Authentication) null);

	}

	@Test
	void saveRolesNoAdminUser() {

		Mockito.doReturn(Optional.of(authentication)).when(userModel).authentication();
		Mockito.when(authentication.username()).thenReturn(USER_NAME);
		Mockito.when(userModel.authorities()).thenReturn(Arrays.asList(Authority.Systemvariables));

		final Button saveRolesButton = saveRolesButton();
		assertNotNull(saveRolesButton);

		final Label noAdminUserLabel = (Label) fields.get("noAdminUserLabel");
		assertNotNull(noAdminUserLabel);
		noAdminUserLabel.setText(I18N_USERS_ADMIN_REQUIRED);

		listener(saveRolesButton).onComponentEvent(null);

		notificationDialog.showError(I18N_USERS_ADMIN_REQUIRED);

	}

	@Test
	void deleteRoleButtonEnabled() {
		final Button deleteRoleButton = deleteRoleButton();
		assertNotNull(deleteRoleButton);
		assertFalse(deleteRoleButton.isEnabled());

		final Grid<Authority> authorityGrid = authorityGrid();
		assertNotNull(authorityGrid);
		authorityGrid.setItems(Arrays.asList(Authority.values()));
		authorityGrid.select(Authority.Systemvariables);

		assertTrue(deleteRoleButton.isEnabled());

		authorityGrid.deselect(Authority.Systemvariables);

		assertFalse(deleteRoleButton.isEnabled());
	}
	@Test
	void i18n() {
		
		
		final Label nameLabel = nameLabel();
		assertNotNull(nameLabel);
		final Button saveUsersButton = saveButton();
		assertNotNull(saveUsersButton);
		final Button saveRolesButton = saveRolesButton();
		assertNotNull(saveRolesButton);
		final Label mandatoryLabel = mandatoryLabel();
		assertNotNull(mandatoryLabel);
		final Label userAlreadyExists = userAlreadyExists();
		assertNotNull(userAlreadyExists);
		final Label changeInfoLabel = changeInfoLabel();
		assertNotNull(changeInfoLabel);
		final Label newInfoLabel = newInfoLabel();
		assertNotNull(newInfoLabel);
		final Label infoLabel = infoLabel();
		assertNotNull(infoLabel);
		final Label rolesColumnLabel = rolesColumnLabel();
		assertNotNull(rolesColumnLabel);
		final Label noAdminUserLabel = noAdminUserLabel();
		assertNotNull(noAdminUserLabel);
		final Label userColumnLabel = userColumnLabel();
		assertNotNull(userColumnLabel);
		final Label passwordLabel = passwordLabel();
		assertNotNull(passwordLabel);
		
		final Observer observer = observers.get(UserModel.Events.ChangeLocale);
		assertNotNull(observer);
		Arrays.asList(I18N_NAME_LABEL, I18N_SAVE_USERS, I18N_SAVE_ROLES, I18N_REQUIRED_LABEL, I18N_EXISTS_LABEL, I18N_INFO_CHANGE_LABEL, I18N_INFO_NEW_LABEL, I18N_ROLES_COLUMN, I18N_ADMIN_REQUIRED, I18N_USER_COLUMN, I18N_PASSWORD_LABEL).forEach(key -> {
			Mockito.doReturn(key).when(messageSource).getMessage("users_"+ key, null, "???", Locale.GERMAN);
		});

		Mockito.doReturn(Locale.GERMAN).when(userModel).locale();
	
		observer.process();
		
		assertEquals(I18N_NAME_LABEL, nameLabel.getText());
		assertEquals(I18N_SAVE_USERS,saveUsersButton.getText());
		assertEquals(I18N_SAVE_ROLES, saveRolesButton.getText());
		assertEquals(I18N_REQUIRED_LABEL, mandatoryLabel.getText());
		assertEquals(I18N_EXISTS_LABEL, userAlreadyExists.getText());
		assertEquals(I18N_INFO_CHANGE_LABEL,changeInfoLabel.getText());
		assertEquals(I18N_INFO_NEW_LABEL, newInfoLabel.getText());
		assertEquals(I18N_INFO_NEW_LABEL, infoLabel.getText());
		assertEquals(I18N_ROLES_COLUMN, rolesColumnLabel.getText());
		assertEquals(I18N_ADMIN_REQUIRED, noAdminUserLabel.getText());
		assertEquals(I18N_USER_COLUMN, userColumnLabel.getText());
		assertEquals(I18N_PASSWORD_LABEL, passwordLabel.getText());
		
	}

	private Label passwordLabel() {
		final Label passwordLabel = (Label) fields.get("passwordLabel");
		return passwordLabel;
	}

	private Label userColumnLabel() {
		final Label userColumnLabel = (Label) fields.get("userColumnLabel");
		return userColumnLabel;
	}

	private Label noAdminUserLabel() {
		final Label noAdminUserLabel = (Label) fields.get("noAdminUserLabel");
		return noAdminUserLabel;
	}

	private Label rolesColumnLabel() {
		final Label rolesColumnLabel = (Label) fields.get("rolesColumnLabel");
		assertNotNull(rolesColumnLabel);
		return rolesColumnLabel;
	}

	private Label mandatoryLabel() {
		final Label mandatoryLabel = (Label) fields.get("mandatoryLabel");
		return mandatoryLabel;
	}

	private Label nameLabel() {
		final Label nameLabel = (Label) fields.get("nameLabel");
		return nameLabel;
	}

}
