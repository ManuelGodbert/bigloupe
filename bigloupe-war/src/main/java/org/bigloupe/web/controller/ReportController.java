package org.bigloupe.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for report
 * @author bigloupe
 *
 */
@Controller
@RequestMapping("/report")
public class ReportController {

	/**
	 * Report for RF_GEO_ZONES
	 * 
	 * @param request
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/geoZones", method = RequestMethod.GET)
	public String reportGeoZones(HttpServletRequest request) throws Exception {
		return "geo-zones";
	}
	
	/**
	 * Report for RF_GEO_ZONES
	 * 
	 * @param request
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/franceGeoZones", method = RequestMethod.GET)
	public String reportFranceGeoZones(HttpServletRequest request) throws Exception {
		return "france-geo-zones";
	}
	
	/**
	 * Report for all airports
	 * 
	 * @param request
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/airport", method = RequestMethod.GET)
	public String reportAirport(HttpServletRequest request) throws Exception {
		return "airport";
	}
	
	/**
	 * Report for airports europe
	 * 
	 * @param request
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/airportEurope", method = RequestMethod.GET)
	public String reportAirportEurope(HttpServletRequest request) throws Exception {
		return "airport-europe";
	}
}
