package org.bigloupe.web.chart.service.impl;

import java.util.List;

import org.bigloupe.web.chart.dao.ChartDao;
import org.bigloupe.web.chart.dao.XYSeriesDao;
import org.bigloupe.web.chart.dao.impl.XYSeriesDaoImplMock;
import org.bigloupe.web.chart.dao.rrd.RRDChartDaoImpl;
import org.bigloupe.web.chart.exception.ChartException;
import org.bigloupe.web.chart.model.Series;
import org.bigloupe.web.chart.model.xy.XYSeries;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DatabaseChartServiceImplTest {

	DatabaseChartServiceImpl databaseChartServiceImpl = new DatabaseChartServiceImpl();
	ChartDao chartDao = new RRDChartDaoImpl();
	XYSeriesDao seriesDao = new XYSeriesDaoImplMock();

	@Before
	public void setUp() {
		databaseChartServiceImpl.chartDao = chartDao;
		databaseChartServiceImpl.xySeriesDao = seriesDao;
	}

	@Test
	public void testGetSeriesByKeyMalformed1() {
		
		List<XYSeries> listSeries = null;
		try {
			String key = "sum(company.server*.applicationInstance*.requestsHandled";
			listSeries = databaseChartServiceImpl
					.getXYSeriesByKey(key);
		} catch (ChartException e) {
			e.printStackTrace();
		}
		Assert.assertNull(listSeries);
	}

	
	//@Test - TODO Improve validator expression to support this test
	public void testGetSeriesByKeyMalformed2() {
		
		List<XYSeries> listSeries = null;
		try {
			String key = "sum(company.server{*.applicationInstance*}.requestsHandled)";
			listSeries = databaseChartServiceImpl
					.getXYSeriesByKey(key);
		} catch (ChartException e) {
			e.printStackTrace();
		}
		Assert.assertNull(listSeries);
	}
	
	@Test
	public void testGetSeriesByKeyWithStar() {
		String key = "company.server*.applicationInstance*.requestsHandled";
		List<XYSeries> listSeries = databaseChartServiceImpl.getXYSeriesByKey(key);
		Assert.assertEquals(6, listSeries.size());
	}

	@Test
	public void testGetSeriesByKeyWithBracket1() {
		String key = "company.server{1}.applicationInstance{1,2,3}.requestsHandled";
		List<XYSeries> listSeries = databaseChartServiceImpl.getXYSeriesByKey(key);
		Assert.assertEquals(3, listSeries.size());
	}

	@Test
	public void testGetSeriesByKeyWithBracket2() {
		String key = "company.server{1,2}.applicationInstance{1,2,3}.requestsHandled";
		List<XYSeries> listSeries = databaseChartServiceImpl.getXYSeriesByKey(key);
		Assert.assertEquals(6, listSeries.size());
	}

	@Test
	public void testGetSeriesByKeyWithBracket3() {
		String key = "company.server1.applicationInstance{1,3}.requestsHandled";
		List<XYSeries> listSeries = databaseChartServiceImpl.getXYSeriesByKey(key);
		Assert.assertEquals(2, listSeries.size());
	}

	@Test
	public void testGetSeriesByKeyWithFunctions1() {
		String key = "sum(company.server1.applicationInstance*.requestsHandled)";
		List<XYSeries> listSeries = databaseChartServiceImpl.getXYSeriesByKey(key);
		Assert.assertEquals(1, listSeries.size());
	}

	@Test
	public void testGetSeriesByKeyWithFunctions2() {
		String key = "summarize(derivative(company.server1.applicationInstance1.requestsHandled),\"1min\")";
		List<XYSeries> listSeries = databaseChartServiceImpl.getXYSeriesByKey(key);
		Assert.assertEquals(1, listSeries.size());
	}



}
