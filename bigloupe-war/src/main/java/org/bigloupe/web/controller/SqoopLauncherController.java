package org.bigloupe.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bigloupe.web.scheduler.JobDescriptor;
import org.bigloupe.web.scheduler.JobManager;
import org.bigloupe.web.scheduler.job.builtin.SqoopProcessJob;
import org.bigloupe.web.util.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class SqoopLauncherController extends AbstractSchedulerController
		implements ServletContextAware {
	private static Logger logger = LoggerFactory
			.getLogger(SqoopLauncherController.class);

	private ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@ModelAttribute("sqoopUsageList")
	public List<String> populateUsage() {
		List<String> sqoopUsageList = new ArrayList<String>();
		sqoopUsageList.add("import");
		sqoopUsageList.add("export");
		return sqoopUsageList;
	}

	@ModelAttribute("jdbcConnection")
	public Map<String, String> populateJdbcConnection() {
		return SqoopProcessJob.JDBC_CONNECTION;
	}

	/**
	 * Return page to launch sqoop Map/Reduce
	 * 
	 * @param pigOption
	 * @param request
	 * @param model
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/sqoop", method = RequestMethod.GET)
	protected String sqoopLauncher(
			@RequestParam(value = "job_id", required = false) String jobId,
			HttpServletRequest request, ModelMap model)
			throws ServletException, IOException {
		if (jobId != null) {
			JobManager jobManager = getInitScheduler(servletContext)
					.getJobManager();
			Map<String, JobDescriptor> descriptors = jobManager
					.loadJobDescriptors();
			JobDescriptor jobDescriptor = descriptors.get(jobId);
			// Check if this job is a SQOOP job
			if (jobDescriptor.getJobType().equals("sqoop")) {
				SqoopProcessJob sqoopProcessJob = new SqoopProcessJob(
						jobDescriptor);
				model.addAttribute("jobDescriptor", jobDescriptor);
				model.addAttribute("sqoopJob", sqoopProcessJob);
			}

		}
		return "sqoop";
	}

	/**
	 * Return list of parameters for SQOOP import or SQOOP export
	 * 
	 * @return
	 */
	@RequestMapping(value = "/sqoopParameters", method = RequestMethod.GET)
	public String sqoopParameters(@RequestParam(value = "usage") String usage,
			HttpServletRequest request, ModelMap model)
			throws ServletException, IOException {
		if (usage.equals("import"))
			return "forward:/WEB-INF/jsp/sqoop/sqoop-import.jsp";
		else
			return "forward:/WEB-INF/jsp/sqoop/sqoop-export.jsp";
	}

	/**
	 * AJAX Request : Return list of tables in HTML FIXME
	 */
	@RequestMapping(value = "/sqoopJdbcTables", method = RequestMethod.GET, produces = "text/html")
	@ResponseBody()
	protected String sqoopJdbcTables(
			@RequestParam(SqoopProcessJob.SQOOP_JDBCCONNECTION) String jdbcConnection,
			@RequestParam(SqoopProcessJob.SQOOP_JDBCUSER) String jdbcUser,
			@RequestParam(SqoopProcessJob.SQOOP_JDBCPASSWORD) String jdbcPassword) {
		// Connect with JDBC connection
		return "<option name=\"TOBEDONE\">TOBEDONE</option>";
	}

	/**
	 * Save Sqoop job
	 * 
	 * @param folder
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/sqoopSaveJob", method = RequestMethod.POST)
	protected String saveSqoopJob(
			@RequestParam(value = "jobName") String jobName,
			@RequestParam(value = "jobPath") String jobPath,
			@RequestParam(value = "jobType") String jobType,
			@RequestParam(value = SqoopProcessJob.SQOOP_USAGE) String usage,
			@RequestParam(value = SqoopProcessJob.SQOOP_JDBCCONNECTION) String jdbcConnection,
			@RequestParam(value = SqoopProcessJob.SQOOP_JDBCUSER) String jdbcUser,
			@RequestParam(value = SqoopProcessJob.SQOOP_JDBCPASSWORD) String jdbcPassword,
			@RequestParam(value = SqoopProcessJob.SQOOP_JDBCTABLE) String jdbcTable,
			@RequestParam(value = SqoopProcessJob.SQOOP_VERBOSE, required = false) String verbose,
			@RequestParam(value = SqoopProcessJob.SQOOP_JDBCWHERECLAUSE, required = false) String jdbcWhereClause,
			@RequestParam(value = SqoopProcessJob.SQOOP_HDFSTARGETDIR, required = false) String hdfsTargetDir,
			@RequestParam(value = SqoopProcessJob.SQOOP_DELETE_HDFS_TARGET_DIRECTORY, required = false) Boolean deleteHdfsTargetDir,
			@RequestParam(value = SqoopProcessJob.SQOOP_DELETE_JAVA_MAPPING_FILE, required = false) Boolean deleteJavaMappingFile,
			@RequestParam(value = SqoopProcessJob.SQOOP_AVRODATAFILE, required = false) Boolean avroDataFile,
			@RequestParam(value = SqoopProcessJob.SQOOP_NUMBERMAPPERS, required = false) Integer numberMappers,
			@RequestParam(value = SqoopProcessJob.SQOOP_AVSCJARFILE, required = false) MultipartFile avscJarFile,
			ModelMap model, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		JobManager jobManager = getInitScheduler(servletContext)
				.getJobManager();
		Map<String, String> props = new HashMap<String, String>();
		props.put("type", "sqoop");
		props.put(SqoopProcessJob.SQOOP_USAGE, usage);
		props.put(SqoopProcessJob.SQOOP_JDBCCONNECTION, jdbcConnection);
		props.put(SqoopProcessJob.SQOOP_JDBCUSER, jdbcUser);
		props.put(SqoopProcessJob.SQOOP_JDBCPASSWORD, jdbcPassword);
		props.put(SqoopProcessJob.SQOOP_JDBCTABLE, jdbcTable);
		if (verbose != null)
			props.put(SqoopProcessJob.SQOOP_VERBOSE,
					verbose.toString());
		else
			props.put(SqoopProcessJob.SQOOP_VERBOSE, "false");
		
		if (jdbcWhereClause != null)
			props.put(SqoopProcessJob.SQOOP_JDBCWHERECLAUSE, jdbcWhereClause);
		if (hdfsTargetDir != null)
			props.put(SqoopProcessJob.SQOOP_HDFSTARGETDIR, hdfsTargetDir);
		if (deleteHdfsTargetDir != null)
			props.put(SqoopProcessJob.SQOOP_DELETE_HDFS_TARGET_DIRECTORY, deleteHdfsTargetDir.toString());
		if (deleteJavaMappingFile != null)
			props.put(SqoopProcessJob.SQOOP_DELETE_JAVA_MAPPING_FILE, deleteJavaMappingFile.toString());
		if (avroDataFile != null)
			props.put(SqoopProcessJob.SQOOP_AVRODATAFILE,
					avroDataFile.toString());
		else
			props.put(SqoopProcessJob.SQOOP_AVRODATAFILE, "false");
		if (numberMappers != null)
			props.put(SqoopProcessJob.SQOOP_NUMBERMAPPERS,
					numberMappers.toString());
		if (avscJarFile != null) {
			props.put(SqoopProcessJob.SQOOP_AVSCJARFILE, avscJarFile.getOriginalFilename());
			// Import jar file
			File avscJarFileInTempDirectoy = new File(configuration
					.getTempDirectory(), avscJarFile.getOriginalFilename());
			if (!avscJarFileInTempDirectoy.exists())
				avscJarFile.transferTo(new File(configuration
					.getTempDirectory(), avscJarFile.getOriginalFilename()));
		}
		List<String> messages = new ArrayList<String>();
		try {
			jobManager.deployJob(jobName, jobPath, new Props(null, props));

			messages.add(jobName + " has been deployed to " + jobPath);
			model.addAttribute("messages", messages);
		} catch (Exception e) {
			logger.error("Failed to deploy job.", e);
			messages.add("Failed to deploy job: " + e.getMessage());
		}
		Map<String, JobDescriptor> descriptors = jobManager
				.loadJobDescriptors();
		JobDescriptor jobDescriptor = descriptors.get(jobName);

		model.addAttribute("messages", messages);
		SqoopProcessJob sqoopProcessJob = new SqoopProcessJob(jobDescriptor);
		model.addAttribute("jobDescriptor", jobDescriptor);
		model.addAttribute("sqoopJob", sqoopProcessJob);
		return "sqoop";

	}

}
