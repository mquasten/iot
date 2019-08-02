package de.mq.iot.rule.support;




import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;

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
	
	private final Grid<String> rulesGrid = new Grid<String>();
	
	private final Grid<Object> resultGrid = new Grid<Object>();
	
	private final TextArea exceptions = new TextArea();
	  private final HorizontalLayout root = new HorizontalLayout(rulesGrid, resultGrid);
	  
	  private final HorizontalLayout exceptionsLayout = new HorizontalLayout(exceptions);
	SimpleAggrgationResultsDialog() {
	

	this.dialog.add(root);
	this.dialog.add(exceptionsLayout);
	VerticalLayout buttonLayout = new VerticalLayout(closeButton);
	this.dialog.add(buttonLayout);
	buttonLayout.setHorizontalComponentAlignment(Alignment.CENTER, closeButton);
	
	exceptions.setLabel("Exceptions:");
	exceptions.setWidth("100vh");
	
	
	exceptions.setReadOnly(true);
	
	//exceptions.setSizeFull();
	
	closeButton.setText("ok");
	
	root.setSizeFull();

	
	dialog.setCloseOnEsc(true);
	dialog.setCloseOnOutsideClick(true);
	
	
	rulesGrid.getElement().getStyle().set("overflow", "auto");
	resultGrid.getElement().getStyle().set("overflow", "auto");
	root.setWidth("100vh");

	
	//rulesGrid.setHeight("50vH");

	
	exceptions.getElement().getStyle().set("overflow", "auto");
	
	rulesGrid.addColumn((ValueProvider<String, String>) x -> x).setHeader("Regeln");
	resultGrid.addColumn((ValueProvider<Object,String>) result -> result.toString()).setHeader("Ergebnisse");
	
	closeButton.addClickListener(event -> dialog.close());
	
	
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	void show(final RulesAggregateResult<?> rulesAggregate){
		
		

		final StringWriter stringWriter = new StringWriter();
		rulesAggregate.exceptions().forEach(entry -> {
			stringWriter.append(entry.getKey() +":" + System.getProperty("line.separator"));
			
			
			PrintWriter printWriter = new PrintWriter(stringWriter);
			entry.getValue().printStackTrace(printWriter);
		
			
			
		});
		
		exceptions.setValue(stringWriter.toString());
		exceptionsLayout.setVisible(!rulesAggregate.exceptions().isEmpty());
		
		exceptionsLayout.setHeight("30vH");
		
		rulesGrid.setItems(rulesAggregate.processedRules());
		resultGrid.setItems((Collection<Object>)rulesAggregate.states());
		dialog.open();
	}
	

}
