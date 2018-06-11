package de.mq.iot.state.support;



import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

public class SimpleServiceCommandlineRunnerImpl {
	
	Map<String, Collection<MainParameter>> mainDefinitions(final Class<?> mainDefinitionClass) {
	
		Assert.isTrue(mainDefinitionClass.isAnnotationPresent(Mains.class), "Mains annotation expected.");
		
		final List<Main> mainList = Arrays.asList(mainDefinitionClass.getAnnotation(Mains.class).value());
		Assert.isTrue(mainList.size() > 0 , "At least one Main annotation should be in list" );
		final Map<String, Collection<MainParameter>> mainsMap = new HashMap<>(); 
		mainList.forEach(main -> {
			mainsMap.put(main.name(), Arrays.asList(main.parameters()));
			
		});
		return mainsMap;
		
	}

}
