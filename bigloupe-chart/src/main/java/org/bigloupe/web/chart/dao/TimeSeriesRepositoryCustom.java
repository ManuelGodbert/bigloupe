package org.bigloupe.web.chart.dao;


public interface TimeSeriesRepositoryCustom {
	
	void addTimeSeriesDataItem(String key, long date, double value);
}
