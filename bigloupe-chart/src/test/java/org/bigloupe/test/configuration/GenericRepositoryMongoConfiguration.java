package org.bigloupe.test.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ActiveProfiles;


/**
 * Test Generic Spring Data Repository
 * 
 * @author bigloupe
 *
 */
@Configuration
@ImportResource(value={"classpath:/applicationContext-mongodb.xml"})
@EnableMongoRepositories
@Profile("GenericRepositoryMongoChartDaoImplTest")
public class GenericRepositoryMongoConfiguration {

   
    
}
