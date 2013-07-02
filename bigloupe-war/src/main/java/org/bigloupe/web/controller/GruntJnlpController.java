package org.bigloupe.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller used to dynamically build a JNLP file used to launch Apache Pig
 * Grunt
 * 
 */
@Controller
public class GruntJnlpController {

	@RequestMapping(value = "/grunt", method = RequestMethod.GET)
	protected String grunt(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) throws Exception {

		StringBuilder sb = new StringBuilder();
		sb.append("http://");
		sb.append(request.getServerName());
		if (request.getServerPort() != 80) {
			sb.append(":");
			sb.append(request.getServerPort());
		}
		sb.append(request.getContextPath());

		// prevent JNLP caching by setting response headers
		response.setHeader("cache-control", "no-cache");
		response.setHeader("pragma", "no-cache");
		response.setContentType("application/x-java-jnlp-file");
		response.setHeader("Content-disposition", "attachment; filename=grunt.jnlp");

		model.put("codebaseUrl", sb.toString());
		return "grunt";
	}
}
