package de.mq.iot.state.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.util.CollectionUtils;

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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import de.mq.iot.model.I18NKey;
import de.mq.iot.model.LocalizeView;
import de.mq.iot.state.Room;
import de.mq.iot.state.State;
import de.mq.iot.state.StateService;
import de.mq.iot.support.ButtonBox;

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

	@I18NKey("invalid_value")
	private final Label invalidValueLabel = new Label();

	@I18NKey("devices")
	private final Label devicesLabel = new Label();


	private final TextField valueField = new TextField();

	@I18NKey("value")
	private Label valueLabel = new Label();

	@I18NKey("info")
	private final Label stateInfoLabel = new Label();

	private final FormLayout formLayout = new FormLayout();
	
	private Column<?> devicesColumn; 
	
	private Collection<Column<?>> devicesValueColumn= new ArrayList<>();
	
	private final ComboBox<String> comboBox = new ComboBox<>();
	
	@I18NKey("type_state")
	private final Label typeStateLabel = new Label();
	@I18NKey("type_level")
	private final Label typeLevelLabel = new Label();
	
	private Map<String,Label> typeLabels = new HashMap<>();
	

	DeviceView(final StateService stateService, final DeviceModel deviveModel, final MessageSource messageSource, final ButtonBox buttonBox,  final StateToStringConverter  stateToStringConverter ) {

		typeLabels.put("STATE", typeStateLabel);
		typeLabels.put("LEVEL", typeLevelLabel);

		createUI(stateService, deviveModel, buttonBox, stateToStringConverter);

	  
		
		
		
		
		
		deviveModel.register(DeviceModel.Events.SeclectionChanged, () -> {
			valueField.setEnabled(deviveModel.isSelected());
			deviveModel.selectedDistinctSinglePercentValue().ifPresent(value -> valueField.setValue("" + stateToStringConverter.convertValue(value)));
			if (!deviveModel.isSelected()) {
				valueField.setInvalid(false);
				saveButton.setEnabled(false);
				valueField.clear();

			}
		});
		deviveModel.register(DeviceModel.Events.ValueChanged, () ->{
			if (deviveModel.isSelected()) {
				valueField.setInvalid(true);
				saveButton.setEnabled(false);
				valueField.setErrorMessage(invalidValueLabel.getText());
			}
			
			deviveModel.value().ifPresent(value -> {
				valueField.setErrorMessage("");
				valueField.setInvalid(false);
				saveButton.setEnabled(true);
				
			});
			
		});
		
		deviveModel.register(DeviceModel.Events.TypeChanged, () -> {
			grid.setItems(Arrays.asList());
			deviveModel.type().ifPresent(type -> grid.setItems(stateService.deviceStates(Arrays.asList(type))));
			
		});
		
		
		

		valueField.addValueChangeListener(event -> deviveModel.assign(event.getValue()));
		
		
		deviveModel.register(DeviceModel.Events.ChangeLocale, () -> {
			localize(messageSource, deviveModel.locale());
			devicesValueColumn.forEach(column -> column.setHeader(deviceValueLabel.getText()));
			
		});
		
		saveButton.addClickListener(event -> {
			
			deviveModel.value().ifPresent(value -> {
				
				 final Collection<State<Object>>  states = deviveModel.selectedDevices();
				 
				 states.forEach(state -> state.assign( value));
			
				 grid.setItems(stateService.update(  states));
				
				 deviveModel.assignType(comboBox.getValue());
			
				
				 
			});
			
		});
		
		
		

		deviveModel.notifyObservers(DeviceModel.Events.ChangeLocale);
		comboBox.setItems(stateService.deviceTypes());
		
		comboBox.getDataProvider().fetch(new Query<>()).findFirst().ifPresent(value -> comboBox.setValue(value));

	}

	private void createUI(final StateService stateService, final DeviceModel deviceModel, final ButtonBox buttonBox, final  StateToStringConverter  stateToStringConverter ) {

		saveButton.setEnabled(false);

		valueField.setEnabled(false);

		final HorizontalLayout layout = new HorizontalLayout(grid);
		grid.getElement().getStyle().set("overflow", "auto");

		final FormLayout searchLayout = new FormLayout();
		searchLayout.add(devicesLabel);
		comboBox.setItemLabelGenerator(value -> typeLabels.get(value).getText());
		
		comboBox.addValueChangeListener(event -> deviceModel.assignType(event.getValue()));
		
		
		comboBox.setAllowCustomValue(false);
		
		
		
		
		
		searchLayout.add(comboBox);
	
		
		formLayout.setSizeFull();

		formLayout.setResponsiveSteps(new ResponsiveStep("10vH", 1));

		valueField.setSizeFull();

		formLayout.addFormItem(valueField, valueLabel);

		final VerticalLayout buttonLayout = new VerticalLayout(saveButton);

		final HorizontalLayout editorLayout = new HorizontalLayout(formLayout, buttonLayout);

		editorLayout.setVerticalComponentAlignment(Alignment.CENTER, formLayout, buttonLayout);

		editorLayout.setSizeFull();

		grid.setSelectionMode(SelectionMode.NONE);

		add(buttonBox, layout, stateInfoLabel, editorLayout);
		setHorizontalComponentAlignment(Alignment.CENTER, stateInfoLabel);

		layout.setSizeFull();

		setHorizontalComponentAlignment(Alignment.CENTER, layout);

		grid.setHeight("50vH");

		grid.setSelectionMode(SelectionMode.SINGLE);

	
		devicesValueColumn.clear();
		devicesColumn=grid.addColumn(new ComponentRenderer<>(room -> {

			final Grid<State<Object>> devices = new Grid<State<Object>>();
		

			devices.setSelectionMode(SelectionMode.MULTI);
			devices.addColumn((ValueProvider<State<Object>, String>) state -> state.name()).setFlexGrow(80).setResizable(true).setHeader(room.name());
			
		
			final Column<State<Object>> column = devices.addColumn((ValueProvider<State<Object>, String>) state -> stateToStringConverter.convert(state));
			devicesValueColumn.add(column);
			column.addAttachListener(event -> deviceModel.notifyObservers(DeviceModel.Events.ChangeLocale));
		
			
			
			devices.setItems((Collection<State<Object>>)room.states());

			devices.setHeightByRows(true);

			devices.addSelectionListener(event -> {

				deviceModel.assign(room, event.getAllSelectedItems());

				if (CollectionUtils.isEmpty(event.getAllSelectedItems())) {

					devices.setItems(room.states());
				}

			});

			return devices;
		}));
	
		
		devicesColumn.setHeader(searchLayout);
		grid.setHeightByRows(true);

		
		
	

	}

}
