package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;

import de.mq.iot.model.Observer;

class AggrgationResultsDialogTest {
	
	private static final String I18N_RULES_AGGREGATION_RESULTS_EXCEPTIONS = "rules_aggregation_results_exceptions";

	private static final String I18N_RULES_AGGREGATION_RESULTS_RESULTS = "rules_aggregation_results_results";

	private static final String I18N_RULES_AGGREGATION_RESULTS_RULES = "rules_aggregation_results_rules";

	private static final String I18N_RULES_AGGREGATION_RESULTS_CLOSE = "rules_aggregation_results_close";

	private final Dialog dialog = Mockito.mock(Dialog.class);
	
	private SimpleAggrgationResultsDialog aggrgationResultsDialog;
	
	private Map<String, Object> fields = new HashMap<>();
	
	
	private final MessageSource messageSource = Mockito.mock(MessageSource.class);
	private final RuleDefinitionModel ruleDefinitionModel = Mockito.mock(RuleDefinitionModel.class);

	private Map<RuleDefinitionModel.Events, Observer> observers = new HashMap<>();
	
	@BeforeEach
	void setup() {
		
		Mockito.doAnswer(answer ->  {
			
			observers.put((RuleDefinitionModel.Events) answer.getArguments()[0], (Observer) answer.getArguments()[1] );
			return null;		
			}		
				).when(ruleDefinitionModel).register(Mockito.any(), Mockito.any());
			
		
		aggrgationResultsDialog = new SimpleAggrgationResultsDialog(ruleDefinitionModel, messageSource,dialog);
		
		
		fields.putAll(Arrays.asList(aggrgationResultsDialog.getClass().getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).collect(Collectors.toMap(Field::getName, field -> ReflectionTestUtils.getField(aggrgationResultsDialog, field.getName()))));
		
	}
	
	@Test
	void init() {
		final HorizontalLayout resultsLayout = resultsLayout();
		assertNotNull(resultsLayout);
		
		final HorizontalLayout exceptionsLayout = exceptionsLayout();
		assertNotNull(exceptionsLayout);
		
		assertFalse(resultsLayout.isVisible());
		assertFalse(exceptionsLayout.isVisible());
		
		Mockito.verify(ruleDefinitionModel).notifyObservers(RuleDefinitionModel.Events.ChangeLocale);
	}
	
	
	@Test
	void i18N() {
		
		//Mockito.when(mes)
		Mockito.doAnswer(a -> a.getArgument(0)).when(messageSource).getMessage(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		assertEquals(1, observers.size());
		assertEquals(Optional.of(RuleDefinitionModel.Events.ChangeLocale), observers.keySet().stream().findAny());
	
			
		observers.values().stream().findAny().get().process();
			
		
		final Button closeButton =  closeButton();
		assertNotNull(closeButton);
		assertEquals(I18N_RULES_AGGREGATION_RESULTS_CLOSE, closeButton.getText());
		
		final Label rulesHeader = rulesHeader();
		assertNotNull(rulesHeader);
		assertEquals(I18N_RULES_AGGREGATION_RESULTS_RULES, rulesHeader.getText());
		
		
		final Label resultsHeader = resultsHeader();
		assertNotNull(resultsHeader);
		assertEquals(I18N_RULES_AGGREGATION_RESULTS_RESULTS, resultsHeader.getText());
		
		final Label errors = errors();
		assertNotNull(errors);
		assertEquals(I18N_RULES_AGGREGATION_RESULTS_EXCEPTIONS, errors.getText());
		
		final TextArea exceptions= exceptions();
		assertNotNull(exceptions);
		assertEquals(I18N_RULES_AGGREGATION_RESULTS_EXCEPTIONS, exceptions.getLabel());
		
	}

	private TextArea exceptions() {
		return (TextArea) fields.get("exceptions");
	}

	private Label errors() {
		return (Label) fields.get("errors");
	}

	private Label resultsHeader() {
		final Label resultsHeader = (Label) fields.get("resultsHeader");
		return resultsHeader;
	}

	private Label rulesHeader() {
		return (Label) fields.get("rulesHeader");
	}

	private Button closeButton() {
		return  (Button) fields.get("closeButton");
	}

	private HorizontalLayout exceptionsLayout() {
		return  (HorizontalLayout) fields.get("exceptionsLayout");
	}

	private HorizontalLayout resultsLayout() {
		return (HorizontalLayout) fields.get("resultsLayout");
	}

}
