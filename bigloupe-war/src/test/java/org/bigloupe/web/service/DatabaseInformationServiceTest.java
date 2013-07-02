package org.bigloupe.web.service;

import org.bigloupe.web.service.database.DatabaseInformationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Create database information for KARMA Database Test
 * 
 * @author bigloupe
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={SpringServletConfiguration.class})
public class DatabaseInformationServiceTest extends AbstractTransactionalJUnit4SpringContextTests{

	@Autowired
	private DatabaseInformationService databaseInformationService;

	@Test
	public void testPrepareDatabaseInformation() throws Exception {
		databaseInformationService.prepareDatabaseInformation();
	}
}
