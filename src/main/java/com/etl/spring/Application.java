package com.etl.spring;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;





//@PropertySource(("classpath:log4j_etl-spring.properties"))
@PropertySource(("classpath:application.properties"))
@SpringBootApplication
public class Application {
	private static final Logger log = Logger.getLogger(Application.class);
	public static void main(String[] args) {

		 
		SpringApplication.run(Application.class, args);
		log.info("Iniciando ETL Spring - prueba");
	}

}
