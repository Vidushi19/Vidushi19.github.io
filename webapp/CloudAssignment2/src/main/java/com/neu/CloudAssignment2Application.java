package com.neu;


import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.neu.controller","com.neu.repository","com.neu.service","com.neu.config"})
public class CloudAssignment2Application extends SpringBootServletInitializer{
	
	 @Override
	    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	        return application.sources(CloudAssignment2Application.class);
	    }

    
	public static void main(String[] args) {
		SpringApplication.run(CloudAssignment2Application.class, args);
	}
	
	@PostConstruct
    public void init()
    {
            Logger log = LoggerFactory.getLogger(CloudAssignment2Application.class);

            try
            {
                    log.info("Started CloudAssignment2Application successfully!");

                   // throw new NullPointerException("Oh no!");
            }
            catch (Exception e)
            {
                    log.error("Could not start CloudAssignment2Application", e);
            }
    }

}
