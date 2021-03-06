package de.mq.iot.rule.support;


import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.Validator;

import de.mq.iot.rule.RulesDefinition;

class ValidationFactory {
	
	
	private final Map< RulesDefinition.Id,  Map<String, Validator>> validators = new HashMap<>();
	
	
	
	private final Map<Class<?>, Validator> mandatoryValidators = new HashMap<>();
	private final Map<Class<?>, Validator> optionalValidators = new HashMap<>();
	
	ValidationFactory(final ConversionService conversionService) {
		mandatoryValidators.put(Boolean.class, new BooleanValidatorImpl(conversionService, true));
		mandatoryValidators.put(LocalTime.class, new TimeValidatorImpl(conversionService, true));
		
		mandatoryValidators.put(Integer.class, new NaturalNumberValidatorImpl(conversionService, true, 0, Integer.MAX_VALUE));
		
		optionalValidators.put(Boolean.class, new BooleanValidatorImpl(conversionService, false));
		
		optionalValidators.put(LocalTime.class, new TimeValidatorImpl(conversionService, false));
		
		optionalValidators.put(Integer.class, new NaturalNumberValidatorImpl(conversionService, true, 0, Integer.MAX_VALUE));
		
		init(RulesDefinition.Id.DefaultDailyIotBatch, DefaultRuleInput.class);
		
		
		init(RulesDefinition.Id.EndOfDayBatch, EndOfDayRuleInput.class);
	}
	
	
	
	


	private void init(final RulesDefinition.Id id, final Class<?> type) {
		validators.put(id, new HashMap<>());
		final Map<String, Entry<Class<?>, Boolean>> fieldTypes =Arrays.asList(type.getDeclaredFields()).stream().collect(Collectors.toMap(Field::getName, field -> new AbstractMap.SimpleImmutableEntry<>(field.getType(), ! field.isAnnotationPresent(Nullable.class))));
		
		
		Stream.concat(id.input().stream(), id.parameter().stream()).forEach(key -> {
			
			
			
			Assert.isTrue(fieldTypes.containsKey(key), String.format("Field  %s not found in %s: ",  key, type));
			final Entry<Class<?>, Boolean> entry = fieldTypes.get(key);
			
			final Validator validator = entry.getValue() ? mandatoryValidators.get(entry.getKey()) : optionalValidators.get(entry.getKey());
			
			
			validators.get(id).put(key, validator);
		});
	}
	
	Validator validator(final RulesDefinition.Id id, final String key) {
		Assert.isTrue(validators.containsKey(id),String.format("Validaors for id %s not found. ", id));
		
		
		Assert.isTrue(validators.get(id).containsKey(key), String.format("Validaors for field %s not found. ", key));
		return validators.get(id).get(key);
		
	}
}
