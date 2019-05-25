package de.mq.iot.rule.support;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.Validator;

import de.mq.iot.rule.RulesDefinition;


@Rule(name = "inputDataMappingRule", priority = 0)
public class InputDataMappingRuleImpl {
	
	
	private final ConversionService conversionService; 
	
	private final ValidationFactory validationFactory;
	

	InputDataMappingRuleImpl(final ConversionService conversionService, final ValidationFactory validationFactory){
		this.conversionService=conversionService;
		this.validationFactory=validationFactory;
	}


	
		
		
		
	
	 @Condition
	 public boolean evaluate(@Fact(RulesAggregate.RULE_ENGINE_ID) final RulesDefinition.Id id, @Fact(RulesAggregate.RULE_INPUT_MAP_FACT) final Map<String,String> ruleInputMap) {
		
		 final Collection<Entry<String,String>> fields = usedFields(id, ruleInputMap.entrySet()).entrySet();
		 final Errors errors = new MapBindingResult(new HashMap<>(), RulesAggregate.RULE_INPUT_MAP_FACT);
		 fields.forEach(field -> {
			
			errors.setNestedPath(field.getKey());
			final Validator validator = validationFactory.validator(id, field.getKey()) ;
			validator.validate(field.getValue(), errors);
		 });
		 
		 return !errors.hasErrors();
		
		
	 }




	
	 
	 @Action
	 public void mapping(@Fact(RulesAggregate.RULE_ENGINE_ID) final RulesDefinition.Id id, @Fact(RulesAggregate.RULE_INPUT_MAP_FACT) final Map<String,String> ruleInputMap, @Fact(RulesAggregate.RULE_INPUT) final Object ruleInput) {
		final  Collection<Entry<String,String>> fields = usedFields(id, ruleInputMap.entrySet()).entrySet();
		
		 fields.stream().forEach(entry -> {
			  final Field field = ReflectionUtils.findField(ruleInput.getClass(), entry.getKey());
			  field.setAccessible(true);
			  ReflectionUtils.setField(field, ruleInput,  conversionService.convert(entry.getValue(), field.getType()) );
		 }); 
     }
	 
	 private Map<String,String>usedFields(final RulesDefinition.Id id, final Collection<Entry<String,String>> input ) {
		 final Collection<String> fields = Stream.concat(id.input().stream(), id.parameter().stream()).collect(Collectors.toSet());
		 return input.stream().filter(entry -> StringUtils.hasText(entry.getValue())).filter(entry -> fields.contains(entry.getKey())).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		
	 }

}
