package org.bigloupe.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.bigloupe.web.BigLoupeConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.ServletContextAware;

@SessionAttributes({ "configuration", "configurationHadoopKarmaCluster" })
public class AbstractConfigurationController implements ServletContextAware {

	@Autowired(required = false)
	public BigLoupeConfiguration configuration;

	ServletContext servletContext;

	List<String> messages;
	
	static HashMap<String, String> configurationHadoopKarmaCluster;
	
	static {
		configurationHadoopKarmaCluster = new HashMap<String,String>();
		configurationHadoopKarmaCluster.put("No Hadoop configuration available", "No Hadoop configuration available");
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@ModelAttribute("configuration")
	public BigLoupeConfiguration populateWebBigLoupeConfiguration() {
		return configuration;
	}

	@ModelAttribute("configurationHadoopKarmaCluster")
	public Map<String, String> populateHadoopKarmaCluster(ModelMap model) {
		if (configuration != null)
			return configuration.getHadoopClusters();
		else {
			return configurationHadoopKarmaCluster;
		}
	}

	/**
	 * Helper class to display a message in top header (<div id="message">
	 * available in main layout)
	 * 
	 * @param model
	 * @param message
	 */
	public void addMessage(ModelMap model, String message) {
		if (messages == null)
			messages = new ArrayList<String>();
		messages.add(message);
		model.addAttribute("messages", messages);
	}
}
