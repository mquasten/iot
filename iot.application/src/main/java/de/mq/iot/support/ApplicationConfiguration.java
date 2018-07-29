package de.mq.iot.support;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.reactive.function.client.WebClient;

import com.mongodb.reactivestreams.client.MongoClients;

@Configuration
@EnableReactiveMongoRepositories(basePackages= {"de.mq.iot.resource.support","de.mq.iot.authentication.support", "de.mq.iot.calendar.support"})
@ComponentScan(basePackages = "de.mq.iot.state,de.mq.iot.authentication,de.mq.iot.calendar,de.mq.iot.openweather.support,de.mq.iot.support, de.mq.iot.resource.support")
@PropertySource(value="classpath:/iot-application.properties" ,ignoreResourceNotFound=true)
public class ApplicationConfiguration {
	
	 
	@Bean
	@Lazy
	ReactiveMongoOperations reactiveMongoTemplate(@Value( "${mongo.url:mongodb://localhost:27017}" ) String mongoUrl, @Value( "${mongo.db:iot}" ) String dbName ) {
	
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
	
	@Bean
	ConversionService conversionService() {
		return new DefaultConversionService();
		
	}

}
