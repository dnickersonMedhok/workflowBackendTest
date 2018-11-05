package com.medhok.workflowServer.main;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages= {"com.medhok.workflowServer"})
public class WorkflowServerMain {
	
	private static Logger logger = LoggerFactory.getLogger(WorkflowServerMain.class);

	
	public static void main( String[] args )
	{
		SpringApplication.run(WorkflowServerMain.class, args);

	}
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

        	logger.debug("****** starting");

        };
    }

}
