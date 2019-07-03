package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.Query;

import de.mq.iot.model.Observer;
import de.mq.iot.rule.RulesDefinition;
import de.mq.iot.support.ButtonBox;

class RulesDefinitionViewTest {

	private final RuleDefinitionModel ruleDefinitionModel = Mockito.mock(RuleDefinitionModel.class);

	private final RulesService rulesService = Mockito.mock(RulesService.class);

	private final MessageSource messageSource = Mockito.mock(MessageSource.class);

	private RulesDefinitionView rulesDefinitionView;

	private final Map<RuleDefinitionModel.Events, Observer> observers = new HashMap<>();

	private RulesDefinition rulesDefinitionDefaultDailyIotBatch = Mockito.mock(RulesDefinition.class);
	private RulesDefinition rulesDefinitionEndOfDayBatch = Mockito.mock(RulesDefinition.class);

	private Map<String, Object> fields = new HashMap<>();

	@BeforeEach
	void setup() {
		
		
		Mockito.doReturn(Arrays.asList(RulesDefinition.TEMPERATURE_RULE_NAME)).when(ruleDefinitionModel).optionalRules();
		
		Mockito.doReturn(Arrays.asList(new AbstractMap.SimpleImmutableEntry<>(RulesDefinition.HOLIDAY_ALARM_TIME_KEY, "7:15"), new AbstractMap.SimpleImmutableEntry<>(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY, "5:15"))).when(ruleDefinitionModel).input();
		Mockito.doReturn(Arrays.asList(new AbstractMap.SimpleImmutableEntry<>(RulesDefinition.TEST_MODE_KEY, null), new AbstractMap.SimpleImmutableEntry<>(RulesDefinition.UPDATE_MODE_KEY, null))).when(ruleDefinitionModel).parameter();

		Mockito.doReturn(RulesDefinition.Id.DefaultDailyIotBatch).when(rulesDefinitionDefaultDailyIotBatch).id();

		Mockito.doReturn(RulesDefinition.Id.EndOfDayBatch).when(rulesDefinitionEndOfDayBatch).id();

		Mockito.doReturn(Arrays.asList(rulesDefinitionDefaultDailyIotBatch, rulesDefinitionEndOfDayBatch)).when(rulesService).rulesDefinitions();
		
		
		

		Mockito.doAnswer(answer -> {

			final RuleDefinitionModel.Events event = (RuleDefinitionModel.Events) answer.getArguments()[0];
			final Observer observer = (Observer) answer.getArguments()[1];
			observers.put(event, observer);
			return null;

		}).when(ruleDefinitionModel).register(Mockito.any(), Mockito.any());

		rulesDefinitionView = new RulesDefinitionView(ruleDefinitionModel, rulesService, new ButtonBox(), messageSource);

		fields.putAll(Arrays.asList(rulesDefinitionView.getClass().getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).collect(Collectors.toMap(Field::getName, field -> ReflectionTestUtils.getField(rulesDefinitionView, field.getName()))));
	}

	@Test
	void init() {
		assertEquals(RuleDefinitionModel.Events.values().length, observers.size());

		Arrays.asList(RuleDefinitionModel.Events.values()).forEach(key -> observers.containsKey(key));

		
		final Grid<RulesDefinition> grid = grid();
		assertNotNull(grid);

		final Collection<RulesDefinition> rulesDefinitions = (Collection<RulesDefinition>) grid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList());

		assertEquals(2, rulesDefinitions.size());

		final Button button = saveButton();

		assertNotNull(button);

		assertFalse(button.isEnabled());

		
		final Grid<String> optionalRules = optionalRules();
		assertNotNull(optionalRules);

		assertFalse(optionalRules.getParent().isPresent());

		
		final Grid<Entry<String, String>> inputParameter = inputParameter();

		assertNotNull(inputParameter);
		assertFalse(inputParameter.getParent().isPresent());

		
		final Grid<Entry<String, String>> arguments = arguments();

		assertNotNull(arguments);
		assertFalse(arguments.getParent().isPresent());
		
		
		Mockito.verify(ruleDefinitionModel).notifyObservers(RuleDefinitionModel.Events.ChangeLocale);
	}

	private Grid<Entry<String, String>> arguments() {
		@SuppressWarnings("unchecked")
		final Grid<Entry<String, String>> arguments = (Grid<Entry<String, String>>) fields.get("arguments");
		return arguments;
	}

	private Grid<Entry<String, String>> inputParameter() {
		@SuppressWarnings("unchecked")
		final Grid<Entry<String, String>> inputParameter = (Grid<Entry<String, String>>) fields.get("inputParameter");
		return inputParameter;
	}

	private Grid<String> optionalRules() {
		@SuppressWarnings("unchecked")
		final Grid<String> optionalRules = (Grid<String>) fields.get("optionalRules");
		return optionalRules;
	}

	private Grid<RulesDefinition> grid() {
		@SuppressWarnings("unchecked")
		final Grid<RulesDefinition> grid = (Grid<RulesDefinition>) fields.get("grid");
		return grid;
	}

	private Button saveButton() {
		final Button button = (Button) fields.get("saveButton");
		return button;
	}
	
	
	@Test
	void selectRulesDefinition() {
		
	
		final Grid<RulesDefinition> grid =grid();
		assertNotNull(grid);
		grid.select(rulesDefinitionDefaultDailyIotBatch);
		
		
		Mockito.verify(ruleDefinitionModel).assignSelected(rulesDefinitionDefaultDailyIotBatch);
	}
	
	
	@Test
	void assignRuleDefinition() {
		Mockito.doReturn(Arrays.asList(RulesDefinition.TEMPERATURE_RULE_NAME)).when(ruleDefinitionModel).definedOptionalRules();
		Mockito.doReturn(true).when(ruleDefinitionModel).isSelected();
		assertTrue(observers.containsKey(RuleDefinitionModel.Events.AssignRuleDefinition));
		
		final Button button = saveButton();
		assertNotNull(button);
		assertFalse(button.isEnabled());
		
		final Grid<String> optionalRules = optionalRules();
		assertNotNull(optionalRules);
		assertFalse(optionalRules.getParent().isPresent());
		
		final Grid<Entry<String, String>> inputParameter = inputParameter();
		assertNotNull(inputParameter);
		assertFalse(inputParameter.getParent().isPresent());
		
		final Grid<Entry<String, String>> arguments = arguments();
		assertNotNull(arguments);
		assertFalse(arguments.getParent().isPresent());
		
		@SuppressWarnings("unchecked")
		final ComboBox<String> optionalRulesComboBox = (ComboBox<String>) fields.get("optionalRulesComboBox");
		assertNotNull(optionalRulesComboBox);
		assertEquals(0, optionalRulesComboBox.getDataProvider().fetch(new Query<>()).collect(Collectors.toList()).size());
		
		observers.get(RuleDefinitionModel.Events.AssignRuleDefinition).process();
		
		assertEquals(1, optionalRulesComboBox.getDataProvider().fetch(new Query<>()).collect(Collectors.toList()).size());
		assertEquals(Optional.of(RulesDefinition.TEMPERATURE_RULE_NAME), optionalRulesComboBox.getDataProvider().fetch(new Query<>()).collect(Collectors.toList()).stream().findAny());
		
		assertTrue(button.isEnabled());
		assertTrue(optionalRules.getParent().isPresent());
		assertTrue(inputParameter.getParent().isPresent());
		assertTrue(arguments.getParent().isPresent());

		assertEquals(Optional.of(RulesDefinition.TEMPERATURE_RULE_NAME), optionalRules.getDataProvider().fetch(new Query<>()).collect(Collectors.toList()).stream().findAny());

		final Collection<String> resultsInputParameter = inputParameter.getDataProvider().fetch(new Query<>()).collect(Collectors.toList()).stream().map(Entry::getKey).collect(Collectors.toList());
		assertEquals(2, resultsInputParameter.size());
		assertTrue(resultsInputParameter.contains(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY));
		assertTrue(resultsInputParameter.contains(RulesDefinition.HOLIDAY_ALARM_TIME_KEY));
		
		final Collection<String> resultsArguments = arguments.getDataProvider().fetch(new Query<>()).collect(Collectors.toList()).stream().map(Entry::getKey).collect(Collectors.toList());
		assertEquals(2, resultsArguments.size());
		assertTrue(resultsArguments.contains(RulesDefinition.UPDATE_MODE_KEY));
		assertTrue(resultsArguments.contains(RulesDefinition.TEST_MODE_KEY));
		
		Mockito.doReturn(false).when(ruleDefinitionModel).isSelected();
		observers.get(RuleDefinitionModel.Events.AssignRuleDefinition).process();
		
		assertFalse(button.isEnabled());
		assertFalse(optionalRules.getParent().isPresent());
		assertFalse(inputParameter.getParent().isPresent());
		assertFalse(arguments.getParent().isPresent());
		
	}

}
