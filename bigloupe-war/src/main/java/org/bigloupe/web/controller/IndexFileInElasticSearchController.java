package org.bigloupe.web.controller;

import java.io.IOException;
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
 * Index a file with ElasticSearch
 * 
 * @author bigloupe
 * 
 */
@Controller
public class IndexFileInElasticSearchController extends
		AbstractSchedulerController {

	private static Logger logger = LoggerFactory
			.getLogger(IndexFileInElasticSearchController.class);

	/**
	 * Return page to launch index Map/Reduce
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/indexFile", method = RequestMethod.GET)
	protected String indexFile(
			@RequestParam(value = "job_id", required = false) String jobId,
			HttpServletRequest request, ModelMap model)
			throws ServletException, IOException {
		if (jobId != null) {
			JobManager jobManager = getInitScheduler(servletContext)
					.getJobManager();
			Map<String, JobDescriptor> descriptors = jobManager
					.loadJobDescriptors();
			JobDescriptor jobDescriptor = descriptors.get(jobId);
			// Check if this job is a job to index Elastic Search
			if (jobDescriptor.getJobType().equals("indexfile")) {
				IndexFileProcessJob indexFileProcessJob = new IndexFileProcessJob(
						jobDescriptor);
				model.addAttribute("jobDescriptor", jobDescriptor);
				model.addAttribute("indexFileJob", indexFileProcessJob);
			}

		}
		return "indexfile";
	}

	/**
	 * Save job configuration to index a file in elasticsearch
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/indexFileSaveJob", method = RequestMethod.POST)
	protected String indexFileSaveJobConfiguration(
			@RequestParam(value = "jobName") String jobName,
			@RequestParam(value = "jobPath") String jobPath,
			@RequestParam(value = "jobType") String jobType,
			@RequestParam(value = IndexFileProcessJob.FILE_TO_INDEX) String fileToIndex,
			ModelMap model, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		JobManager jobManager = getInitScheduler(servletContext)
				.getJobManager();
		Map<String, String> props = new HashMap<String, String>();
		props.put("type", "indexfile");
		jobPath = "index_avro_file";
		jobName = "index_generic_avro_file_with_elasticsearch";
		props.put(IndexFileProcessJob.FILE_TO_INDEX, fileToIndex);
		props.put(IndexFileProcessJob.ELASTICSEARCH_DIRECTORY,
				configuration.getConfigurationElasticSearchDirectory());

		List<String> messages = new ArrayList<String>();
		try {
			jobManager.deployJob(jobName, jobPath, new Props(null, props));
			messages.add(jobName + " has been deployed to " + jobPath);

		} catch (Exception e) {
			logger.error("Failed to deploy job.", e);
			messages.add("Failed to deploy job: " + e.getMessage());
		}
		Map<String, JobDescriptor> descriptors = jobManager
				.loadJobDescriptors();
		JobDescriptor jobDescriptor = descriptors.get(jobName);

		model.addAttribute("messages", messages);
		IndexFileProcessJob indexFileProcessJob = new IndexFileProcessJob(
				jobDescriptor);
		model.addAttribute("jobDescriptor", jobDescriptor);
		model.addAttribute("indexFileJob", indexFileProcessJob);
		return "indexfile";

	}

	/**
	 * Index file in elasticsearch (request available in HDFS Browser)
	 * 
	 * @param folder
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/job/indexAvro/**", method = RequestMethod.GET)
	@ResponseBody
	protected ResponseStatus jobIndexAvro(@RequestParam("elasticSearchIndexName")String elasticSearchIndexName, ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String uri = request.getContextPath() + request.getServletPath();
		uri = uri.substring(0, uri.length() - ".html".length());
		String fileToIndex = uri.substring("/job/indexAvro".length());

		ResponseStatus responseStatus = new ResponseStatus();
		JobManager jobManager = getInitScheduler(servletContext)
				.getJobManager();

		// Property of this job
		Map<String, String> props = new HashMap<String, String>();
		props.put("type", "indexfile");
		props.put("hadoop.cluster",
				configuration.getHadoopConfigurationCluster(request));

		String jobPath = "index_avro_file";
		String jobName = "index_generic_avro_file_with_elasticsearch_"
				+ request.getLocalAddr();
		props.put(IndexFileProcessJob.FILE_TO_INDEX, fileToIndex);
		
		// All index in elasticsearch should be in lower case
		props.put(IndexFileProcessJob.INDEX, elasticSearchIndexName.toLowerCase());
		props.put(IndexFileProcessJob.ELASTICSEARCH_DIRECTORY,
				configuration.getConfigurationElasticSearchDirectory());

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
			logger.error("Failed to run job '" + jobName + " to index file '"
					+ fileToIndex + "'", e);
			responseStatus.setSuccess(false);
			responseStatus.setMessage("Failed to run job '" + jobName
					+ " to index file '" + fileToIndex + "' : "
					+ e.getMessage());
			return responseStatus;
		}
		responseStatus.setSuccess(true);
		responseStatus
				.setMessage("<span class=\"label label-important\"><a href=\""
						+ request.getContextPath()
						+ "/scheduler/jobDetail.html?id="
						+ jobDescriptor.getId() + "&logs"
						+ "\">Job</a> to index '" + fileToIndex + "' has been launched</span>");

		return responseStatus;

	}
	

}
