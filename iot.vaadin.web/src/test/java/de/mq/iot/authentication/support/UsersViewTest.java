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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.AuthentificationService;
import de.mq.iot.authentication.Authority;
import de.mq.iot.model.Observer;
import de.mq.iot.state.support.SimpleNotificationDialog;
import de.mq.iot.support.ButtonBox;


class UsersViewTest {
	
	private static final String I18N_NEW_INFO_LABEL = "Benutzer neu anlegen";
	private static final String I18N_CHANGE_INFO_LABEL = "Passwort ändern";
	private static final String USER_NAME = "kminogue";
	private final Authentication authentication = Mockito.mock(Authentication.class);
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
		
		Mockito.doReturn(true).when(userModel).isAdmin();
		Mockito.doReturn(true).when(userModel).isPasswordChangeAllowed();
		
		Mockito.doReturn(Arrays.asList(user,otherUser)).when(authentificationService).authentifications();
		
		Mockito.doAnswer(answer -> {

			final UserModel.Events event = (UserModel.Events) answer.getArguments()[0];
			final Observer observer = (Observer) answer.getArguments()[1];
			observers.put(event, observer);
			return null;

		}).when(userModel).register(Mockito.any(), Mockito.any());

		
		 userView = new UsersView(authentificationService, userModel, messageSource, new ButtonBox(), notificationDialog);
		 
		assignFields();
	}


	private void assignFields() {
		fields.clear(); 
		fields.putAll(Arrays.asList(userView.getClass().getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).collect(Collectors.toMap(Field::getName, field -> ReflectionTestUtils.getField(userView, field.getName()))));
	}
	
	
	@Test
	public final void initAdminUser() {
		
		
		assertEquals(25, fields.size());
		assertEquals(3, observers.size());
		
		observers.keySet().forEach(key -> Arrays.asList(UserModel.Events.values()).contains(key));
		
		
		final Grid<Authentication> userGrid = userGrid();
		
		assertNotNull(userGrid);
		
		final Collection<Authentication> users = fetchAll(userGrid);
		
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
		return  (Button) fields.get("addRoleButton");
	}


	@SuppressWarnings("unchecked")
	private <T>  ComboBox<T> roleComboBox() {
		return (ComboBox<T>) fields.get("roleCombobox");
	
	}
	
	@Test
	public final void initNotAdminUser() {
		
		Mockito.doReturn(false).when(userModel).isAdmin();
		
		userView = new UsersView(authentificationService, userModel, messageSource, new ButtonBox(), notificationDialog);
		 
		assignFields();
		
		final Grid<Authentication> userGrid = userGrid();
		
		assertNotNull(userGrid);
		
		final Collection<Authentication> users = fetchAll(userGrid);
		
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


	private <T> Collection<T> fetchAll(final Grid<T> userGrid) {
		return (Collection<T>) userGrid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList());
	}


	@SuppressWarnings("unchecked")
	private <T>  Grid<T> userGrid() {
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
		
		final Collection<Authentication> users = fetchAll(userGrid);
		
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
		assertEquals(USER_NAME,nameTextField.getValue());
		assertTrue(nameTextField.isReadOnly());
		assertTrue(deleteUserButton.isEnabled());
		assertTrue(saveRolesButton.isEnabled());
		assertFalse(nameTextField.isInvalid());
	}


	private Label changeInfoLabel() {
		return  (Label) fields.get("changeInfoLabel");
	}


	@SuppressWarnings("unchecked")
	private Grid<Authority> authorityGrid() {
	return  (Grid<Authority>) fields.get("authorityGrid");
		
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
		
		
		final HorizontalLayout layout = layout();;
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
		assertEquals(Authority.values().length, roleComboBox.getDataProvider().fetch(new Query<>()).collect(Collectors.toList()).size());
		roleComboBox.setValue(Authority.Systemvariables);
		
		final Grid<Authority> authorityGrid =authorityGrid();
		assertNotNull(authorityGrid);
		assertEquals(0, authorityGrid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList()).size());
	
		observers.get(UserModel.Events.AuthoritiesChanged).process();
		
		assertEquals(Arrays.asList(Authority.values()), authorityGrid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList()));
		assertNull(roleComboBox.getValue());
		
	}


}
