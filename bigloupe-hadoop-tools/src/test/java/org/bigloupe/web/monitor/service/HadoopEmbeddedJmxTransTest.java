package org.bigloupe.web.monitor.service;

import static org.junit.Assert.*;

import org.bigloupe.web.BigLoupeConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext.xml" })
@ActiveProfiles("jmx")
public class HadoopEmbeddedJmxTransTest implements ApplicationContextAware {

	HadoopEmbeddedJmxTrans jmxTrans;
	
	ApplicationContext applicationContext;
	
	@Before
	public void setUp() throws Exception {
		BigLoupeConfiguration.setBaseDir("../bigloupe-war/src/main/webapp");
		jmxTrans = applicationContext.getBean(HadoopEmbeddedJmxTrans.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testJMXTransConfigurationBuilder() throws Exception {
		jmxTrans.loadConfiguration();  
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
		
	}
	
	/**
	 * Used only for tests
	 * @param args
	 */
	public static void main(String[] args) {
		
		ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/applicationContext.xml");
		BigLoupeConfiguration.setBaseDir("../bigloupe-war/src/main/webapp");
		HadoopEmbeddedJmxTrans jmxTrans = applicationContext.getBean(HadoopEmbeddedJmxTrans.class);
		try {
			jmxTrans.loadConfiguration();
			applicationContext.registerShutdownHook();
			applicationContext.start();
            Object lock = new Object();
            synchronized (lock) {
                lock.wait();  
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
