package de.mq.iot.rule.support;

import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.Action;

import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.Validator;


@Rule(name = "inputDataMappingRule", priority = 0)
class InputDataMappingRuleImpl {
	
	static final String WORKINGDAY_ALARM_TIME_KEY =  "workingdayAlarmTime";
	static final String HOLIDAY_ALARM_TIME_KEY =  "holidayAlarmTime";
	static final String UPDATE_MODE_KEY = "updateMode";
	static final String TEST_MODE_KEY = "testMode";
	
	
	
	private final DefaultConversionService conversionService = new DefaultConversionService(); 
	
	
	InputDataMappingRuleImpl() {
		conversionService.addConverter(String.class, LocalTime.class, stringValue -> {
			String[] values = splitTimeString(stringValue);
			if( values.length!=2) {
				return null;
			}
			return LocalTime.of(conversionService.convert(values[0], Integer.class), conversionService.convert(values[1], Integer.class));
		});
		
		
		
		
	}




	private String[] splitTimeString(final String stringValue) {
		return stringValue.split(TimeValidatorImpl.DELIMITER);
	}
		
		
		
	
	 @Condition
	 public boolean evaluate(@Fact("ruleInputMap") final Map<String,String> ruleInputMap) {
		 final Map<?,?> map = new HashMap<>();
		 final Errors errors = new MapBindingResult(map, "ruleInputData");
		 
		 final Validator timeValidator = new TimeValidatorImpl(true);
		 
		 errors.setNestedPath(WORKINGDAY_ALARM_TIME_KEY);
		 timeValidator.validate(ruleInputMap.get(WORKINGDAY_ALARM_TIME_KEY), errors);
		 errors.setNestedPath(HOLIDAY_ALARM_TIME_KEY);
		 timeValidator.validate(ruleInputMap.get(HOLIDAY_ALARM_TIME_KEY), errors);
		 
		 final Validator booleanValidator = new BooleanValidatorImpl(false);
		 errors.setNestedPath(UPDATE_MODE_KEY);
		 
		 booleanValidator.validate(ruleInputMap.get(UPDATE_MODE_KEY), errors);
		 
		 errors.setNestedPath(TEST_MODE_KEY);
		 
		 booleanValidator.validate(ruleInputMap.get(TEST_MODE_KEY), errors);
		 return !errors.hasErrors();
		
		
	 }




	
	 
	 @Action
	 public void mapping(@Fact("ruleInputMap") final Map<String,String> ruleInputMap, @Fact("ruleInput") DefaultRuleInput ruleInput) {
		
		
		 ruleInputMap.entrySet().stream().forEach(entry -> {
			  final Field field = ReflectionUtils.findField(DefaultRuleInput.class, entry.getKey());
			  field.setAccessible(true);
			  
			  ReflectionUtils.setField(field, ruleInput,  conversionService.convert(entry.getValue(), field.getType()) );
			 
		 });
		 
		 
     }

}
