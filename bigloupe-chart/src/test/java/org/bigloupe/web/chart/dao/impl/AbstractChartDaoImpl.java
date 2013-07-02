package org.bigloupe.web.chart.dao.impl;

import java.util.Date;

import org.bigloupe.web.chart.model.Chart;
import org.bigloupe.web.chart.model.xy.XYDataItem;
import org.bigloupe.web.chart.model.xy.XYSeries;

/**
 * Common function for DAO Tests
 *
 */
public abstract class AbstractChartDaoImpl {
	
	protected Chart createSimpleChartWithOneLinearXYSeries() {
		Chart chart = new Chart();
		chart.setKey("test");
		XYSeries series = new XYSeries();
		series.setKey("test-series");
		for (int i = 1; i < 60; i++) {
			Date now = new Date();
			series.add(new XYDataItem(now.getTime() + 1000*i, i));
			
		}
		
		chart.addSeries(series);
		return chart;
	}
}
