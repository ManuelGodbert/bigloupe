package org.bigloupe.web.monitor.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for Hadoop Monitoring
 * @author bigloupe
 *
 */
@Controller
@RequestMapping("/hadoop-monitoring")
public class HadoopMonitoringController {

	/**
	 * 
	 * @param request
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/jmx-namenodeconfiguration", method = RequestMethod.GET)
	public String namenode(HttpServletRequest request) throws Exception {
		return "geo-zones";
	}
	

}
