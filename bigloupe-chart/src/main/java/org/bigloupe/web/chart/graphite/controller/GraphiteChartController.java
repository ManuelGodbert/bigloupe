package org.bigloupe.web.chart.graphite.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bigloupe.ErrorCode;
import org.bigloupe.web.chart.exception.ChartException;
import org.bigloupe.web.chart.graphite.model.GraphiteSeries;
import org.bigloupe.web.chart.graphite.model.ListGraphiteChartForJsonP;
import org.bigloupe.web.chart.model.time.TimeSeries;
import org.bigloupe.web.chart.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Chart controller compatible with <a
 * href="http://graphite.wikidot.com/">Graphite</a> All UI Front End can be used
 * 
 * @author bigloupe
 * 
 */
@Controller
@RequestMapping("/graphite")
public class GraphiteChartController {

	@Autowired
	ChartService chartService;

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ChartException handleNotFoundException(RuntimeException ex,
			HttpServletRequest request) {
		ex.printStackTrace();
		return new ChartException(ex);
	}

	/**
	 * Render graphic
	 */
	@RequestMapping(value = "/render", produces = "application/x-javascript")
	@ResponseBody
	public ListGraphiteChartForJsonP render(
			@RequestParam(value = "from", required = true) String from,
			@RequestParam(value = "until", required = false) String until,
			@RequestParam(value = "target", required = true) String[] targets,
			@RequestParam(value = "format", required = true) String format,
			@RequestParam(value = "jsonp", required = false) String jsonCallback,
			ModelMap model) throws ChartException {
		if (!format.equals("json")) {
			throw new ChartException(ErrorCode.CHART_ONLY_JSON_FORMAT_SUPPORTED);
		}

		ListGraphiteChartForJsonP graphiteChartForJsonP = new ListGraphiteChartForJsonP();
		graphiteChartForJsonP.setJsonCallback(jsonCallback);

		for (String target : targets) {
			// Find series
			List<TimeSeries> series = chartService.getTimeSeriesByKey(target,
					from, until);

			if (series != null) {
				for (TimeSeries seriesToAddInGraphiteChart : series) {
					graphiteChartForJsonP.add(new GraphiteSeries(target,
							seriesToAddInGraphiteChart));
				}
			} else
				graphiteChartForJsonP.add(new GraphiteSeries(target, null));
		}

		return graphiteChartForJsonP;

	}
}
