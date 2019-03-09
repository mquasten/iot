package de.mq.iot.rule.support;

import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.Map;

import javax.xml.ws.Action;

import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.ReflectionUtils;


@Rule(name = "inputDataMappingRule", priority = 0)
class InputDataMappingRuleImpl {
	
	static final String WORKINGDAY_ALARM_TIME_KEY =  "workingdayAlarmTime";
	static final String HOLIDAY_ALARM_TIME_KEY =  "holidayAlarmTime";
	
	
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
		return stringValue.split("[:.| ,\t]");
	}
		
		
		
	
	 @Condition
	 public boolean evaluate(@Fact("ruleInputMap") final Map<String,String> ruleInputMap) {
		 
		if( !ruleInputMap.containsKey(WORKINGDAY_ALARM_TIME_KEY) ) {
			return false;
		}
		
		if( !ruleInputMap.containsKey(HOLIDAY_ALARM_TIME_KEY) ) {
			return false;
		}
		
		return validTimeString(splitTimeString(ruleInputMap.get(WORKINGDAY_ALARM_TIME_KEY))) && validTimeString(splitTimeString(ruleInputMap.get(HOLIDAY_ALARM_TIME_KEY)));
		
		
	 }




	protected boolean validTimeString(final String[] values) {
		if( values.length != 2) {
			return false;
		}
		
		return validInt(values[0], 24) && validInt(values[1], 60);
	}




	private boolean validInt(String value, final int limit ) {
		try {
		int intValue = 	conversionService.convert(value, int.class);
		return  intValue >= 0 && intValue < limit;
		} catch (IllegalArgumentException ia) {
			return false;
		}
	}
	 
	 @Action
	 public void mapping(@Fact("ruleInputMap") final Map<String,String> ruleInputMap, @Fact("ruleInput") DefaultRuleInput ruleInput) {
		
		
		 ruleInputMap.entrySet().stream().forEach(entry -> {
			  final Field field = ReflectionUtils.findField(DefaultRuleInput.class, entry.getKey());
			  field.setAccessible(true);
			 
			  ReflectionUtils.setField(field, ruleInput, conversionService.convert(entry.getValue(), field.getType()));
			 
		 });
		 
		 
     }

}
