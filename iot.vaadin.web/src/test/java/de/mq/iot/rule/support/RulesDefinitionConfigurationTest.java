package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.flow.component.dialog.Dialog;

import de.mq.iot.model.Subject;

class RulesDefinitionConfigurationTest {
	
	private final RulesDefinitionConfiguration rulesDefinitionConfiguration = new RulesDefinitionConfiguration();
	
	@SuppressWarnings("unchecked")
	private final Subject<RuleDefinitionModel.Events, RuleDefinitionModel> subject = Mockito.mock(Subject.class);
	
	private final ValidationFactory validationFactory = Mockito.mock(ValidationFactory.class);
	
	@Test
	void ruleDefinitionModel() {
		
		final RuleDefinitionModel model = rulesDefinitionConfiguration.ruleDefinitionModel(subject, validationFactory);
		final Map<Class<?>, Object> dependencies = Arrays.asList(RuleDefinitionModelImpl.class.getDeclaredFields()).stream().filter(field -> Arrays.asList(ValidationFactory.class, Subject.class).contains(field.getType()) ).map(field -> new AbstractMap.SimpleImmutableEntry<>(field.getType(), ReflectionTestUtils.getField(model, field.getName()))).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		assertEquals(2, dependencies.size());
	    assertEquals(validationFactory, dependencies.get(ValidationFactory.class));
	    assertEquals(subject, dependencies.get(Subject.class));
	}
	@Test
	void simpleAggrgationResultsDialog() {
		final RuleDefinitionModel ruleDefinitionModel = Mockito.mock(RuleDefinitionModel.class);
		final MessageSource messageSource = Mockito.mock(MessageSource.class);
		final  Dialog dialog = Mockito.mock(Dialog.class);
		final Object simpleAggrgationResultsDialog = rulesDefinitionConfiguration.simpleAggrgationResultsDialog(ruleDefinitionModel, messageSource, dialog);
		final Map<Class<?>, Object>  dependencies = Arrays.asList(SimpleAggrgationResultsDialog.class.getDeclaredFields()).stream().filter(field -> Arrays.asList(Dialog.class, RuleDefinitionModel.class, MessageSource.class).contains(field.getType())).collect(Collectors.toMap(Field::getType, field -> ReflectionTestUtils.getField(simpleAggrgationResultsDialog,field.getName())));
	
		
		
	   assertEquals(3, dependencies.size());
	   assertEquals(dialog, dependencies.get(Dialog.class));
	   assertEquals(messageSource, dependencies.get(MessageSource.class));
	   assertEquals(ruleDefinitionModel, dependencies.get(RuleDefinitionModel.class));
	}
	

}
