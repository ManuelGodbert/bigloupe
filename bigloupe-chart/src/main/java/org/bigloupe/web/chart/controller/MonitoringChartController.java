package org.bigloupe.web.chart.controller;

import org.bigloupe.web.chart.service.ChartService;
import org.bigloupe.web.controller.AbstractConfigurationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chart-monitoring")
public class MonitoringChartController extends AbstractConfigurationController {

	@Autowired
	ChartService chartService;
	
	/**
	 * List all charts available in database (H2)
	 */
	@RequestMapping("/cpu")
	public String showIndexChartPage(ModelMap model) {
		return "cpu";
	}
}
