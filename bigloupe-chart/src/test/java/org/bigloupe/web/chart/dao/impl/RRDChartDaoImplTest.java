package org.bigloupe.web.chart.dao.impl;

import java.util.Date;

import org.bigloupe.web.chart.dao.ChartDao;
import org.bigloupe.web.chart.dao.rrd.RRDChartDaoImpl;
import org.bigloupe.web.chart.model.Chart;
import org.bigloupe.web.chart.model.xy.XYDataItem;
import org.bigloupe.web.chart.model.xy.XYSeries;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("chart-rrd")
public class RRDChartDaoImplTest {

	ChartDao chartDao = new RRDChartDaoImpl();

	@Before
	public void setUp() {

	}

	
	@Test
	public void testSave() {
		Chart chart = new Chart();
		chart.setKey("test");
		XYSeries series = new XYSeries();
		for (int i = 1; i < 60; i++) {
			Date now = new Date();
			series.add(new XYDataItem(now.getTime() + 1000*i, i));
			
		}
		
		chart.addSeries(series);
		chart = chartDao.save(chart);
		Assert.assertNotNull(chart);
	}

}
