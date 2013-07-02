package org.bigloupe.web.chart.dao;

import java.util.List;

import org.bigloupe.web.chart.model.xy.XYSeries;

public interface XYSeriesDao {
	
	List<XYSeries> findById(Long id);

	List<XYSeries> findByKey(String key);

	XYSeries findOne(Long id);
	
	List<XYSeries> findByKeyContaining(String key);
	
	XYSeries save(XYSeries series);

	void delete(Long id);
	
	void delete(XYSeries id);
}
