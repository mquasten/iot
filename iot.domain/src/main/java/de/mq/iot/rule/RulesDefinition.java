package de.mq.iot.rule;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;




public interface RulesDefinition
 {
	
	
	static final String WORKINGDAY_ALARM_TIME_KEY =  "workingdayAlarmTime";
	static final String HOLIDAY_ALARM_TIME_KEY =  "holidayAlarmTime";
	static final String UPDATE_MODE_KEY = "updateMode";
	
	static final String TEST_MODE_KEY = "testMode";

	static final String MIN_EVENT_TIME_KEY="minEventTime";
	static final String MIN_SUN_DOWN_TIME_KEY=  "minSunDownTime";
	
	
	static final String MAX_IP_COUNT_KEY = "maxIpCount";
	static final String FIRST_IP_KEY = "firstIp";
	static final String DAYS_BACK_KEY = "daysBack";
	
	enum Id {
		DefaultDailyIotBatch(Arrays.asList(WORKINGDAY_ALARM_TIME_KEY, HOLIDAY_ALARM_TIME_KEY,MIN_SUN_DOWN_TIME_KEY), Arrays.asList(UPDATE_MODE_KEY, TEST_MODE_KEY, MIN_EVENT_TIME_KEY)),
		EndOfDayBatch(Arrays.asList(MAX_IP_COUNT_KEY, FIRST_IP_KEY, DAYS_BACK_KEY), Arrays.asList(UPDATE_MODE_KEY, TEST_MODE_KEY));
		
		
		private final Collection<String> input;
		private final Collection<String> parameter;
		private Id(Collection<String> input,Collection<String> parameter) {
			this.input=input;
			this.parameter=parameter;
		}
		public Collection<String> input() {
			return Collections.unmodifiableCollection(input);
	       	
		}
		
		public Collection<String> parameter() {
			
			return Collections.unmodifiableCollection(parameter);
	       	
		}
	}
	
	
	Id id();
	
	Map<String,String> inputData();
	
	Collection<String> optionalRules();

	void assign(String key, String value);

	String value(String key);

	void remove(String key);

	void assignRule(String rule);

	void removeOptionalRule(String rule);
	
}
