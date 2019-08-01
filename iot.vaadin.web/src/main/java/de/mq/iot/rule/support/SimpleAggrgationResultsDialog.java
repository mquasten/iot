package de.mq.iot.rule.support;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.function.ValueProvider;

public class SimpleAggrgationResultsDialog {
	

	private final Button closeButton = new Button();
  
	private final Dialog dialog = new Dialog();
	
	private final Grid<String> rules = new Grid<String>();
	
	private final TextArea exceptions = new TextArea();
	  private final HorizontalLayout root = new HorizontalLayout(rules, exceptions);
	SimpleAggrgationResultsDialog(final RulesAggregateResult<?> rulesAggregate) {
	

	this.dialog.add(root);
	VerticalLayout buttonLayout = new VerticalLayout(closeButton);
	this.dialog.add(buttonLayout);
	buttonLayout.setHorizontalComponentAlignment(Alignment.CENTER, closeButton);
	
	exceptions.setLabel("Exceptions:");
	
	exceptions.setValue("eine Exception ...");
	exceptions.setReadOnly(true);
	
	closeButton.setText("ok");
	
	root.setSizeFull();

	
	dialog.setCloseOnEsc(true);
	dialog.setCloseOnOutsideClick(true);
	
	rules.getElement().getStyle().set("overflow", "auto");
	root.setWidth("90vh");

	rules.setHeight("50vH");
	rules.addColumn((ValueProvider<String, String>) x -> x).setHeader("Regeln");
	
	
	closeButton.addClickListener(event -> dialog.close());
	rules.setItems(rulesAggregate.processedRules());
	
	}
	
	
	
	void show(){
		
		dialog.open();
	}
	

}
