package edu.kndev.mongo.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EntityScan("edu.kndev.mongo.entity.PatentReport")
@EnableMongoRepositories(basePackages = "edu.kndev.mongo.repo", mongoTemplateRef = "PatentMongoTemplate")
public class PatentReportDbConfig {

}
