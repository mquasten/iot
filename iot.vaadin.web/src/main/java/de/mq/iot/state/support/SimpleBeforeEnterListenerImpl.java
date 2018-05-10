package de.mq.iot.state.support;

import org.springframework.security.core.context.SecurityContext;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;

class SimpleBeforeEnterListenerImpl  implements BeforeEnterListener {

	
	private final SecurityContext securityContext;
	
	SimpleBeforeEnterListenerImpl(SecurityContext securityContext) {
		this.securityContext = securityContext;
	}

	@Override
	public void beforeEnter(final BeforeEnterEvent event) {
		System.out.println("Check security...");
		System.out.println(securityContext);
		
	}

}
