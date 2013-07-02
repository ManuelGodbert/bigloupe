package org.bigloupe.web.chart.dao.impl;

import org.bigloupe.test.configuration.MongoConfiguration;
import org.bigloupe.web.chart.dao.mongo.MongoChartDaoImpl;
import org.bigloupe.web.chart.model.Chart;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test 
 * 
 * @author bigloupe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={MongoConfiguration.class})
@ActiveProfiles("MongoChartDaoImplTest")
public class MongoChartDaoImplTest extends AbstractChartDaoImpl {

	@Autowired
	MongoChartDaoImpl chartDao;

	@Before
	public void setUp() {
	}

	@Test
	public void testSave() {
		Chart chart = createSimpleChartWithOneLinearXYSeries();
		chart = chartDao.save(chart);
		Assert.assertNotNull(chart);
		
		Chart newChart = chartDao.findByKey("test");
		Assert.assertNotNull(newChart);
		Assert.assertEquals(newChart.getKey(), chart.getKey());
	}


}
