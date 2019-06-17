package de.mq.iot.rule.support;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.context.MessageSource;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcons;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import de.mq.iot.model.I18NKey;
import de.mq.iot.model.LocalizeView;
import de.mq.iot.rule.RulesDefinition;
import de.mq.iot.rule.support.RuleDefinitionModel.Events;
import de.mq.iot.support.ButtonBox;

@Route("rules")
@Theme(Lumo.class)
@I18NKey("rules_")
class RulesDefinitionView extends VerticalLayout implements LocalizeView {

	static final String I18N_VALIDATION_PREFIX = "rules_validation_";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@I18NKey("id_column")
	private final Label ruleDefinitionColumnLabel = new Label();
	
	@I18NKey("input_parameter_column")
	private final Label inputParameterColumnLabel = new Label();
	
	@I18NKey("input_value_column")
	private final Label inputValueColumnLabel = new Label();
	
	@I18NKey("argument_parameter_column")
	private final Label argumentParameterColumnLabel = new Label();
	
	@I18NKey("argument_value_column")
	private final Label argumentValueColumnLabel = new Label();
	

	@I18NKey("optional_rules_column")
	private final Label optionalRuleColumnLabel = new Label();

	@I18NKey("value_column")
	private final Label valueColumnLabel = new Label();

	private final Grid<RulesDefinition> grid = new Grid<>();

	private final Grid<String> optionalRules = new Grid<>();

	private final Grid<Entry<String, String>> inputParameter = new Grid<>();

	private final Grid<Entry<String, String>> arguments = new Grid<>();

	private final TextField inputTextField = new TextField();
	@I18NKey("input_change")
	private final Button changeInputButton = new Button();
	
	private final Button addOptionalRulesButton = new Button();
	
	private final Button deleteOptionalRulesButton = new Button();
	
	private final ComboBox<String> optionalRulesComboBox = new ComboBox<>();

	private final HorizontalLayout editorLayout = new HorizontalLayout();
	private final HorizontalLayout layout = new HorizontalLayout(grid);


	// https://vaadin.com/components/vaadin-grid/java-examples/grid-editor

	RulesDefinitionView(final RuleDefinitionModel ruleDefinitionModel, final RulesService rulesService, final ButtonBox buttonBox, final MessageSource messageSource) {

		createUI(rulesService, buttonBox);
		grid.asSingleSelect().addValueChangeListener(selectionEvent -> ruleDefinitionModel.assignSelected(selectionEvent.getValue()));

		inputParameter.asSingleSelect().addValueChangeListener(selectionEvent -> ruleDefinitionModel.assignSelectedInput(selectionEvent.getValue()));
		optionalRules.asSingleSelect().addValueChangeListener(selectionEvent -> ruleDefinitionModel.assignOptionalRule(selectionEvent.getValue()));
		changeInputButton.addClickListener(event -> updateInput(ruleDefinitionModel, messageSource));

		grid.setItems(rulesService.rulesDefinitions());
		inputParameter.setItems(new ArrayList<>());

		ruleDefinitionModel.register(RuleDefinitionModel.Events.ChangeLocale, () -> {

			localize(messageSource, ruleDefinitionModel.locale());
		});

		ruleDefinitionModel.register(Events.AssignInput, () -> {
			inputTextField.setEnabled(ruleDefinitionModel.isInputSelected());
			changeInputButton.setEnabled(ruleDefinitionModel.isInputSelected());
			inputTextField.setValue(ruleDefinitionModel.selectedInputValue());
			
		});
		
		
		ruleDefinitionModel.register(Events.AssignOptionalRule, () -> {
			
			deleteOptionalRulesButton.setEnabled(ruleDefinitionModel.isOptionalRuleSelected());
			
			
		});
		

		ruleDefinitionModel.register(Events.AssignRuleDefinition, () -> {

			optionalRules.setItems(ruleDefinitionModel.optionalRules());
			inputParameter.setItems(ruleDefinitionModel.input());
			arguments.setItems(ruleDefinitionModel.parameter());
			optionalRulesComboBox.setItems(ruleDefinitionModel.definedOptionalRules());
			optionalRulesComboBox.setItemLabelGenerator( value  ->  value);
			optionalRulesComboBox.setValue(null);
			
		
			deleteOptionalRulesButton.setEnabled(false);
			
			arguments.getParent().ifPresent(parent -> layout.remove(arguments));
			inputParameter.getParent().ifPresent(parent -> editorLayout.remove(inputParameter));
			optionalRules.getParent().ifPresent(parent -> editorLayout.remove(optionalRules));
			
			if (ruleDefinitionModel.isSelected()) {
				editorLayout.add(inputParameter, optionalRules);
				layout.add(arguments);
			}

		});

		ruleDefinitionModel.notifyObservers(Events.ChangeLocale);

	}

	private void updateInput(final RuleDefinitionModel ruleDefinitionModel, MessageSource messageSource) {

		final String inputValue = inputTextField.getValue();

		final Optional<String> error = ruleDefinitionModel.validateInput(inputValue);

		
		
		
		
		inputTextField.setErrorMessage("");
		inputTextField.setInvalid(false);
		
		if (error.isPresent()) {
			inputTextField.setInvalid(true);
			final String message =  messageSource.getMessage(I18N_VALIDATION_PREFIX + error.get().toLowerCase() , null, "???", ruleDefinitionModel.locale());
			inputTextField.setErrorMessage(message);
			
			
			
			
		} else {

			ruleDefinitionModel.assignInput(inputValue);
			inputParameter.setItems(ruleDefinitionModel.input());
		}
	}

	private void createUI(final RulesService ruleService, final ButtonBox buttonBox) {

		inputTextField.setEnabled(false);
		changeInputButton.setEnabled(false);

	
		addOptionalRulesButton.setEnabled(false);
		deleteOptionalRulesButton.setEnabled(false);
		
		
		grid.getElement().getStyle().set("overflow", "auto");
		optionalRules.getElement().getStyle().set("overflow", "auto");
		inputParameter.getElement().getStyle().set("overflow", "auto");

		arguments.getElement().getStyle().set("overflow", "auto");

		grid.addColumn((ValueProvider<RulesDefinition, String>) rulesDefinition -> rulesDefinition.id().name()).setHeader(ruleDefinitionColumnLabel).setResizable(true);
		grid.setSelectionMode(SelectionMode.SINGLE);

		arguments.addColumn((ValueProvider<Entry<String, String>, String>) entry -> entry.getKey()).setHeader(argumentParameterColumnLabel).setResizable(true);
		arguments.addColumn((ValueProvider<Entry<String, String>, String>) entry -> entry.getValue()).setHeader(argumentValueColumnLabel).setResizable(true);

		inputParameter.addColumn((ValueProvider<Entry<String, String>, String>) entry -> entry.getKey()).setHeader(inputParameterColumnLabel).setFooter(changeInputButton).setResizable(true);

		inputParameter.addColumn((ValueProvider<Entry<String, String>, String>) entry -> entry.getValue()).setHeader(inputValueColumnLabel).setFooter(inputTextField).setResizable(true);

		grid.setSelectionMode(SelectionMode.SINGLE);

		// final HorizontalLayout persistentParameterLayout = new
		// HorizontalLayout(inputParameter, optionalRules);

		// persistentParameterLayout.setSizeFull();

		editorLayout.setVerticalComponentAlignment(Alignment.CENTER);

		// persistentParameterLayout.setVerticalComponentAlignment(Alignment.CENTER,
		// inputParameter);

		editorLayout.setSizeFull();
		final HorizontalLayout optionalRulesHeader = new HorizontalLayout();
		
		optionalRulesHeader.add(optionalRulesComboBox);
		
		addOptionalRulesButton.setIcon(VaadinIcons.FILE_ADD.create());
		optionalRulesHeader.add(addOptionalRulesButton);
		
		deleteOptionalRulesButton.setIcon(VaadinIcons.FILE_REMOVE.create());
		optionalRulesHeader.add(deleteOptionalRulesButton);
	
		optionalRules.addColumn((ValueProvider<String, String>) value -> value).setHeader(optionalRuleColumnLabel).setFooter(optionalRulesHeader).setResizable(true);

		add(buttonBox, layout, editorLayout);
		setHorizontalComponentAlignment(Alignment.CENTER);

		layout.setSizeFull();

		setHorizontalComponentAlignment(Alignment.CENTER, layout);

		grid.setHeight("30vH");

		optionalRules.setHeight("30vH");
		inputParameter.setHeight("30vH");

		arguments.setHeight("30vH");

	}

}
