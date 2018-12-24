package edu.kndev.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EntityScan("edu.kndev.web.mdata")

//@ComponentScan({"edu.kndev.web.controller","edu.kndev.web.service"})
//@EnableJpaRepositories(basePackages= {"edu.kndev.web.repo"})
public class PatentParserApplication {
	public static void main(String[] args) {
		
		SpringApplication.run(PatentParserApplication.class, args);
		
	}
	
}
