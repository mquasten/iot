package de.mq.iot.state.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.MessageSource;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import de.mq.iot.model.I18NKey;
import de.mq.iot.model.LocalizeView;
import de.mq.iot.state.Room;
import de.mq.iot.state.State;
import de.mq.iot.state.StateService;
import de.mq.iot.state.StateService.DeviceType;
import de.mq.iot.support.ButtonBox;

import de.mq.iot.synonym.SynonymService;

@Route("devices")
@Theme(Lumo.class)
@I18NKey("devices_")
class DeviceView extends VerticalLayout implements LocalizeView {

	private static final long serialVersionUID = 1L;

	private final Grid<Room> grid = new Grid<>();

	@I18NKey("change")
	private final Button saveButton = new Button();

	@I18NKey("devices_value")
	private final Label deviceValueLabel = new Label();

	@I18NKey("invalid_value_level")
	private final Label invalidLevelValueLabel = new Label();

	@I18NKey("invalid_value_state")
	private final Label invalidStateValueLabel = new Label();

	@I18NKey("devices")
	private final Label devicesLabel = new Label();

	@I18NKey("value")
	private Label valueLabel = new Label();

	@I18NKey("info")
	private final Label stateInfoLabel = new Label();

	private final DeviceStateValueField stateValueField = new DeviceStateValueField();

	private Column<?> devicesColumn;

	private Collection<Column<?>> devicesValueColumn = new ArrayList<>();

	private final ComboBox<DeviceType> comboBox = new ComboBox<>();

	@I18NKey("type_state")
	private final Label typeStateLabel = new Label();
	@I18NKey("type_level")
	private final Label typeLevelLabel = new Label();

	private Map<DeviceType, Label> typeLabels = new HashMap<>();

	private Map<DeviceType, Label> errorMessages = new HashMap<>();

	DeviceView(final StateService stateService, final SynonymService synonymService, final DeviceModel deviveModel, final MessageSource messageSource, final ButtonBox buttonBox) {

		deviveModel.assign(synonymService.deviveSynonyms());
		
		typeLabels.put(DeviceType.State, typeStateLabel);
		typeLabels.put(DeviceType.Level, typeLevelLabel);

		errorMessages.put(DeviceType.Level, invalidLevelValueLabel);
		errorMessages.put(DeviceType.State, invalidStateValueLabel);
		createUI(stateService, deviveModel, buttonBox);

		deviveModel.register(DeviceModel.Events.SeclectionChanged, () -> {

			stateValueField.setEnabled(deviveModel.isSelected());

			deviveModel.selectedDistinctSingleViewValue().ifPresent(value -> stateValueField.setValue(value));
			if (!deviveModel.isSelected()) {
				saveButton.setEnabled(false);
				stateValueField.setErrorMessage(Optional.empty());
			}
		});
		deviveModel.register(DeviceModel.Events.ValueChanged, () -> {
			
			
			if (deviveModel.isSelected()) {
				saveButton.setEnabled(false);
				deviveModel.type().ifPresent(type -> stateValueField.setErrorMessage(Optional.ofNullable(errorMessages.get(type).getText())));

			}

			deviveModel.value().ifPresent(value -> {
				stateValueField.setErrorMessage(Optional.empty());
				saveButton.setEnabled(true);
			});

		});

		deviveModel.register(DeviceModel.Events.TypeChanged, () -> {
			
			
			grid.setItems(Arrays.asList());
			deviveModel.type().ifPresent(type -> grid.setItems(stateService.deviceStates(Arrays.asList(type))));
			deviveModel.type().ifPresent(type -> stateValueField.setDeviceType(type));

		});

		stateValueField.addConsumer(value -> deviveModel.assign(value));

		deviveModel.register(DeviceModel.Events.ChangeLocale, () -> {
			localize(messageSource, deviveModel.locale());
			stateValueField.localize(valueLabel.getText());
			devicesValueColumn.forEach(column -> column.setHeader(deviceValueLabel.getText()));

		});

		saveButton.addClickListener(event -> deviveModel.value().ifPresent(value -> update(stateService, deviveModel)));

		deviveModel.notifyObservers(DeviceModel.Events.ChangeLocale);
		
		
		comboBox.setItems(stateService.deviceTypes());

		comboBox.getDataProvider().fetch(new Query<>()).findFirst().ifPresent(value -> comboBox.setValue(value));

	}

	private void update(final StateService stateService, final DeviceModel deviveModel) {
		final Collection<State<Object>> states = deviveModel.changedValues();

		stateService.update(states);

		grid.getDataProvider().refreshAll();

		deviveModel.clearSelection();
	}

	private void createUI(final StateService stateService, final DeviceModel deviceModel, final ButtonBox buttonBox) {

	
		
		saveButton.setEnabled(false);

		stateValueField.setEnabled(false);

		final HorizontalLayout layout = new HorizontalLayout(grid);
		grid.getElement().getStyle().set("overflow", "auto");

		final FormLayout searchLayout = new FormLayout();
		searchLayout.add(devicesLabel);
		comboBox.setItemLabelGenerator(value -> typeLabels.get(value).getText());

		comboBox.addValueChangeListener(event -> deviceModel.assignType(event.getValue()));

		comboBox.setAllowCustomValue(false);

		searchLayout.add(comboBox);

		stateValueField.setSizeFull();

		stateValueField.setResponsiveSteps(new ResponsiveStep("10vH", 1));

		stateValueField.setSizeFull();

		final VerticalLayout buttonLayout = new VerticalLayout(saveButton);

		final HorizontalLayout editorLayout = new HorizontalLayout(stateValueField, buttonLayout);

		editorLayout.setVerticalComponentAlignment(Alignment.CENTER, stateValueField, buttonLayout);

		editorLayout.setSizeFull();
		
		setEditorVisisble(editorLayout, deviceModel);

		grid.setSelectionMode(SelectionMode.NONE);

		add(buttonBox, layout, stateInfoLabel, editorLayout);
		setHorizontalComponentAlignment(Alignment.CENTER, stateInfoLabel);

		layout.setSizeFull();

		setHorizontalComponentAlignment(Alignment.CENTER, layout);

		grid.setHeight("50vH");

		grid.setSelectionMode(SelectionMode.SINGLE);

		devicesValueColumn.clear();
		devicesColumn = grid.addColumn(new ComponentRenderer<>(new DeviceStateComponentRendererFunction(deviceModel,devicesValueColumn)));

		devicesColumn.setHeader(searchLayout);
		grid.setHeightByRows(true);

	}

	private void setEditorVisisble(final HorizontalLayout editorLayout, final DeviceModel deviceModel) {
		
		editorLayout.setVisible(deviceModel.isChangeDeviceAllowed());
		stateInfoLabel.setVisible(deviceModel.isChangeDeviceAllowed());
	}

}
