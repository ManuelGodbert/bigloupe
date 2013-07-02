package org.bigloupe.web.chart.dao;

import java.util.List;

import org.bigloupe.web.chart.model.time.TimeSeries;
import org.bigloupe.web.chart.model.xy.XYSeries;

public interface TimeSeriesDao {

	List<TimeSeries> findById(Long id);

	List<TimeSeries> findByKey(String key);

	TimeSeries findOne(Long id);
	
	List<TimeSeries> findByKeyContaining(String key);
	
	void addTimeSeriesDataItem(String key, long date, double value);
	
	TimeSeries save(TimeSeries series);
	
	void delete(Long id);
	
	void delete(TimeSeries id);
}
