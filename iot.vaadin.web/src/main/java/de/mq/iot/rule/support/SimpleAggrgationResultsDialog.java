package de.mq.iot.rule.support;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;

import org.springframework.context.MessageSource;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.function.ValueProvider;


import de.mq.iot.model.I18NKey;
import de.mq.iot.model.LocalizeView;


@I18NKey("rules_aggregation_results_")
class SimpleAggrgationResultsDialog implements LocalizeView {

	@I18NKey("close")
	private final Button closeButton = new Button();

	private final Dialog dialog;
	private final Grid<String> rulesGrid = new Grid<String>();

	private final Grid<Object> resultGrid = new Grid<Object>();

	private final TextArea exceptions = new TextArea();
	private final HorizontalLayout resultsLayout = new HorizontalLayout(rulesGrid, resultGrid);

	private final HorizontalLayout exceptionsLayout = new HorizontalLayout(exceptions);
	@I18NKey("rules")
	private final Label rulesHeader = new Label();
	
	@I18NKey("results")
	private final Label resultsHeader = new Label();
	
	@I18NKey("exceptions")
	private final Label errors = new Label();
	
	
	private final RuleDefinitionModel ruleDefinitionModel;
	
	private final MessageSource messageSource;

	SimpleAggrgationResultsDialog(final RuleDefinitionModel ruleDefinitionModel, final MessageSource messageSource,  final Dialog dialog) {
	
		this.dialog = dialog;
		this.ruleDefinitionModel=ruleDefinitionModel;
		this.messageSource=messageSource;
		this.dialog.add(resultsLayout);
		this.dialog.add(exceptionsLayout);
		VerticalLayout buttonLayout = new VerticalLayout(closeButton);
		this.dialog.add(buttonLayout);
		buttonLayout.setHorizontalComponentAlignment(Alignment.CENTER, closeButton);

		exceptions.setLabel(errors.getText());
		exceptions.setWidth("85vh");

		exceptions.setReadOnly(true);

		// exceptions.setSizeFull();

		

		resultsLayout.setSizeFull();

		this.dialog.setCloseOnEsc(true);
		this.dialog.setCloseOnOutsideClick(true);

		rulesGrid.getElement().getStyle().set("overflow", "auto");
		resultGrid.getElement().getStyle().set("overflow", "auto");
		resultsLayout.setWidth("85vh");

		// rulesGrid.setHeight("50vH");

		exceptions.getElement().getStyle().set("overflow", "auto");

		rulesGrid.addColumn( valueProvider()).setHeader(rulesHeader);
		resultGrid.addColumn( valueProviderToString()).setHeader(resultsHeader);

		closeButton.addClickListener(event -> dialog.close());
		resultsLayout.setVisible(false);
		exceptionsLayout.setVisible(false);
		
	
		registerObserver();
		
		
		ruleDefinitionModel.notifyObservers(RuleDefinitionModel.Events.ChangeLocale);
	}

	private void registerObserver() {
		this.ruleDefinitionModel.register(RuleDefinitionModel.Events.ChangeLocale, () -> {
			localize(this.messageSource, this.ruleDefinitionModel.locale()); 
			exceptions.setLabel(errors.getText());
		});
	}

	@SuppressWarnings("unchecked")
	void show(final RulesAggregateResult<?> rulesAggregate) {


		final StringWriter stringWriter = new StringWriter();
		rulesAggregate.exceptions().forEach(entry -> {
			stringWriter.append(entry.getKey() + ":" + System.getProperty("line.separator"));

			final PrintWriter printWriter = new PrintWriter(stringWriter);
			entry.getValue().printStackTrace(printWriter);

		});

		
		
		exceptions.setValue(stringWriter.toString());
		exceptionsLayout.setVisible(!rulesAggregate.exceptions().isEmpty());
		resultsLayout.setVisible(true);
		exceptionsLayout.setHeight("30vH");

		rulesGrid.setItems(rulesAggregate.processedRules());
		resultGrid.setItems((Collection<Object>) rulesAggregate.states());
		dialog.open();
	}

	public void show(final Exception exception) {
		

		
		final StringWriter stringWriter = new StringWriter();

		final PrintWriter printWriter = new PrintWriter(stringWriter);
		exception.printStackTrace(printWriter);

		exceptions.setValue(stringWriter.toString());

		exceptionsLayout.setVisible(true);
		resultsLayout.setVisible(false);
		exceptionsLayout.setHeight("80vH");
		dialog.open();
	}
	
	ValueProvider<String, String> valueProvider() {
		return value -> value;
	}
	
	ValueProvider<Object, String> valueProviderToString() {
		return value -> value.toString();
	}

}
