package de.mq.iot.rule.support;


import java.util.Map.Entry;

import org.springframework.context.MessageSource;

import com.vaadin.flow.component.button.Button;
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
import de.mq.iot.rule.support.RuleDefinitionModel.Events;
import de.mq.iot.support.ButtonBox;

@Route("rules")
@Theme(Lumo.class)
@I18NKey("rules_")
class  RulesDefinitionView extends VerticalLayout implements LocalizeView {
	
	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@I18NKey("id_column")
	private final Label ruleDefinitionColumnLabel = new Label();
	
	@I18NKey("optional_rules_column")
	private final Label optionalRuleColumnLabel = new Label();
	
	
	@I18NKey("value_column")
	private final Label valueColumnLabel = new Label();

	private final Grid<RulesDefinition> grid = new Grid<>();
	
	
	private final Grid<String> optionalRules = new Grid<>();
	
	private final Grid<Entry<String,String>> inputParameter = new Grid<>();
	
	
	private final Grid<Entry<String,String>> arguments = new Grid<>();
	
//https://vaadin.com/components/vaadin-grid/java-examples/grid-editor

	
	RulesDefinitionView( final RuleDefinitionModel ruleDefinitionModel, final RulesService rulesService, final ButtonBox buttonBox, final MessageSource messageSource ) {
	
	
		createUI(rulesService, buttonBox);	
		grid.asSingleSelect().addValueChangeListener(selectionEvent -> ruleDefinitionModel.assignSelected(selectionEvent.getValue()));
		
		ruleDefinitionModel.register(Events.AssignRuleDefinition, () ->{
			
			System.out.println("selectionChanged:"  + ruleDefinitionModel.selectedRuleDefinition());
			
			ruleDefinitionModel.selectedRuleDefinition().ifPresent(rd -> {
			optionalRules.setItems(rd.optionalRules());
			inputParameter.setItems(rd.inputData().entrySet());
			arguments.setItems(rd.inputData().entrySet());
			});
			
			
		});
		
		grid.setItems(rulesService.rulesDefinitions());
		
		
		
		ruleDefinitionModel.register(RuleDefinitionModel.Events.ChangeLocale, () -> {
			
			 localize(messageSource, ruleDefinitionModel.locale());
		
			
		});
		
		
		
		
		ruleDefinitionModel.notifyObservers(RuleDefinitionModel.Events.ChangeLocale);
		
		
	}


	

	
	


	

	


	

	



	private void createUI(final RulesService ruleService, final ButtonBox buttonBox) {
				
	
		
		
	
		
		
		final HorizontalLayout layout = new HorizontalLayout(grid, arguments);
		grid.getElement().getStyle().set("overflow", "auto");
		optionalRules.getElement().getStyle().set("overflow", "auto");
		inputParameter.getElement().getStyle().set("overflow", "auto");
	
		
		
		arguments.getElement().getStyle().set("overflow", "auto");
		
		
		grid.addColumn((ValueProvider<RulesDefinition, String>) rulesDefinition -> rulesDefinition.id().name()).setHeader(ruleDefinitionColumnLabel).setResizable(true);
		grid.setSelectionMode(SelectionMode.SINGLE);
		
		
		arguments.addColumn((ValueProvider<Entry<String,String>, String>) entry -> entry.getKey()).setHeader("Parameter").setResizable(true);
		arguments.addColumn((ValueProvider<Entry<String,String>, String>) entry -> entry.getValue()).setHeader("Wert").setResizable(true);
		
		
		
		
		
		inputParameter.addColumn((ValueProvider<Entry<String,String>, String>) entry -> entry.getKey()).setHeader("Parameter").setFooter(new Button("ändern")).setResizable(true);
		inputParameter.addColumn((ValueProvider<Entry<String,String>, String>) entry -> entry.getValue()).setHeader("Wert").setFooter(new TextField()).setResizable(true);
		
		
		
	
		
		final HorizontalLayout persistentParameterLayout = new HorizontalLayout(inputParameter, optionalRules);
		persistentParameterLayout.setSizeFull();
		
		//inputParameterLayout.setSizeFull();
		
		final HorizontalLayout editorLayout = new HorizontalLayout(persistentParameterLayout);

		
		
		editorLayout.setVerticalComponentAlignment(Alignment.CENTER, persistentParameterLayout);
		
		persistentParameterLayout.setVerticalComponentAlignment(Alignment.CENTER, inputParameter);

		editorLayout.setSizeFull();
		
	  
	
		
		
		optionalRules.addColumn((ValueProvider<String, String>) value -> value).setHeader(optionalRuleColumnLabel).setResizable(true);
		
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

