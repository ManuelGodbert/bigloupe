package org.bigloupe.web.service;

import org.bigloupe.web.BigLoupeConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.mock.web.MockServletContext;


@Configuration
@ImportResource(value={"classpath:/applicationContext-test.xml", "classpath:/applicationContext-database.xml", "classpath:/applicationContext.xml"})
public class SpringServletConfiguration {

	
    @Bean
    public BigLoupeConfiguration webBigLoupeConfiguration() {
    	BigLoupeConfiguration configuration = new BigLoupeConfiguration();
        return configuration;
    }
    

    
    
}
