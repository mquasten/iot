package de.mq.iot.rule.support;


import java.util.Arrays;

import org.springframework.context.MessageSource;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Label;
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
import de.mq.iot.support.ButtonBox;

@Route("rules")
@Theme(Lumo.class)
@I18NKey("rules_")
class  RulesDefinitionView extends VerticalLayout implements LocalizeView {
	
	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@I18NKey("name")
	private final Label nameLabel = new Label(); 
	private final TextField nameTextField = new TextField();
	@I18NKey("date")
	private final Label dateLabel = new Label(); 
	private final TextField lastUpdateTextField = new TextField();
	private final TextField valueTextField = new TextField();
	private final ComboBox<Object> valueComboBox = new ComboBox<>();
	@I18NKey("reset")
	private  final Button resetButton = new Button();
	@I18NKey("save")
	private  final Button saveButton = new Button();
	
	private  final Label stateInfoLabel = new Label();
	
	@I18NKey("value")
	private final Label textValueLabel = new Label();
	
	@I18NKey("value")
	private final Label listValueLabel = new Label();
	
	@I18NKey("id_column")
	private final Label ruleDefinitionColumnLabel = new Label();
	
	@I18NKey("optional_rules_column")
	private final Label optionalRuleColumnLabel = new Label();
	
	
	@I18NKey("value_column")
	private final Label valueColumnLabel = new Label();
	private FormItem textFieldFormItem ; 
	private FormItem comboBoxFormItem ; 
	private final Grid<RulesDefinition> grid = new Grid<>();
	
	
	private final Grid<String> optionalRules = new Grid<>();
	
	private final FormLayout formLayout = new FormLayout();
	
	


	
	RulesDefinitionView( final RuleDefinitionModel ruleDefinitionModel, final RulesService rulesService, final ButtonBox buttonBox, final MessageSource messageSource ) {
	
	
		createUI(rulesService, buttonBox);	
		grid.asSingleSelect().addValueChangeListener(selectionEvent -> {System.out.println("selected");});
		
		grid.setItems(rulesService.rulesDefinitions());
		optionalRules.setItems(Arrays.asList("rule1" ,"rule2"));
		
		
		ruleDefinitionModel.register(RuleDefinitionModel.Events.ChangeLocale, () -> {
			
			 localize(messageSource, ruleDefinitionModel.locale());
		
			
		});
		
		
		ruleDefinitionModel.notifyObservers(RuleDefinitionModel.Events.ChangeLocale);
		
	}


	

	
	


	

	


	

	



	private void createUI(final RulesService ruleService, final ButtonBox buttonBox) {
				
	
		saveButton.setEnabled(false);
		resetButton.setEnabled(false);
		
		valueComboBox.setRequired(true);
		valueTextField.setRequired(true);
		
		
		final HorizontalLayout layout = new HorizontalLayout(grid,optionalRules);
		grid.getElement().getStyle().set("overflow", "auto");
		optionalRules.getElement().getStyle().set("overflow", "auto");

	
		nameTextField.setSizeFull();
		nameTextField.setReadOnly(true);
	
		lastUpdateTextField.setSizeFull();
		lastUpdateTextField.setReadOnly(true);
		
		valueTextField.setSizeFull();
		valueComboBox.setSizeFull();
		valueTextField.setReadOnly(true);
		formLayout.addFormItem(nameTextField, nameLabel);
		formLayout.addFormItem(lastUpdateTextField, dateLabel);
		textFieldFormItem=formLayout.addFormItem(valueTextField, textValueLabel);
		comboBoxFormItem=formLayout.addFormItem(valueComboBox, listValueLabel);
		comboBoxFormItem.setVisible(false);
		

		formLayout.setSizeFull();

		formLayout.setResponsiveSteps(new ResponsiveStep("10vH",1));

		

		final VerticalLayout buttonLayout = new VerticalLayout(resetButton, saveButton);

		
		
		final HorizontalLayout editorLayout = new HorizontalLayout(formLayout, buttonLayout);

		editorLayout.setVerticalComponentAlignment(Alignment.CENTER, buttonLayout);

		editorLayout.setSizeFull();
		
		
	
		grid.addColumn((ValueProvider<RulesDefinition, String>) rulesDefinition -> rulesDefinition.id().name()).setHeader(ruleDefinitionColumnLabel).setResizable(true);
	   
		grid.setSelectionMode(SelectionMode.SINGLE);
		
		optionalRules.addColumn((ValueProvider<String, String>) value -> value).setHeader(optionalRuleColumnLabel).setResizable(true);
		
		add(buttonBox, layout, stateInfoLabel, editorLayout);
		setHorizontalComponentAlignment(Alignment.CENTER, stateInfoLabel);
	
		
		layout.setSizeFull();
	
		setHorizontalComponentAlignment(Alignment.CENTER, layout);
	
		
	
	   grid.setHeight("50vH");
	 
	   optionalRules.setHeight("50vH");
	   
	   
		
	}


	
	


	


	


	


	
	
	
	

}

