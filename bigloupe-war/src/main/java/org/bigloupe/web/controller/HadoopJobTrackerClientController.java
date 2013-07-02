package org.bigloupe.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.hadoop.mapred.JobStatus;
import org.apache.log4j.Logger;
import org.bigloupe.jobclient.service.JobTrackerClientService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.ServletContextAware;

/**
 * Jobtracker controller
 * List all jobs
 * 
 */
@Controller
@RequestMapping("/jobtracker")
public class HadoopJobTrackerClientController extends AbstractHdfsController
		implements InitializingBean, ServletContextAware {

	private static Logger logger = Logger
			.getLogger(HadoopJobTrackerClientController.class);
	
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
	}

	/**
	 * Handle index page (home page)
	 */
	@RequestMapping("/history")
	public String showIndexPage(
			ModelMap model, HttpServletRequest request) {
		model.addAttribute("jobTracker", getJobTrackerClient(request));
		return "jobtracker";
	}

	/**
	 * List of current jobs
	 * 
	 * @param hadoopConfigurationCluster
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/jobs", method = RequestMethod.GET)
	@ResponseBody
	protected JobStatus[] listJobs(ModelMap model, HttpServletRequest request)
			throws Exception {

		return getJobTrackerClient(request).listJob();
	}

}
