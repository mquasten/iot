package de.mq.iot.support;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.reactive.function.client.WebClient;

import com.mongodb.reactivestreams.client.MongoClients;

@Configurable
@EnableReactiveMongoRepositories(basePackages= {"de.mq.iot.resource.support"})
@ComponentScan(basePackages = "de.mq.iot.state")
@PropertySource(value="classpath:/iot-application.properties", ignoreResourceNotFound=true)
public class ApplicationConfiguration {
	
	private @Value( "${mongo.url:mongodb://localhost:27017}" ) String mongoUrl; 
	
	private @Value( "${mongo.db:iot}" ) String dbName; 
	@Bean
	ReactiveMongoOperations reactiveMongoTemplate() {
		 return new ReactiveMongoTemplate( MongoClients.create(mongoUrl), dbName);
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
