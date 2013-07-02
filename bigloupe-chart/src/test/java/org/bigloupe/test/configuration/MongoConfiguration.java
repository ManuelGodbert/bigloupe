package org.bigloupe.test.configuration;

import org.bigloupe.web.chart.dao.mongo.MongoChartDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;


@Configuration
@ImportResource(value={"classpath:/applicationContext-mongodb.xml"})
@Profile("MongoChartDaoImplTest")
public class MongoConfiguration {

	@Autowired
	MongoTemplate mongoTemplate;
	
    @Bean
    public MongoChartDaoImpl getChartDao() {
    	MongoChartDaoImpl mongoChartDaoImpl = new MongoChartDaoImpl();
    	mongoChartDaoImpl.setMongoTemplate(mongoTemplate);
        return mongoChartDaoImpl;
    }
    

    
    
}
