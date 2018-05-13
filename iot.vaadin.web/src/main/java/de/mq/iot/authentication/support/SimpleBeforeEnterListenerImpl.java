package de.mq.iot.authentication.support;

import org.springframework.util.Assert;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;

import de.mq.iot.authentication.SecurityContext;

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
		
		if( securityContext.authentication().isPresent()) {
			return;
		}
		
		
		event.rerouteTo(LoginView.class);
	
		
		
	}

}
