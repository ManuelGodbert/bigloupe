package org.bigloupe.web.chart.dao.jpa;

import org.bigloupe.web.chart.dao.TimeSeriesRepositoryCustom;


public class TimeSeriesRepositoryImpl implements TimeSeriesRepositoryCustom {

	@Override
	public void addTimeSeriesDataItem(String key, long date, double value) {
		// TODO Auto-generated method stub
		System.out.println("Add value" + key + ", " + date + ", " + value);
		
	}
 

}
