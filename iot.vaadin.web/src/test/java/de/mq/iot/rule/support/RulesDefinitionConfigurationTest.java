package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

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
	

}
