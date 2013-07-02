package org.bigloupe.web.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bigloupe.web.dto.ResponseStatus;
import org.bigloupe.web.scheduler.JobDescriptor;
import org.bigloupe.web.scheduler.JobManager;
import org.bigloupe.web.scheduler.job.builtin.IndexFileProcessJob;
import org.bigloupe.web.util.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller to launch job 2 jobs available :
 * 
 * - jobCompactorAvro : Compact all files in a directory with Map Reduce Job.
 * This job supports only Avro - jobFilterAvro : filter Avro files or directory
 * with Pig
 * 
 * @author bigloupe
 * 
 */
@Controller
@RequestMapping("/job")
public class JobLauncherController extends AbstractSchedulerController {

	private static Logger logger = LoggerFactory
			.getLogger(JobLauncherController.class);

	/**
	 * Compact all avro files in directory (request available in HDFS Browser)
	 * Path variable contains directory destination
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/compactorAvro/**", method = RequestMethod.GET)
	@ResponseBody
	protected ResponseStatus jobCompactorAvro(
			@RequestParam("targetDir") String targetDir, ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String uri = request.getContextPath() + request.getServletPath();
		uri = uri.substring(0, uri.length() - ".html".length());
		String dirToCompact = uri.substring("/job/compactorAvro".length());

		ResponseStatus responseStatus = new ResponseStatus();
		JobManager jobManager = getInitScheduler(servletContext)
				.getJobManager();

		// Property of this job
		Map<String, String> props = new HashMap<String, String>();
		props.put("type", "map-reduce");
		props.put("hadoop.cluster",
				configuration.getHadoopConfigurationCluster(request));
		props.put("classpath.ext", "./lib,./hadoop-client");
		props.put("jar", "lib/compactor-1.1.jar");

		props.put(
				"jar.args",
				"--input-format org.apache.avro.mapred.AvroInputFormat --avro-input-schema --output-format org.apache.avro.mapred.AvroOutputFormat --compress none --verbose --tmp-dir /user/karma/ "
						+ dirToCompact + " " + targetDir + " COMPACTED");

		String jobPath = "compactor";
		String jobName = "compactor_" + request.getLocalAddr();

		List<String> messages = new ArrayList<String>();
		JobDescriptor jobDescriptor;
		try {
			// Deploy Job with a new parameter (specific job by ip user)
			jobManager.deployJob(jobName, jobPath, new Props(null, props));
			jobDescriptor = jobManager.getJobDescriptor(jobName);
			getInitScheduler(servletContext).getJobExecutorManager().execute(
					jobDescriptor.getId(), false);

			messages.add(jobName + " has been deployed to " + jobPath);
		} catch (Exception e) {
			logger.error("Failed to run job '" + jobName
					+ " to compact directory '" + dirToCompact + "' in '"
					+ targetDir, e);
			responseStatus.setSuccess(false);
			responseStatus.setMessage("Failed to run job '" + jobName
					+ " to compact directory '" + dirToCompact + "' : "
					+ e.getMessage());
			return responseStatus;
		}
		responseStatus.setSuccess(true);
		responseStatus
				.setMessage("<span class=\"label label-important\"><a href=\""
						+ request.getContextPath()
						+ "/scheduler/jobDetail.html?id="
						+ jobDescriptor.getId() + "&logs"
						+ "\">Job</a> to compact directory '" + dirToCompact
						+ "' in <br/>'" + targetDir + "' has been launched</span>");

		return responseStatus;

	}

	/**
	 * Filter Avro files with Pig. Result available in target directory (request
	 * available in HDFS Browser) Path variable contains directory destination
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/filterAvro/**", method = RequestMethod.GET)
	@ResponseBody
	protected ResponseStatus jobFilterAvro(
			@RequestParam("targetDir") String targetDir,
			@RequestParam("filterParam") String filterParam,
			@RequestParam("filterValue") String filterValue, ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String uri = request.getContextPath() + request.getServletPath();
		uri = uri.substring(0, uri.length() - ".html".length());
		String fileToFilter = uri.substring("/job/filterAvro".length());

		ResponseStatus responseStatus = new ResponseStatus();
		JobManager jobManager = getInitScheduler(servletContext)
				.getJobManager();

		// Property of this job
		Map<String, String> props = new HashMap<String, String>();
		props.put("type", "pig");
		props.put("hadoop.cluster",
				configuration.getHadoopConfigurationCluster(request));
		props.put("pig.script", "filter.pig");

		props.put("param.fileToFilter", fileToFilter);
		props.put("param.fileFiltered", targetDir);
		props.put("param.filterParam", filterParam);
		props.put("param.filterValue", filterValue);

		String jobPath = "filter";
		String jobName = "filter_" + request.getLocalAddr();

		List<String> messages = new ArrayList<String>();
		JobDescriptor jobDescriptor;
		try {
			// Deploy Job with a new parameter (specific job by ip user)
			jobManager.deployJob(jobName, jobPath, new Props(null, props));
			jobDescriptor = jobManager.getJobDescriptor(jobName);
			getInitScheduler(servletContext).getJobExecutorManager().execute(
					jobDescriptor.getId(), false);

			messages.add(jobName + " has been deployed to " + jobPath);
		} catch (Exception e) {
			logger.error("Failed to run job '" + jobName
					+ " to filter directory '" + fileToFilter + "' in '"
					+ targetDir, e);
			responseStatus.setSuccess(false);
			responseStatus.setMessage("Failed to run job '" + jobName
					+ " to filter directory '" + fileToFilter + "' : "
					+ e.getMessage());
			return responseStatus;
		}
		responseStatus.setSuccess(true);
		responseStatus
				.setMessage("<span class=\"label label-important\"><a href=\""
						+ request.getContextPath()
						+ "/scheduler/jobDetail.html?id="
						+ jobDescriptor.getId() + "&logs"
						+ "\">Job</a> to filter directory '" + fileToFilter
						+ "' in <br/>'" + targetDir + "' has been launched</span>");

		return responseStatus;

	}
	

}
