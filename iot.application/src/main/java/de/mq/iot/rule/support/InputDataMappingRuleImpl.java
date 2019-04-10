package de.mq.iot.rule.support;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.Validator;

import de.mq.iot.rule.RulesDefinition;


@Rule(name = "inputDataMappingRule", priority = 0)
public class InputDataMappingRuleImpl {
	
	
	private final ConversionService conversionService; 
	
	
	

	InputDataMappingRuleImpl(final ConversionService conversionService){
		this.conversionService=conversionService;
	}


	
		
		
		
	
	 @Condition
	 public boolean evaluate(@Fact(RulesAggregate.RULE_INPUT_MAP_FACT) final Map<String,String> ruleInputMap) {
		 final Map<?,?> map = new HashMap<>();
		 final Errors errors = new MapBindingResult(map, "ruleInputData");
		 
		 final Validator timeValidator = new TimeValidatorImpl(conversionService, true);
		 
		 errors.setNestedPath(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY);
		 timeValidator.validate(ruleInputMap.get(RulesDefinition.WORKINGDAY_ALARM_TIME_KEY), errors);
		 errors.setNestedPath(RulesDefinition.HOLIDAY_ALARM_TIME_KEY);
		 timeValidator.validate(ruleInputMap.get(RulesDefinition.HOLIDAY_ALARM_TIME_KEY), errors);
		 
		 final Validator booleanValidator = new BooleanValidatorImpl(conversionService, false);
		 errors.setNestedPath(RulesDefinition.UPDATE_MODE_KEY);
		 
		 booleanValidator.validate(ruleInputMap.get(RulesDefinition.UPDATE_MODE_KEY), errors);
		 
		 errors.setNestedPath(RulesDefinition.TEST_MODE_KEY);
		 
		 booleanValidator.validate(ruleInputMap.get(RulesDefinition.TEST_MODE_KEY), errors);
		 return !errors.hasErrors();
		
		
	 }




	
	 
	 @Action
	 public void mapping(@Fact(RulesAggregate.RULE_INPUT_MAP_FACT) final Map<String,String> ruleInputMap, @Fact("ruleInput") DefaultRuleInput ruleInput) {
		
		
		 ruleInputMap.entrySet().stream().forEach(entry -> {
			  final Field field = ReflectionUtils.findField(DefaultRuleInput.class, entry.getKey());
			  field.setAccessible(true);
			  
			  ReflectionUtils.setField(field, ruleInput,  conversionService.convert(entry.getValue(), field.getType()) );
			 
		 }); 
		 System.out.println("*inputDataMappingRule*");
     }

}
