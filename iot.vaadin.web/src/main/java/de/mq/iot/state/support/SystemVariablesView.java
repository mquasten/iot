package de.mq.iot.state.support;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
	private final Grid<State<?>> grid = new Grid<>();

	
	@Autowired
	SystemVariablesView(StateService stateService) {
		grid.addColumn((ValueProvider<State<?>, Long>) state -> state.id()).setVisible(false);
		grid.addColumn((ValueProvider<State<?>, String>) state -> state.name()).setHeader("Name");
		grid.addColumn((ValueProvider<State<?>, String>) state -> String.valueOf(state.value())).setHeader("Wert");
		add(grid);
		grid.setHeightByRows(true);

		grid.setItems(stateService.states());

	}

}
