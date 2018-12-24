package edu.kndev.mongo.config;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClientURI;

@Component
public class MongoTemplateConfig {
	

	@Value("${patent.data.mongodb.uri}")
	String PatentdbUri;

	@Bean(autowire = Autowire.BY_NAME, name = "PatentMongoTemplate")
	public MongoTemplate PatentMongoTemplate() throws Exception {
		return new MongoTemplate(new SimpleMongoDbFactory(new MongoClientURI(PatentdbUri)));
	}
}
