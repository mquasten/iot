package de.mq.iot.state;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.xml.sax.SAXException;

@ExtendWith(SpringExtension.class)

@ContextConfiguration(classes = { TestConfiguration.class })
class StateRepositoryTest {

	@Autowired
	private HomematicXmlApiStateRepositoryImpl stateRepository;

	@Test
	@Disabled
	void findStates() throws ParserConfigurationException, SAXException, IOException {
		System.out.println(stateRepository);
		stateRepository.findStates();

	}

}

@Configuration
@ComponentScan(basePackages = "de.mq.iot.state")
class TestConfiguration {
	@Bean
	@Scope(scopeName = "prototype")
	WebClient.Builder webClientBuilder() {
		return WebClient.builder();
	}

}