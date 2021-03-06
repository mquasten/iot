package de.mq.iot.support;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.reactive.function.client.WebClient;

import com.mongodb.reactivestreams.client.MongoClients;



@Configuration
@EnableReactiveMongoRepositories(basePackages= {"de.mq.iot.resource.support","de.mq.iot.authentication.support", "de.mq.iot.calendar.support", "de.mq.iot.synonym.support", "de.mq.iot.rule.support" })
@ComponentScan(basePackages = "de.mq.iot.state.support,de.mq.iot.authentication,de.mq.iot.calendar,de.mq.iot.openweather.support,de.mq.iot.support,de.mq.iot.resource.support,de.mq.iot.synonym.support,de.mq.iot.rule.support")

@PropertySources({

@PropertySource(value="classpath:/iot-application.properties" ,ignoreResourceNotFound=true),
@PropertySource(value="file:${config.file}" ,ignoreResourceNotFound=true)

})


public class ApplicationConfiguration {
	
	
	@Bean
	@Lazy
	ReactiveMongoOperations reactiveMongoTemplate(@Value( "${mongo.url:mongodb://localhost:27017}" ) String mongoUrl, @Value( "${mongo.db:iot2}" ) String dbName ) {
	
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
