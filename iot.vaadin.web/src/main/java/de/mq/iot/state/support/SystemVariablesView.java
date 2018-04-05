package de.mq.iot.state.support;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import de.mq.iot.state.StateService;

@Route("")
@Theme(Lumo.class)
@SpringComponent
@UIScope
class SystemVariablesView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	

	@Autowired
	SystemVariablesView(StateService stateService) {
	
		
		final Grid<State<?>> grid = new Grid<>(50);
		final HorizontalLayout layout = new HorizontalLayout(grid);
		grid.getElement().getStyle().set("overflow", "auto");
		final FormLayout formLayout = new FormLayout();

		final TextField name = new TextField();
		name.setSizeFull();

		final TextField lastUpdate = new TextField();
		lastUpdate.setSizeFull();
		final TextField value = new TextField();
		value.setSizeFull();
		formLayout.addFormItem(name, "Name");
		formLayout.addFormItem(lastUpdate, "Änderungsdatum");
		formLayout.addFormItem(value, "Wert");
		
		// value.setErrorMessage("sucks");
		// value.setInvalid(true);

		final Label info = new Label("Textvariable ändern");

		formLayout.setSizeFull();

		formLayout.setResponsiveSteps(new ResponsiveStep("95vH", 1));

		final Button resetButton = new Button("abbrechen");
		final Button saveButton = new Button("speichern");

		final VerticalLayout buttonLayout = new VerticalLayout(resetButton, saveButton);

		final HorizontalLayout editorLayout = new HorizontalLayout(formLayout, buttonLayout);

		editorLayout.setVerticalComponentAlignment(Alignment.CENTER, buttonLayout);

		editorLayout.setSizeFull();
		
		
		grid.addColumn((ValueProvider<State<?>, Long>) state -> state.id()).setVisible(false);
		grid.addColumn((ValueProvider<State<?>, String>) state -> state.name()).setHeader("Name").setResizable(true);
		grid.addColumn((ValueProvider<State<?>, String>) state -> String.valueOf(state.value())).setHeader("Wert").setResizable(true);
		grid.setSelectionMode(SelectionMode.SINGLE);
		
		add(layout, info, editorLayout);
		setHorizontalComponentAlignment(Alignment.CENTER, info);
	
		
		layout.setSizeFull();
	
		setHorizontalComponentAlignment(Alignment.CENTER, layout);
	
	
		grid.setHeight("50vH");
		
		
	
		grid.setItems(stateService.states());
		
		


	
		
	}

}
