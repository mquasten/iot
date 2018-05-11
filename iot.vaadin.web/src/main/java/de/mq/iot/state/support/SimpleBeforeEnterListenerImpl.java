package de.mq.iot.state.support;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.util.Assert;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;

class SimpleBeforeEnterListenerImpl  implements BeforeEnterListener {

	
	private final SecurityContext securityContext;
	
	SimpleBeforeEnterListenerImpl(final SecurityContext securityContext) {
		Assert.notNull(securityContext , "SecurityContext is required.");
		this.securityContext = securityContext;
		
	}

	@Override
	public void beforeEnter(final BeforeEnterEvent event) {
		
		if( event.getNavigationTarget() == LoginView.class) {
			return;
		}
		
		if( securityContext.getAuthentication() == null) {
			event.rerouteTo(LoginView.class);
			return;
		}
		
		if( ! securityContext.getAuthentication().isAuthenticated() ) {
			event.rerouteTo(LoginView.class);
		}
		
		
	}

}
