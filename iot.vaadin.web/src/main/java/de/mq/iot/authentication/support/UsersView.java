package de.mq.iot.authentication.support;


import java.util.Arrays;

import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcons;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.AuthentificationService;
import de.mq.iot.authentication.Authority;
import de.mq.iot.authentication.support.UserModel.Events;
import de.mq.iot.model.I18NKey;
import de.mq.iot.model.LocalizeView;
import de.mq.iot.state.support.SimpleNotificationDialog;
import de.mq.iot.support.ButtonBox;

@Route("users")
@Theme(Lumo.class)
@I18NKey("users_")
class UsersView extends VerticalLayout implements LocalizeView {

	private static final long serialVersionUID = 1L;
	@I18NKey("name")
	private final Label nameLabel = new Label();
	private final TextField nameTextField = new TextField();

	private final TextField passwordTextField = new TextField();

	private final Button deleteUserButton = new Button();
	
	private final ComboBox<Authority> roleCombobox = new ComboBox<>();
	
	
	
	private final Button addRoleButton = new Button();
	
	private final Button deleteRoleButton = new Button();

	@I18NKey("save_users")
	private final Button saveButton = new Button();
	
	
	@I18NKey("save_roles")
	private final Button saveRolesButton = new Button();

	@I18NKey("required")
	private final Label mandatoryLabel = new Label();

	@I18NKey("exists")
	private final Label userAlreadyExists = new Label();

	@I18NKey("info_change")
	private final Label changeInfoLabel = new Label();

	@I18NKey("info_new")
	private final Label newInfoLabel = new Label();

	@I18NKey("info_new")
	private final Label infoLabel = new Label();

	@I18NKey("roles_column")
	private final Label rolesColumnLabel = new Label();
	
	@I18NKey("admin_required")
	private final Label noAdminUserLabel = new Label();

	@I18NKey("user_column")
	private final Label userColumnLabel = new Label();

	@I18NKey("password")
	private final Label passwordLabel = new Label();

	private final Grid<Authentication> userGrid = new Grid<>();

	private final Grid<Authority> authorityGrid = new Grid<>();

	private final FormLayout formLayout = new FormLayout();

	private final VerticalLayout buttonLayout = new VerticalLayout(saveButton);

	private final HorizontalLayout editorLayout = new HorizontalLayout(formLayout, buttonLayout);
	
	private final HorizontalLayout layout = new HorizontalLayout(userGrid);
	
	private final SimpleNotificationDialog notificationDialog;

	UsersView(final AuthentificationService authentificationService, final UserModel userModel, final MessageSource messageSource, final ButtonBox buttonBox, final SimpleNotificationDialog notificationDialog) {

		this.notificationDialog=notificationDialog;
		
		createUI(buttonBox, userModel);

		userGrid.setItems(authentificationService.authentifications());
		
		
		userGrid.asSingleSelect().addValueChangeListener(selectionEvent -> {
			userModel.assign(selectionEvent.getValue());
		});

		userModel.register(UserModel.Events.ChangeLocale, () -> {

			localize(messageSource, userModel.locale());

		});

		userModel.register(UserModel.Events.SeclectionChanged, () -> selectionChangedObserver(userModel));
		
		userModel.register(UserModel.Events.AuthoritiesChanged, () -> authoritiesChanged(userModel));

		userModel.notifyObservers(Events.ChangeLocale);

		saveButton.addClickListener(event -> changeUser(authentificationService, userModel));

		deleteUserButton.addClickListener(event -> deleteUser(authentificationService, userModel));
		
		
		roleCombobox.setItems(Arrays.asList(Authority.values()));
	
		addRoleButton.addClickListener(event -> {
			
			userModel.assign(roleCombobox.getValue());
			
		});
		
		deleteRoleButton.addClickListener(event -> {
			userModel.delete(authorityGrid.getSelectedItems());
		});
		
		
		saveRolesButton.addClickListener(event -> saveRoles(authentificationService, userModel));
		
		
	}

	private void saveRoles(final AuthentificationService authentificationService, final UserModel userModel) {
		userModel.authentication().ifPresent(authentication -> {
			final boolean saved = authentificationService.changeAuthorities(authentication.username(), userModel.authorities());
			
			if( saved) {
				userModel.assign((Authentication) null); 
			
		
				userGrid.setItems(authentificationService.authentifications());
			
			
				refresh(userGrid.getDataProvider());
			} else {
				
				notificationDialog.showError(noAdminUserLabel.getText());
			}
			
			
		});
	}

	private <T> void refresh(DataProvider<T,?> provider) {
	 provider.fetch(new Query<>()).forEach(item -> provider.refreshItem(item));
	}

	private void authoritiesChanged(UserModel userModel) {
		authorityGrid.setItems(userModel.authorities());
		roleCombobox.setValue(null);
		
	}

	private void deleteUser(final AuthentificationService authentificationService, final UserModel userModel) {
		userModel.authentication().ifPresent(authentication -> {
			authentificationService.delete(authentication.username());
			userGrid.setItems(authentificationService.authentifications());
		});
	}

	private void changeUser(final AuthentificationService authentificationService, final UserModel userModel) {
		final Binder<UserModel> binder = new Binder<>();
		binder.forField(nameTextField).withValidator(value -> StringUtils.hasText(value), mandatoryLabel.getText()).bind(UserModel::login, UserModel::assignLogin);

		binder.forField(passwordTextField).withValidator(value -> StringUtils.hasText(value), mandatoryLabel.getText()).bind(UserModel::password, UserModel::assignPassword);

		if (!binder.writeBeanIfValid(userModel)) {
			return;
		}

		if (userModel.authentication().isPresent()) {
			authentificationService.changePassword(userModel.login(), userModel.password());
			passwordTextField.setValue("");
			nameTextField.setInvalid(false);
			passwordTextField.setInvalid(false);
		} else {

			createNew(authentificationService, userModel);

		}

	}

	private void createNew(final AuthentificationService authentificationService, final UserModel userModel) {
		if (!authentificationService.create(userModel.login(), userModel.password())) {
			nameTextField.setInvalid(true);
			nameTextField.setErrorMessage(userAlreadyExists.getText());
			return;
		}

		nameTextField.setValue("");
		passwordTextField.setValue("");
		nameTextField.setInvalid(false);
		passwordTextField.setInvalid(false);
		userGrid.setItems(authentificationService.authentifications());
	}

	private void selectionChangedObserver(final UserModel userModel) {
	

		nameTextField.setReadOnly(false);
		nameTextField.setValue("");
		passwordTextField.setValue("");
		deleteUserButton.setEnabled(false);
		saveRolesButton.setEnabled(false);
		infoLabel.setText(newInfoLabel.getText());

		nameTextField.setInvalid(false);
		passwordTextField.setInvalid(false);
		
		roleCombobox.setValue(null);
		
		authorityGrid.getParent().ifPresent(parent ->  layout.remove(authorityGrid));
		userModel.authentication().ifPresent(authentication -> {
			
			layout.add(authorityGrid);
			infoLabel.setText(changeInfoLabel.getText());
			nameTextField.setValue(authentication.username());
			nameTextField.setReadOnly(true);
			deleteUserButton.setEnabled(true);
			saveRolesButton.setEnabled(true);

		});
		
	

	}

	private void createUI(final ButtonBox buttonBox, final UserModel userModel) {

	
		addRoleButton.setEnabled(false);
		deleteRoleButton.setEnabled(false);
		saveRolesButton.setEnabled(false);
		
		//roleCombobox.setWidth("80%");
		
		userGrid.getElement().getStyle().set("overflow", "auto");

		authorityGrid.getElement().getStyle().set("overflow", "auto");

		nameTextField.setSizeFull();

		passwordTextField.setSizeFull();

		// passwordTextField.setReadOnly(true);
		nameLabel.setText("Login");
		;
		passwordLabel.setText("Passwort");

		formLayout.addFormItem(nameTextField, nameLabel);
		formLayout.addFormItem(passwordTextField, passwordLabel);

		formLayout.setSizeFull();

		formLayout.setResponsiveSteps(new ResponsiveStep("10vH", 1));

		editorLayout.setVerticalComponentAlignment(Alignment.CENTER, buttonLayout);

		editorLayout.setSizeFull();

		userGrid.setSelectionMode(SelectionMode.SINGLE);

		add(buttonBox, layout, infoLabel, editorLayout);
		setHorizontalComponentAlignment(Alignment.CENTER, infoLabel);

		layout.setSizeFull();

		setHorizontalComponentAlignment(Alignment.CENTER, layout);

		userGrid.setHeight("50vH");
		authorityGrid.setHeight("50vH");
		userGrid.setSelectionMode(SelectionMode.SINGLE);
		authorityGrid.setSelectionMode(SelectionMode.SINGLE);

		deleteUserButton.setIcon(VaadinIcons.FILE_REMOVE.create());

		userGrid.addColumn((ValueProvider<Authentication, String>) authentication -> {
			return authentication.username();

		}).setHeader(userColumnLabel).setFooter(new HorizontalLayout(deleteUserButton));

		addRoleButton.setIcon((VaadinIcons.FILE_ADD.create()));
		deleteRoleButton.setIcon((VaadinIcons.FILE_REMOVE.create()));
		final HorizontalLayout roleFooterLayout = new HorizontalLayout(roleCombobox, addRoleButton,deleteRoleButton, saveRolesButton);
		
		authorityGrid.addColumn((ValueProvider<Authority, String>) authority -> {

			return authority.name();
		}).setHeader(rolesColumnLabel).setFooter(roleFooterLayout).setResizable(true);
		
		roleCombobox.addValueChangeListener(event ->  addRoleButton.setEnabled(userModel.authorityCanGranted(event.getValue())));

		
		authorityGrid.asSingleSelect().addValueChangeListener(selectionEvent -> {
			deleteRoleButton.setEnabled(selectionEvent.getValue() != null);
		});
	
	
	}



}
