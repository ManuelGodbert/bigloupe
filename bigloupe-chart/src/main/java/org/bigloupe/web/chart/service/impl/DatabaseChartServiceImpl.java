package org.bigloupe.web.chart.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.bigloupe.ErrorCode;
import org.bigloupe.web.chart.dao.ChartDao;
import org.bigloupe.web.chart.dao.TimeSeriesDao;
import org.bigloupe.web.chart.dao.XYSeriesDao;
import org.bigloupe.web.chart.exception.ChartException;
import org.bigloupe.web.chart.function.FunctionManager;
import org.bigloupe.web.chart.model.Chart;
import org.bigloupe.web.chart.model.Series;
import org.bigloupe.web.chart.model.time.TimeSeries;
import org.bigloupe.web.chart.model.time.TimeSeriesDataItem;
import org.bigloupe.web.chart.model.xy.XYDataItem;
import org.bigloupe.web.chart.model.xy.XYSeries;
import org.bigloupe.web.chart.service.ChartService;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.ReadablePeriod;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Load & Store chart in database
 * 
 * @author bigloupe
 * 
 */
@Service
public class DatabaseChartServiceImpl implements ChartService {

	private static Logger logger = LoggerFactory
			.getLogger(DatabaseChartServiceImpl.class);

	@Autowired
	ChartDao chartDao;

	@Autowired
	TimeSeriesDao timeSeriesDao;

	@Autowired
	XYSeriesDao xySeriesDao;

	FunctionManager functionManager = new FunctionManager();

	public ChartDao getChartDao() {
		return chartDao;
	}

	public void setChartDao(ChartDao chartDao) {
		this.chartDao = chartDao;
	}

	public TimeSeriesDao getTimeSeriesDao() {
		return timeSeriesDao;
	}

	public void setTimeSeriesDao(TimeSeriesDao timeSeriesDao) {
		this.timeSeriesDao = timeSeriesDao;
	}

	public XYSeriesDao getXySeriesDao() {
		return xySeriesDao;
	}

	public void setXySeriesDao(XYSeriesDao xySeriesDao) {
		this.xySeriesDao = xySeriesDao;
	}

	@Override
	public Chart getChart(Long id) {
		return chartDao.findOne(id);
	}

	@Override
	public Chart getChartByKey(String key) {
		return chartDao.findByKey(key);
	}

	@Override
	public Chart saveChart(Chart chart) {
		return chartDao.save(chart);
	}

	@Override
	public long count() {
		return chartDao.count();
	}

	@Override
	public Chart addSeries(Long id, XYSeries series) {
		Chart chart = getChart(id);
		if (chart == null) {
			chart = new Chart();
			chart = chartDao.save(chart);
		}
		chart.addSeries(series);
		return chart;
	}

	@Override
	public Chart addSeries(Long id, TimeSeries series) {
		Chart chart = getChart(id);
		if (chart == null) {
			chart = new Chart();
			chart = chartDao.save(chart);
		}
		chart.addSeries(series);
		return chart;
	}

	@Override
	public XYSeries saveSeries(XYSeries series) {
		try {
			return xySeriesDao.save(series);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public TimeSeries saveSeries(TimeSeries series) {
		try {
			return timeSeriesDao.save(series);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void deleteXYSeries(Long id) {
		xySeriesDao.delete(id);
	}

	/**
	 * Delete series : key can support expression with '*' or '%'
	 * 
	 * @param key
	 */
	@Override
	public void deleteXYSeries(String key) {
		List<XYSeries> listSeries = getXYSeriesByKey(key);
		if (listSeries.isEmpty())
			return;
		else
			for (XYSeries series : listSeries) {
				xySeriesDao.delete(series);
			}
	}

	@Override
	public void deleteTimeSeries(Long id) {
		timeSeriesDao.delete(id);
	}

	@Override
	public void deleteTimeSeries(String key) throws ChartException {
		List<TimeSeries> listSeries = getTimeSeriesByKey(key);
		if (listSeries.isEmpty())
			return;
		else
			for (TimeSeries series : listSeries) {
				timeSeriesDao.delete(series);
			}

	}

	@Override
	public XYSeries getXYSeries(Long id) {
		return xySeriesDao.findOne(id);
	}

	@Override
	public TimeSeries getTimeSeries(Long id) {
		return timeSeriesDao.findOne(id);
	}

	/**
	 * Add item to series
	 * 
	 * @throws ChartException
	 */
	@Override
	public XYSeries addDataItem(XYSeries series, XYDataItem item)
			throws ChartException {
		if (series == null)
			throw new ChartException(ErrorCode.SERIES_NOT_FOUND);
		series.add(item);
		return xySeriesDao.save(series);
	}

	@Override
	public TimeSeries addDataItem(TimeSeries series, TimeSeriesDataItem item)
			throws ChartException {
		if (series == null)
			throw new ChartException(ErrorCode.SERIES_NOT_FOUND);
		series.add(item);
		return timeSeriesDao.save(series);
	}

	/**
	 * Add item to timeseries
	 */
	@Override
	public void addTimeSeriesDataItem(String key, long date, double value) {
		timeSeriesDao.addTimeSeriesDataItem(key, date, value);
	}

	/**
	 * List Series by key. Key can cannot contain expression A unique series
	 * will be return
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public XYSeries getXYSeriesBySimpleKey(String key) throws ChartException {
		List<XYSeries> listSeries = xySeriesDao.findByKey(key);
		if ((listSeries == null) || (listSeries.isEmpty()))
			return null;
		else
			return listSeries.get(0);
	}

	@Override
	public TimeSeries getTimeSeriesBySimpleKey(String key)
			throws ChartException {
		List<TimeSeries> listSeries = timeSeriesDao.findByKey(key);
		if ((listSeries == null) || (listSeries.isEmpty()))
			return null;
		else
			return listSeries.get(0);
	}

	/**
	 * List Series by key. Key can contains '*' or {1,2,3} and also functions
	 * e.g sum(company.server.application*.requestsHandled)
	 * summarize(derivative(app.numUsers),"1min")
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public List<XYSeries> getXYSeriesByKey(String key) throws ChartException {
		List<XYSeries> listSeries = new ArrayList<XYSeries>();
		validateExpression(key);
		findFunction(key, listSeries, null, "none", false);
		return listSeries;
	}

	@Override
	public List<TimeSeries> getTimeSeriesByKey(String key)
			throws ChartException {
		List<TimeSeries> listSeries = new ArrayList<TimeSeries>();
		validateExpression(key);
		findFunction(key, listSeries, null, "none", true);
		return listSeries;
	}

	@Override
	public List<TimeSeries> getTimeSeriesByKey(String key, String from,
			String until) throws ChartException {
		DateTime fromDateTime = parseTimeExpression(from);
		List<TimeSeries> listSeries = new ArrayList<TimeSeries>();
		validateExpression(key);
		findFunction(key, listSeries, fromDateTime, "none", true);
		return listSeries;
	}
	
	
	/**
	 * MANAGE FUNCTIONS AND EXPRESSIONS
	 */
	
	/**
	 * Validate expression (number of parenthesis and bracket) TODO : Add more
	 * controls
	 * 
	 * @param key
	 */
	private void validateExpression(String key) {

		int countOpen = StringUtils.countMatches(key, "(");
		int countClosed = StringUtils.countMatches(key, ")");
		if (countOpen != countClosed) {
			String msg = (countOpen < countClosed ? "(" : ")");
			throw new ChartException(ErrorCode.SERIES_EXPRESSION_MALFORMED,
					key, msg);
		}
		countOpen = StringUtils.countMatches(key, "{");
		countClosed = StringUtils.countMatches(key, "}");
		if (countOpen != countClosed) {
			String msg = (countOpen < countClosed ? "{" : "}");
			throw new ChartException(ErrorCode.SERIES_EXPRESSION_MALFORMED,
					key, msg);
		}
	}

	/**
	 * Extract function and series
	 * 
	 * @param key
	 * @param listSeries
	 * @param fromDateTime 
	 * @param function
	 * @throws ChartException
	 */
	private void findFunction(String key,
			@SuppressWarnings("rawtypes") List listSeries, DateTime fromDateTime, String function, boolean isTimeSeries)
			throws ChartException {

		if (key.contains("(") && key.contains(")")) {
			int startParameterFunction = key.indexOf("(");
			int endParameterFunction = key.lastIndexOf(")");
			function = key.substring(0, startParameterFunction);
			String newKey = key.substring(startParameterFunction + 1,
					endParameterFunction);
			logger.debug("Execute function " + function + "(" + newKey + ")");
			if (newKey.contains("(")) {
				findFunction(newKey, listSeries, fromDateTime, function, isTimeSeries);
				List listSeriesWithFunction = new ArrayList();
				findAllSeries(listSeriesWithFunction, newKey, fromDateTime, isTimeSeries);

				// Execute function for all series found
				logger.debug("Find " + listSeriesWithFunction.size()
						+ " with function '" + function + "'");
				listSeriesWithFunction = executeFunction(function,
						listSeriesWithFunction);
				listSeries.addAll(listSeriesWithFunction);

			} else {
				List listSeriesWithFunction = new ArrayList();
				findAllSeries(listSeriesWithFunction, newKey, fromDateTime, isTimeSeries);

				// Execute function for all series found
				logger.debug("Find " + listSeriesWithFunction.size()
						+ " with function '" + function + "'");
				listSeriesWithFunction = executeFunction(function,
						listSeriesWithFunction);
				listSeries.addAll(listSeriesWithFunction);
			}

		} else {
			findAllSeries(listSeries, key, fromDateTime, isTimeSeries);
		}

	}

	/**
	 * Execute function for a list of series
	 * 
	 * @param function
	 * @param listSeriesWithFunction
	 */
	private List<Series> executeFunction(String function,
			List<Series> listSeriesWithFunction) {
		return functionManager.execute(function, listSeriesWithFunction);

	}

	private void findAllSeries(List listSeries, String key, DateTime fromDateTime, boolean isTimeSeries) {

		// Replace all bracket {1,2,3} per value
		replaceBracket(key, listSeries, isTimeSeries);

		// Replace all '*' per key
		// parameter bound wrapped in %
		if (key.contains("*")) {
			key = key.replace("*", "%");
			if (isTimeSeries)
				listSeries.addAll(timeSeriesDao.findByKeyContaining(key));
			else
				listSeries.addAll(xySeriesDao.findByKeyContaining(key));
		}
		if (isTimeSeries)
			listSeries.addAll(timeSeriesDao.findByKey(key));
		else
			listSeries.addAll(xySeriesDao.findByKey(key));

	}

	/**
	 * Replace all brackets by values
	 * 
	 * @param key
	 * @param listSeries
	 */
	private void replaceBracket(String key, List listSeries,
			boolean isTimeSeries) {
		if (key.contains("{") && key.contains("}")) {
			int startBracket = key.indexOf("{");
			int endBracket = key.indexOf("}");
			String values = key.substring(startBracket + 1, endBracket);
			StringTokenizer valuesTokenizer = new StringTokenizer(values, ",");
			while (valuesTokenizer.hasMoreElements()) {
				String newKey = key.substring(0, startBracket)
						+ valuesTokenizer.nextToken()
						+ key.substring(endBracket + 1, key.length());
				if (newKey.contains("{")) {
					replaceBracket(newKey, listSeries, isTimeSeries);
				} else if (isTimeSeries)
					listSeries.addAll(timeSeriesDao.findByKey(newKey));
				else

					listSeries.addAll(xySeriesDao.findByKey(newKey));
			}

		}
	}
	
	/**
	 * MANAGE TIME
	 */
	
	/**
	 * Should return a from and until could be equals to : -12hours = last 12
	 * hours -5min = last 5 min
	 * 
	 * @param from
	 * @return
	 */
	protected ReadablePeriod calculatePeriod(String from, String until) {

		// return parseAtTime(from).;
		DateTime fromDateTime = parseTimeExpression(from);
		DateTime untilDateTime = parseTimeExpression(until);
		return new Period(fromDateTime, untilDateTime);

	}
	
	/**
	 * Parse Graphite time expression
	 * @param time
	 * @return
	 */
	private DateTime parseTimeExpression(String time) {
		if (time == null)
			return DateTime.now();

		time = time.toLowerCase().replace("_", "").replace(",", "").trim();

		if (StringUtils.isNumeric(time)) {
			return new DateTime(Integer.parseInt(time));
		}
		if (time.contains("now"))
			return DateTime.now();

		DateTime calculateTime = new DateTime();
		calculateTime = parseExpressionRecursively(calculateTime, time);

		return calculateTime;
	}
	
	/**
	 * Parse expression with '+' and '-'
	 * 
	 * @param calculateTime
	 * @param expression
	 * @return
	 */
	private DateTime parseExpressionRecursively(DateTime calculateTime,
			String expression) {
		for (int i = 0; i < expression.length(); i++) {
			String beforeOperator = null;
			if (expression.charAt(i) == '+') {
				beforeOperator = expression.substring(0, i);
				if (beforeOperator != null)
					calculateTime = calculateTime
							.plus(convertPeriod(beforeOperator));
				String afterOperator = expression.substring(i + 1,
						expression.length());

				if ((afterOperator.contains("+"))
						|| (afterOperator.contains("-")))
					return parseExpressionRecursively(calculateTime,
							afterOperator);
				else {
					calculateTime = calculateTime
							.plus(convertPeriod(afterOperator));
					return calculateTime;
				}

			}
			if (expression.charAt(i) == '-') {
				beforeOperator = expression.substring(0, i);
				if (beforeOperator != null)
					calculateTime = calculateTime
							.plus(convertPeriod(beforeOperator));
				String afterOperator = expression.substring(i + 1,
						expression.length());
				if ((afterOperator.contains("+"))
						|| (afterOperator.contains("-")))
					return parseExpressionRecursively(calculateTime,
							afterOperator);
				else {
					calculateTime = calculateTime
							.minus(convertPeriod(afterOperator));
					return calculateTime;
				}
			}

		}
		return calculateTime;

	}

	/**
	 * Convert information in period
	 * 
	 * @param time
	 * @return
	 */
	private ReadablePeriod convertPeriod(String time) {
		if ((time == null) || (time.equals("")))
			return Seconds.seconds(0);
		ReadablePeriod period = Seconds.seconds(0);
		// Extract period (d : day , min : minutes, h : hours, ...)
		String timeWithNoCharacters = time.replaceAll("[a-zA-Z]", "");
		int value = Integer.parseInt(timeWithNoCharacters);

		if (time.contains("d") || time.contains("days"))
			period = Days.days(value);
		if (time.contains("min") || time.contains("minutes"))
			period = Minutes.minutes(value);
		if (time.contains("h") || time.contains("hours"))
			period = Hours.hours(value);

		// if (time.contains("noon"))
		// return LocalTime.MIDNIGHT;
		return period;

	}



}
