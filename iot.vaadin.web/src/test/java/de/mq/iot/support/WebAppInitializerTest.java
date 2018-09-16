package de.mq.iot.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class WebAppInitializerTest {
	
	private final WebAppInitializer webAppInitializer = new WebAppInitializer();
	
	@Test
	void getConfigurationClasses() {
		final Collection<Class<?>> classes = webAppInitializer.getConfigurationClasses();
		
		assertEquals(1, classes.size());
		assertEquals(Optional.of(ApplicationConfiguration.class), classes.stream().findFirst());
		
	}

}
