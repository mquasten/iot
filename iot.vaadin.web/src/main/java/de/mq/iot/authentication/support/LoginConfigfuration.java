package de.mq.iot.authentication.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vaadin.flow.spring.annotation.UIScope;

@Configuration
class LoginConfigfuration {
	
	@Bean
	@UIScope
	LoginModel loginModel() {
		return new LoginModelImpl();
	}

}
