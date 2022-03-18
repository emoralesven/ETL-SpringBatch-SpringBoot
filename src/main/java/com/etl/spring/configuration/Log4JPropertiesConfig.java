package com.etl.spring.configuration;



import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Accenture
 * @since bin8digitos
 * @version 1.0
 */
@Configuration
public class Log4JPropertiesConfig {
    @Value("${path.properties}")
    String pathProperties;

    @Bean("log4jConfig")
    public void log4jProperties() {
    	System.out.println("hola");
        String nombreArchivoPropertiesLog4j = "log4j_etl-spring.properties";
        String fullPathProperties = pathProperties + "/" + nombreArchivoPropertiesLog4j;
        System.out.println(fullPathProperties);
        File file = new File(fullPathProperties);
        if (file.exists()) {
            PropertyConfigurator.configure(fullPathProperties);
        } else {
        }
    }
}