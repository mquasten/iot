package de.mq.iot.state.support;

import org.springframework.context.MessageSource;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
	
	@I18NKey("room")
	private final Label roomLabel = new Label();
	
	
	@I18NKey("devices")
	private final Label devicesLabel = new Label();
	
	private final MessageSource messageSource;
	
	
	
	@I18NKey("info")
	private final Label stateInfoLabel = new Label();
	
	
	private final FormLayout formLayout = new FormLayout();

	DeviceView(final StateService stateService, final DeviceModel deviveModel, final MessageSource messageSource, final ButtonBox buttonBox) {

		this.messageSource = messageSource;

	

		createUI(deviveModel, buttonBox);

		
		grid.setItems(stateService.deviceStates());
		
		
		deviveModel.notifyObservers(DeviceModel.Events.ChangeLocale);
		

		
		
	}

	private void createUI(final DeviceModel deviceModel, final ButtonBox buttonBox) {

		
		saveButton.setEnabled(false);

		
		
	

		final HorizontalLayout layout = new HorizontalLayout(grid);
		grid.getElement().getStyle().set("overflow", "auto");

		


		formLayout.setSizeFull();

		formLayout.setResponsiveSteps(new ResponsiveStep("10vH", 1));

		final VerticalLayout buttonLayout = new VerticalLayout(saveButton);

		final HorizontalLayout editorLayout = new HorizontalLayout(formLayout, buttonLayout);
		

		editorLayout.setVerticalComponentAlignment(Alignment.CENTER, buttonLayout);

		editorLayout.setSizeFull();

		grid.setSelectionMode(SelectionMode.SINGLE);

		add(buttonBox, layout, stateInfoLabel, editorLayout);
		setHorizontalComponentAlignment(Alignment.CENTER, stateInfoLabel);
	
		layout.setSizeFull();

		setHorizontalComponentAlignment(Alignment.CENTER, layout);

		grid.setHeight("50vH");

	
		grid.setSelectionMode(SelectionMode.SINGLE);
		
	
		
	
		grid.addColumn((ValueProvider<Room, String>) room -> room.name()).setHeader(roomLabel).setResizable(true);
		
		
			
		grid.addColumn(new ComponentRenderer<>(
		        room  -> {
		        
		        final Grid<State<Double>> devices = new Grid<State<Double>>();
		        devices.addColumn((ValueProvider<State<Double>, String>) state -> state.name());
		        devices.setItems(room.states());
		       
		        devices.setHeightByRows(true);
		        devices.setSelectionMode(SelectionMode.MULTI);
		        return devices ; 
		        })).setHeader("Geräte");
		
		
		
		  grid.setHeightByRows(true);
		
		deviceModel.register(DeviceModel.Events.ChangeLocale, () -> {	
			localize(messageSource, deviceModel.locale());
		});
		
			
	}



	
}
