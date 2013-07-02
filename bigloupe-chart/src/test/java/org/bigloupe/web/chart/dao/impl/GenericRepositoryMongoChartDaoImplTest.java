package org.bigloupe.web.chart.dao.impl;

import java.math.BigInteger;
import java.util.Date;

import org.bigloupe.test.configuration.GenericRepositoryMongoConfiguration;
import org.bigloupe.web.chart.dao.ChartDao;
import org.bigloupe.web.chart.model.Chart;
import org.bigloupe.web.chart.model.xy.XYDataItem;
import org.bigloupe.web.chart.model.xy.XYSeries;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test Generic Spring Data Repository
 * 
 * @author bigloupe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={GenericRepositoryMongoConfiguration.class})
@ActiveProfiles("GenericRepositoryMongoChartDaoImplTest")
public class GenericRepositoryMongoChartDaoImplTest extends AbstractChartDaoImpl {

	@Autowired
	ChartDao chartDao;


	@Before
	public void setUp() {

	}

	
	@Test
	public void testSaveAndFind() {
		Chart chart = createSimpleChartWithOneLinearXYSeries();
		chartDao.save(chart);
		
		Chart newChart = chartDao.findByKey("test");
		Assert.assertNotNull(newChart);
		Assert.assertEquals(newChart.getKey(), chart.getKey());
	}
	
	@Test
	public void testCount() {
		testSaveAndFind();
		long count = chartDao.count();
		Assert.assertTrue(count>=1);
	}
	
	@Test
	public void testDelete() {
		long countBefore = chartDao.count();
		Chart chart = createSimpleChartWithOneLinearXYSeries();
		chartDao.save(chart);
		long countAfter = chartDao.count();
		Assert.assertEquals(countBefore + 1, countAfter);
	}

}
