package org.bigloupe.web.chart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bigloupe.ErrorCode;
import org.bigloupe.web.chart.exception.ChartException;
import org.bigloupe.web.chart.model.Chart;
import org.bigloupe.web.chart.model.Renderer;
import org.bigloupe.web.chart.model.Series;
import org.bigloupe.web.chart.model.time.TimeSeries;
import org.bigloupe.web.chart.model.time.TimeSeriesDataItem;
import org.bigloupe.web.chart.model.xy.XYDataItem;
import org.bigloupe.web.chart.model.xy.XYSeries;
import org.bigloupe.web.chart.service.ChartService;
import org.bigloupe.web.controller.AbstractConfigurationController;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Generic controller for charts
 * 
 * @author bigloupe
 * 
 */
@Controller
@RequestMapping("/chart")
public class ChartController {

	private static Logger logger = LoggerFactory
			.getLogger(ChartController.class);

	@Autowired
	ChartService chartService;

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ChartException handleNotFoundException(RuntimeException ex,
			HttpServletRequest request) {

		if (logger.isDebugEnabled())
			ex.printStackTrace();

		ex.printStackTrace();

		return new ChartException(ex);
	}

	/**
	 * List all charts available in database (H2)
	 */
	@RequestMapping("/index")
	public String showIndexChartPage(ModelMap model) {
		return "chart";
	}

	/**
	 * Get a chart by id
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/chart-api/{id:[0-9]*}", method = RequestMethod.GET, headers = { "content-type=application/json" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Chart getChart(Model model, @PathVariable Long id)
			throws ChartException {
		return chartService.getChart(id);
	}

	/**
	 * Get a chart by key
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/chart-api/{key}", method = RequestMethod.GET, headers = { "content-type=application/json" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Chart getChart(Model model, @PathVariable String key)
			throws ChartException {
		return chartService.getChartByKey(key);
	}
	
	
	/**
	 * Save a new chart with series
	 * 
	 * @param model
	 * @param chart
	 * @return
	 */
	@RequestMapping(value = "/chart-api", method = RequestMethod.POST, headers = { "content-type=application/json" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Chart saveChart(Model model, @RequestBody Chart chart)
			throws ChartException {
		return chartService.saveChart(chart);
	}

	/**
	 * Create new sample chart
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/chart-api/create/{key}", method = RequestMethod.POST, headers = { "content-type=application/json" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Chart createChart(Model model, @PathVariable String key)
			throws ChartException {
		Chart chart = new Chart();
		chart.setKey(key);
		chart.setDescription("sample chart");
		chart.addSeries(createXYSeries(model, key));
		chart.setRenderer(Renderer.BAR);
		return chartService.saveChart(chart);
	}

	/**
	 * Add XY series to a chart {id}
	 * 
	 * @param model
	 * @param id
	 * @param series
	 * @return
	 */
	@RequestMapping(value = "/chart-api/add-series/xy/{id}", method = RequestMethod.POST, headers = { "content-type=application/json" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Chart addXYSeries(Model model, @PathVariable Long id,
			@RequestBody XYSeries series) throws ChartException {
		return chartService.addSeries(id, series);
	}

	/**
	 * Add Time series to a chart {id}
	 * 
	 * @param model
	 * @param id
	 * @param series
	 * @return
	 */
	@RequestMapping(value = "/chart-api/add-series/time/{id}", method = RequestMethod.POST, headers = { "content-type=application/json" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Chart addTimeSeries(Model model, @PathVariable Long id,
			@RequestBody TimeSeries series) throws ChartException {
		return chartService.addSeries(id, series);
	}
	
	/**
	 * Get XY series by id
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/series-api/xy/id/{id}", method = RequestMethod.GET, headers = { "content-type=application/json" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public XYSeries getXYSeries(Model model, @PathVariable Long id)
			throws ChartException {
		return chartService.getXYSeries(id);
	}

	/**
	 * Get Time series by id
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/series-api/time/id/{id}", method = RequestMethod.GET, headers = { "content-type=application/json" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public TimeSeries getTimeSeries(Model model, @PathVariable Long id)
			throws ChartException {
		return chartService.getTimeSeries(id);
	}
	
	/**
	 * Get XY series by key. Key can contains expression.
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/series-api/xy/key/{key}", method = RequestMethod.GET, headers = { "content-type=application/json" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public List<XYSeries> getXYSeries(Model model, @PathVariable String key)
			throws ChartException {
		return chartService.getXYSeriesByKey(key);
	}

	/**
	 * Get XY series by key. Key can contains expression.
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/series-api/time/key/{key}", method = RequestMethod.GET, headers = { "content-type=application/json" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public List<TimeSeries> getTimeSeries(Model model, @PathVariable String key)
			throws ChartException {
		return chartService.getTimeSeriesByKey(key);
	}
	
	/**
	 * Remove XY series by id
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/series-api/xy/id/{id}", method = RequestMethod.DELETE, headers = { "content-type=application/json" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public org.bigloupe.web.dto.ResponseStatus deleteXYSeries(Model model,
			@PathVariable Long id) throws ChartException {
		chartService.deleteXYSeries(id);
		return new org.bigloupe.web.dto.ResponseStatus(true, "Series '" + id
				+ "' deleted");
	}

	/**
	 * Remove Time series by id
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/series-api/time/id/{id}", method = RequestMethod.DELETE, headers = { "content-type=application/json" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public org.bigloupe.web.dto.ResponseStatus deleteTimeSeries(Model model,
			@PathVariable Long id) throws ChartException {
		chartService.deleteTimeSeries(id);
		return new org.bigloupe.web.dto.ResponseStatus(true, "Series '" + id
				+ "' deleted");
	}
	
	/**
	 * Remove XY series by key (key can contains expression with '*' and '{}'
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/series-api/xy/key/{key}", method = RequestMethod.DELETE, headers = { "content-type=application/json" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public org.bigloupe.web.dto.ResponseStatus deleteXYSeries(Model model,
			@PathVariable String key) throws ChartException {
		chartService.deleteXYSeries(key);
		return new org.bigloupe.web.dto.ResponseStatus(true,
				"Series with key '" + key + "' deleted");
	}

	/**
	 * Remove Time series by key (key can contains expression with '*' and '{}'
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/series-api/time/key/{key}", method = RequestMethod.DELETE, headers = { "content-type=application/json" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public org.bigloupe.web.dto.ResponseStatus deleteTimeSeries(Model model,
			@PathVariable String key) throws ChartException {
		chartService.deleteTimeSeries(key);
		return new org.bigloupe.web.dto.ResponseStatus(true,
				"Series with key '" + key + "' deleted");
	}
	
	/**
	 * Save XY series
	 * 
	 * @param model
	 * @param series
	 * @return
	 */
	@RequestMapping(value = "/series-api/xy", method = RequestMethod.POST, headers = { "content-type=application/json" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Series saveXYSeries(Model model, @RequestBody XYSeries series)
			throws ChartException {
		return chartService.saveSeries(series);
	}
	
	/**
	 * Save series
	 * 
	 * @param model
	 * @param series
	 * @return
	 */
	@RequestMapping(value = "/series-api/time", method = RequestMethod.POST, headers = { "content-type=application/json" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Series saveTimeSeries(Model model, @RequestBody TimeSeries series)
			throws ChartException {
		return chartService.saveSeries(series);
	}

	/**
	 * Add XY data item in series
	 * 
	 * @param model
	 * @param series
	 * @return
	 * @throws ChartException
	 */

	@RequestMapping(value = "/series-api/xy/{id}/addData", method = RequestMethod.POST, headers = { "content-type=application/json" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Series addXYDataItemSeries(Model model, @PathVariable Long id,
			@RequestBody XYDataItem item) throws ChartException {
		// Retrieve series
		XYSeries series = chartService.getXYSeries(id);
		if (series == null)
			throw new ChartException(ErrorCode.SERIES_NOT_FOUND);
		return chartService.addDataItem(series, item);
	}

	/**
	 * Add data item in time series
	 * 
	 * @param model
	 * @param series
	 * @return
	 * @throws ChartException
	 */
	@RequestMapping(value = "/series-api/time/{id}/addData", method = RequestMethod.POST, headers = { "content-type=application/json" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Series addTimeDataItemSeries(Model model, @PathVariable Long id,
			@RequestBody TimeSeriesDataItem item) throws ChartException {
		// Retrieve series
		TimeSeries series = chartService.getTimeSeries(id);
		if (series == null)
			throw new ChartException(ErrorCode.SERIES_NOT_FOUND);
		return chartService.addDataItem(series, item);
	}


	/**
	 * Create a new series for example
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/series-api/xy/createSeries/{key}", method = RequestMethod.POST)
	@ResponseBody
	public XYSeries createXYSeries(Model model, @PathVariable String key)
			throws ChartException {
		XYSeries series = new XYSeries();
		series.setDescription("Sample XY series");
		series.setKey(key);
		series.setColor("steelblue");
		series.add(0, 40);
		series.add(1, 49);
		series.add(2, 38);
		series.add(3, 30);
		series.add(4, 32);
		return chartService.saveSeries(series);
	}

	/**
	 * Create a new series for example
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/series-api/time/createSeries/{key}", method = RequestMethod.POST)
	@ResponseBody
	public TimeSeries createTimeSeries(Model model, @PathVariable String key)
			throws ChartException {
		TimeSeries series = new TimeSeries();
		series.setDescription("Sample time series");
		series.setKey(key);
		series.setColor("steelblue");
		DateTime dt = new DateTime();
		series.add(dt.plus(Period.minutes(1)).getMillis(), 12.0);
		series.add(dt.plus(Period.minutes(1)).getMillis(), 17);
		series.add(dt.plus(Period.minutes(1)).getMillis(), 42);
		series.add(dt.plus(Period.minutes(1)).getMillis(), 34);
		series.add(dt.plus(Period.minutes(1)).getMillis(), 40);
		return chartService.saveSeries(series);
	}
}
