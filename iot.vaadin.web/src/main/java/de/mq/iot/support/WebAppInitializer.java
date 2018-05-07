package de.mq.iot.support;

import java.util.Collection;
import java.util.Collections;


import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.vaadin.flow.spring.VaadinMVCWebAppInitializer;

import de.mq.iot.support.ApplicationConfiguration;

class WebAppInitializer extends VaadinMVCWebAppInitializer {

    @Override
    protected Collection<Class<?>> getConfigurationClasses() {
        return Collections.singletonList(ApplicationConfiguration.class);
    }
    
    
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
    	
    	super.onStartup(servletContext);
    	
    	servletContext.addFilter("loginFilter", DelegatingFilterProxy.class)
        .addMappingForUrlPatterns(null, false, "/*");
    }
   
}
	
	
	
	


