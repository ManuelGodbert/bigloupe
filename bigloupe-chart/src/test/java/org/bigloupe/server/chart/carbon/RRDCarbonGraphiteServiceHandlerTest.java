package org.bigloupe.server.chart.carbon;

import org.bigloupe.server.chart.AbstractCarbonServiceHandler;
import org.bigloupe.server.chart.CarbonGraphiteServiceHandler;
import org.bigloupe.web.chart.dao.ChartDao;
import org.bigloupe.web.chart.dao.TimeSeriesDao;
import org.bigloupe.web.chart.dao.rrd.RRDChartDaoImpl;
import org.bigloupe.web.chart.dao.rrd.RRDTimeSeriesDaoImpl;
import org.bigloupe.web.chart.service.impl.DatabaseChartServiceImpl;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test backend RRD4J-FILE
 * 
 * @author bigloupe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml", "classpath:applicationContext-rrd.xml"})
@ActiveProfiles(profiles={"chart-rrd", "standalone"})
public class RRDCarbonGraphiteServiceHandlerTest extends AbstractCarbonServiceHandler {

	@Before
	public void setUp() throws Exception {
		DatabaseChartServiceImpl chartService = new DatabaseChartServiceImpl();
		TimeSeriesDao timeSeriesDao = new RRDTimeSeriesDaoImpl();
		ChartDao chartDao = new RRDChartDaoImpl();
		chartService.setChartDao(chartDao);
		chartService.setTimeSeriesDao(timeSeriesDao);
		
		carbonGraphiteServiceHandler = new CarbonGraphiteServiceHandler(
				chartService);
	}

}
