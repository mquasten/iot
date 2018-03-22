package de.mq.iot.support;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.reactive.function.client.WebClient;

import com.mongodb.reactivestreams.client.MongoClients;

@Configurable
@EnableReactiveMongoRepositories(basePackages= {"de.mq.iot.resource.support"})
@ComponentScan(basePackages = "de.mq.iot.state")
public class ApplicationConfiguration {
	@Bean
	ReactiveMongoOperations reactiveMongoTemplate() {
		 return new ReactiveMongoTemplate( MongoClients.create(String.format("mongodb://localhost:%d", 27017)), "iot");
	}
	
	@Bean
	@Scope(scopeName = "prototype")
	WebClient.Builder webClientBuilder() {
		return WebClient.builder();
	}

	@Bean
	@Scope(scopeName = "prototype")
	XPath xpath() {
		return XPathFactory.newInstance().newXPath();
	}

}
