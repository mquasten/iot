package de.mq.iot.support;

import java.util.Collection;
import java.util.Collections;

import com.vaadin.flow.spring.VaadinMVCWebAppInitializer;

class WebAppInitializer extends VaadinMVCWebAppInitializer {

    @Override
    protected Collection<Class<?>> getConfigurationClasses() {
        return Collections.singletonList(ApplicationConfiguration.class);
    }
    
    
   
   
}
	
	
	
	


