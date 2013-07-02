package org.bigloupe.web.chart.service;

import java.util.List;

import org.bigloupe.web.chart.exception.ChartException;
import org.bigloupe.web.chart.model.Chart;
import org.bigloupe.web.chart.model.DataItem;
import org.bigloupe.web.chart.model.time.TimeSeries;
import org.bigloupe.web.chart.model.time.TimeSeriesDataItem;
import org.bigloupe.web.chart.model.xy.XYDataItem;
import org.bigloupe.web.chart.model.xy.XYSeries;

/**
 * Chart service
 * 
 * @author bigloupe
 * 
 */
public interface ChartService {

	public long count() throws ChartException;

	/**
	 * Get chart by id
	 * 
	 * @param id
	 * @return
	 */
	public Chart getChart(Long id) throws ChartException;

	/**
	 * Get chart by key
	 * 
	 * @param target
	 * @return
	 */
	public Chart getChartByKey(String key) throws ChartException;

	public Chart saveChart(Chart chart) throws ChartException;

	/**
	 * Add series to a chart {id}
	 * 
	 * @param id
	 * @param series
	 * @return
	 */
	public Chart addSeries(Long id, XYSeries series) throws ChartException;
	public Chart addSeries(Long id, TimeSeries series) throws ChartException;

	public XYSeries saveSeries(XYSeries series) throws ChartException;
	public TimeSeries saveSeries(TimeSeries series) throws ChartException;

	public void deleteXYSeries(String key) throws ChartException;
	public void deleteXYSeries(Long id);

	public void deleteTimeSeries(String key) throws ChartException;
	public void deleteTimeSeries(Long id);

	
	public XYSeries getXYSeries(Long id) throws ChartException;
	public TimeSeries getTimeSeries(Long id) throws ChartException;

	/**
	 * List Series by key. Key can contains '*' or {1,2,3}
	 * 
	 * @param key
	 * @return
	 */
	public List<XYSeries> getXYSeriesByKey(String key) throws ChartException;
	public List<TimeSeries> getTimeSeriesByKey(String key) throws ChartException;
	public List<TimeSeries> getTimeSeriesByKey(String target, String from, String until) throws ChartException;
	
	/**
	 * List Series by simple key. Key cannot contains expression with '*' or
	 * {1,2,3}
	 * 
	 * @param key
	 * @return
	 */
	public XYSeries getXYSeriesBySimpleKey(String key) throws ChartException;
	public TimeSeries getTimeSeriesBySimpleKey(String key) throws ChartException;

	public XYSeries addDataItem(XYSeries series, XYDataItem item)
			throws ChartException;
	public TimeSeries addDataItem(TimeSeries series, TimeSeriesDataItem item)
			throws ChartException;
	
	/**
	 * 
	 * @param key of series
	 * @param date
	 * @param value
	 */
	public void addTimeSeriesDataItem(String key, long date, double value);



}
