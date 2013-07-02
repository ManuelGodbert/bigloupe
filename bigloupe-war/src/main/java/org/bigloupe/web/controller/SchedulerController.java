package org.bigloupe.web.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.bigloupe.web.BigLoupeConfiguration;
import org.bigloupe.web.scheduler.InitializeScheduler;
import org.bigloupe.web.scheduler.JobDescriptor;
import org.bigloupe.web.scheduler.JobManager;
import org.bigloupe.web.scheduler.flow.ComposedExecutableFlow;
import org.bigloupe.web.scheduler.flow.ExecutableFlow;
import org.bigloupe.web.scheduler.flow.Flow;
import org.bigloupe.web.scheduler.flow.FlowExecutionHolder;
import org.bigloupe.web.scheduler.flow.FlowManager;
import org.bigloupe.web.scheduler.flow.Flows;
import org.bigloupe.web.scheduler.flow.IndividualJobExecutableFlow;
import org.bigloupe.web.scheduler.flow.MultipleDependencyExecutableFlow;
import org.bigloupe.web.scheduler.flow.WrappingExecutableFlow;
import org.bigloupe.web.scheduler.job.JobExecution;
import org.bigloupe.web.scheduler.job.JobExecutionException;
import org.bigloupe.web.scheduler.job.JobExecutorManager.ExecutingJobAndInstance;
import org.bigloupe.web.scheduler.job.Status;
import org.bigloupe.web.scheduler.workflow.flow.DagLayout;
import org.bigloupe.web.scheduler.workflow.flow.Dependency;
import org.bigloupe.web.scheduler.workflow.flow.FlowNode;
import org.bigloupe.web.scheduler.workflow.flow.SugiyamaLayout;
import org.bigloupe.web.util.GuiUtils;
import org.bigloupe.web.util.Props;
import org.bigloupe.web.util.Utils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Hours;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.joda.time.ReadablePeriod;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

/**
 * Main scheduler controller
 * 
 * - upload jobs
 * 
 * @author bigloupe
 * 
 */
@Controller
@RequestMapping("/scheduler")
public class SchedulerController extends AbstractSchedulerController implements
		InitializingBean, ServletContextAware {

	private static Logger logger = Logger.getLogger(SchedulerController.class);

	ServletContext servletContext;

	/**
	 * List of jobs
	 */
	private List<String> jobTypeList = new ArrayList<String>();

	/**
	 * Initialize scheduler
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		// Initialiase jobType list
		jobTypeList.add("command");
		jobTypeList.add("java");
		jobTypeList.add("pig");
		jobTypeList.add("map-reduce");
		jobTypeList.add("indexfile");
		jobTypeList.add("sqoop");
		jobTypeList.add("script");
		jobTypeList.add("ruby");
		jobTypeList.add("python");
		jobTypeList.add("noop");
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * Initialize value for all schedule page
	 */
	@ModelAttribute("initScheduler")
	public InitializeScheduler populateInitScheduler() {
		return getInitScheduler(servletContext);
	}

	/**
	 * Upload jobs
	 * 
	 */
	@RequestMapping(value = "/uploadJobs", method = RequestMethod.GET)
	protected String uploadJobScreen() throws ServletException, IOException {
		return "job-upload";
	}

	/**
	 * Upload jobs from Zip file
	 * 
	 * @param attachmentFile
	 * @param deployPath
	 * @param request
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/uploadJobs", method = RequestMethod.POST)
	protected String uploadJob(ModelMap model,
			@RequestParam("files") MultipartFile attachmentFile,
			@RequestParam("path") String deployPath,
			HttpServletResponse response, HttpServletRequest request)
			throws ServletException, IOException {
		if (!ServletFileUpload.isMultipartContent(request))
			throw new ServletException("No job file found!");

		List<String> messages = new ArrayList<String>();
		try {
			final JobManager jobManager = getInitScheduler(servletContext)
					.getJobManager();
			File jobDir = unzipFile(attachmentFile);
			jobManager.deployJobDir(jobDir.getAbsolutePath(), deployPath);
		} catch (Exception e) {
			logger.info("Installation failed.", e);
			messages.add("Installation failed in path '" + deployPath + "'. "
					+ e.getMessage());
			model.addAttribute("messages", messages);
			model.addAttribute("path", deployPath);
			return "job-upload";
		}
		if (deployPath.equals("")) {
			messages.add("Installation succeeded");
		} else
			messages.add("Installation succeeded in path " + deployPath);
		model.addAttribute("messages", messages);
		model.addAttribute("tab", "jobs");
		return index(model, request, response);
	}

	private File unzipFile(MultipartFile attachmentFile)
			throws ServletException, IOException {
		File temp = File.createTempFile("job-temp", ".zip");
		temp.deleteOnExit();
		OutputStream out = new BufferedOutputStream(new FileOutputStream(temp));
		IOUtils.copy(attachmentFile.getInputStream(), out);
		out.close();
		ZipFile zipfile = new ZipFile(temp);
		File unzipped = Utils.createTempDir(new File(getInitScheduler(
				servletContext).getTempDirectory()));
		Utils.unzip(zipfile, unzipped);
		temp.delete();
		return unzipped;
	}

	/**
	 * View Job Log
	 * 
	 * @param file
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/viewJobLog", method = RequestMethod.GET)
	protected void viewLogForFile(@RequestParam("file") String file,
			HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/plain");

		String fileName = getInitScheduler(servletContext).getLogDirectory()
				+ File.separator + file;
		Utils.viewFile(fileName, false, req, resp);
	}

	/**
	 * View Job console Log -- warning only one file per directory (not per job
	 * definition, not per job execution context)
	 * 
	 * @param file
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/viewJobConsoleLog", method = RequestMethod.GET)
	protected void viewJobConsoleLogForJobId(
			@RequestParam("jobId") String jobId, HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/plain");
		JobManager jobManager = getInitScheduler(servletContext)
				.getJobManager();
		Map<String, JobDescriptor> descriptors = jobManager
				.loadJobDescriptors();
		JobDescriptor jobDescriptor = descriptors.get(jobId);

		String fileName = servletContext.getRealPath(BigLoupeConfiguration
				.getSchedulerDirectory())
				+ System.getProperty("file.separator")
				+ "jobs"
				+ jobDescriptor.getPath()
				+ System.getProperty("file.separator")
				+ "logs"
				+ System.getProperty("file.separator") + "JobControl.log";

		Utils.viewFile(fileName, false, req, resp);
	}

	/**
	 * Main page for scheduler
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	protected String index(ModelMap model, HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {

		Map<String, JobDescriptor> descriptors = getInitScheduler(
				servletContext).getJobManager().loadJobDescriptors();
		model.addAttribute("logDir", getInitScheduler(servletContext)
				.getLogDirectory());
		model.addAttribute("flows", getInitScheduler(servletContext)
				.getAllFlows());
		model.addAttribute("scheduled", getInitScheduler(servletContext)
				.getScheduleManager().getSchedule());
		model.addAttribute("executing", getInitScheduler(servletContext)
				.getJobExecutorManager().getExecutingJobs());
		model.addAttribute("completed", getInitScheduler(servletContext)
				.getJobExecutorManager().getCompleted());
		model.addAttribute("rootJobNames", getInitScheduler(servletContext)
				.getAllFlows().getRootFlowNames());
		model.addAttribute("folderNames", getInitScheduler(servletContext)
				.getAllFlows().getFolders());
		model.addAttribute("jobDescComparator", JobDescriptor.NAME_COMPARATOR);
		return "scheduler";
	}

	/**
	 * Load list of jobs available
	 * 
	 * @param folder
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/loadJobs", method = RequestMethod.POST)
	protected void loadJobs(@RequestParam("folder") String folder,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		List<String> rootJobs = getInitScheduler(servletContext).getAllFlows()
				.getRootNamesByFolder(folder);
		Collections.sort(rootJobs);

		JSONArray rootJobObj = new JSONArray();
		for (String root : rootJobs) {
			Flow flow = getInitScheduler(servletContext).getAllFlows().getFlow(
					root);
			JSONObject flowObj = getJSONDependencyTree(flow);
			rootJobObj.add(flowObj);
		}

		response.getWriter().write(rootJobObj.toJSONString());
	}

	@SuppressWarnings("unchecked")
	private JSONObject getJSONDependencyTree(Flow flow) {
		JSONObject jobObject = new JSONObject();
		jobObject.put("name", flow.getName());

		if (flow.hasChildren()) {
			JSONArray dependencies = new JSONArray();
			for (Flow child : flow.getChildren()) {
				JSONObject childObj = getJSONDependencyTree(child);
				dependencies.add(childObj);
			}

			Collections.sort(dependencies, new FlowComparator());
			jobObject.put("dep", dependencies);
		}

		return jobObject;
	}

	private class FlowComparator implements Comparator<JSONObject> {

		@Override
		public int compare(JSONObject arg0, JSONObject arg1) {
			String first = (String) arg0.get("name");
			String second = (String) arg1.get("name");
			return first.compareTo(second);
		}

	}

	/**
	 * View job in details
	 * 
	 * @param folder
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/jobDetail", method = RequestMethod.GET)
	protected String jobDetail(@RequestParam("id") String jobId,
			ModelMap modelMap, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		JobManager jobManager = getInitScheduler(servletContext)
				.getJobManager();
		Map<String, JobDescriptor> descriptors = jobManager
				.loadJobDescriptors();
		JobDescriptor jobDescriptor = descriptors.get(jobId);
		List<JobExecution> execs = jobManager.loadJobExecutions(jobId);
		int successes = 0;
		for (JobExecution exec : execs)
			if (exec.isSucceeded())
				successes++;
		modelMap.addAttribute("job", jobDescriptor);
		modelMap.addAttribute("descriptors", descriptors);
		modelMap.addAttribute("jsonData", getJSONText(jobDescriptor.getProps()));
		if (request.getParameter("logs") != null) {
			modelMap.addAttribute("tab", "logs");
		}
		modelMap.addAttribute("executions", execs);
		modelMap.addAttribute("successful_executions", successes);
		return "job-detail";

	}

	@SuppressWarnings("unchecked")
	private String getJSONText(Props prop) {
		JSONObject jsonObj = new JSONObject();
		for (String key : prop.localKeySet()) {
			Object value = prop.get(key);
			jsonObj.put(key, value);
		}

		return jsonObj.toJSONString();
	}

	/**
	 * Create job
	 * 
	 * @param folder
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/createJob", method = RequestMethod.GET)
	protected String createJob(ModelMap modelMap, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		modelMap.addAttribute("jobTypeList", jobTypeList);
		modelMap.addAttribute("jsonData", getJSONText(new Props()));
		return "job-edit";
	}

	/**
	 * Edit job in detail
	 * 
	 * @param folder
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/editJob", method = RequestMethod.GET)
	protected String editJob(@RequestParam("id") String jobId,
			ModelMap modelMap, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		JobManager jobManager = getInitScheduler(servletContext)
				.getJobManager();
		Map<String, JobDescriptor> descriptors = jobManager
				.loadJobDescriptors();
		JobDescriptor jobDescriptor = descriptors.get(jobId);
		modelMap.addAttribute("job", jobDescriptor);
		modelMap.addAttribute("jobTypeList", jobTypeList);
		modelMap.addAttribute("jsonData", getJSONText(jobDescriptor.getProps()));
		return "job-edit";

	}

	/**
	 * Save job
	 * 
	 * @param folder
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/saveJob", method = RequestMethod.POST)
	protected String saveJob(@RequestParam("jobName") String jobName,
			@RequestParam("jobPath") String jobPath,
			@RequestParam("jobType") String jobType, ModelMap modelMap,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		JobManager jobManager = getInitScheduler(servletContext)
				.getJobManager();
		Map<String, String> props = new HashMap<String, String>();
		boolean isPigFromPigLauncher = false;
		for (int i = 0;; i++) {
			String key = request.getParameter("key" + i);
			String val = request.getParameter("val" + i);

			// Try to retrieve variables from pig launcher if key null
			if (key == null) {
				key = request
						.getParameter("scriptPigVariables[" + i + "].name");
				val = request.getParameter("scriptPigVariables[" + i
						+ "].value");
				if (key != null) {
					// Add param as suffix
					if (key.indexOf('$') != -1)
						key = key.substring(key.indexOf('$') + 1, key.length());
					key = "param." + key;

					isPigFromPigLauncher = true;
				}
			}

			if (key == null || val == null)
				break;
			if (key.length() > 0)
				props.put(key, val);
		}

		// Type of Jobs (could be pig, java, sqoop, ....)
		props.put("type", jobType);

		// Add additional properties when saving job from Pig launcher
		if (isPigFromPigLauncher) {
			props.put("pig.script", request.getParameter("scriptPig"));
			// Copy pigScript in jobPath
			File jobPathFile = new File(
					servletContext.getRealPath(configuration
							.getSchedulerDirectory() + "/jobs/" + jobPath));
			jobPathFile.mkdir();
			FileUtils.copyFileToDirectory(
					new File(servletContext.getRealPath(configuration
							.getPigScriptDirectory()), request
							.getParameter("scriptPig")), jobPathFile);
		}
		List<String> messages = new ArrayList<String>();
		try {
			jobManager.deployJob(jobName, jobPath, new Props(null, props));

			messages.add(jobName + " has been deployed to " + jobPath);
			modelMap.addAttribute("messages", messages);
		} catch (Exception e) {
			logger.error("Failed to deploy job.", e);
			messages.add("Failed to deploy job: " + e.getMessage());
		}
		Map<String, JobDescriptor> descriptors = jobManager
				.loadJobDescriptors();
		JobDescriptor jobDescriptor = descriptors.get(jobName);

		modelMap.addAttribute("messages", messages);
		modelMap.addAttribute("job", jobDescriptor);
		modelMap.addAttribute("jobTypeList", jobTypeList);
		modelMap.addAttribute("jsonData", getJSONText(jobDescriptor.getProps()));
		return "job-edit";

	}

	/**
	 * Run job
	 * 
	 * @param folder
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/runJob", method = RequestMethod.POST)
	@ResponseBody
	protected HashMap<String, Object> runJob(@RequestParam("id") String jobId,
			@RequestParam("include_deps") Boolean includeDeps,
			ModelMap modelMap, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");

		HashMap<String, Object> results = new HashMap<String, Object>();
		try {
			// JobDescriptor jobDescriptor =
			// getInitScheduler(servletContext).getJobManager().getJobDescriptor(jobId);

			getInitScheduler(servletContext).getJobExecutorManager().execute(
					jobId, includeDeps);
			broadcastLogViewer(jobId, request);
			results.put("success", "Running "
					+ jobId
					+ (includeDeps ? " without dependencies."
							: " with dependencies."));
		} catch (JobExecutionException e) {
			results.put("error", e.getMessage());
		}
		return results;
	}

	/**
	 * Boradcast with Atmosphere all log viewers
	 * 
	 * Used by /runJob and /scheduleJob
	 * 
	 * @param jobId
	 * @param request
	 * @param broadcaster
	 * @throws IOException
	 */
	private void broadcastLogViewer(String jobId, HttpServletRequest request)
			throws IOException {
		// Prepare broadcaster to send message concerning scheduled job
		Broadcaster broadcaster = BroadcasterFactory.getDefault().lookup(
				"jobDetailLog", true);
		JobManager jobManager = getInitScheduler(servletContext)
				.getJobManager();
		List<JobExecution> jobExecutions = jobManager.loadJobExecutions(jobId);
		if (!jobExecutions.isEmpty()) {
			JobExecution jobExecution = jobExecutions.get(0);
			broadcaster
					.broadcast("<tr><td><span class=\"label label-important\">"
							+ jobId
							+ "</span></td><td>"
							+ GuiUtils.formatDateTimeAndZone(jobExecution
									.getStarted())
							+ "</td><td>-</td><td>-</td><td>-</td><td>-</td></tr>");
		}
	}

	/**
	 * Schedule Job
	 */
	@RequestMapping(value = "/scheduleJob", method = RequestMethod.POST)
	protected String scheduleJob(
			@RequestParam(value = "jobs", required = false) String[] jobNames,
			@RequestParam(value = "flow_now", required = false) String flowNow,
			@RequestParam(value = "period", required = false) Integer period,
			@RequestParam(value = "period_units", required = false) String periodUnits,
			@RequestParam(value = "hour", required = false) Integer hour,
			@RequestParam(value = "minutes", required = false) Integer minutes,
			@RequestParam(value = "am_pm", required = false) String am_pm,
			@RequestParam(value = "date", required = false) String scheduledDate,
			HttpServletResponse response, HttpServletRequest request,
			ModelMap modelMap) throws ServletException, IOException {
		List<String> messages = new ArrayList<String>();
		if (jobNames == null) {
			modelMap.addAttribute("tab", "jobs");
			messages.add("You must select at least one job to run.");
			modelMap.addAttribute("messages", messages);
			return "scheduler";
		}
		String redirectUrl;
		if (flowNow != null) {
			if (jobNames.length > 1) {
				messages.add("Can only run flow instance on one job.");
				modelMap.addAttribute("messages", messages);
				return "scheduler";
			}

			String jobName = jobNames[0];
			JobManager jobManager = this.getInitScheduler(servletContext)
					.getJobManager();
			JobDescriptor descriptor = jobManager.getJobDescriptor(jobName);
			if (descriptor == null) {
				messages.add("Can only run flow instance on one job.");
				modelMap.addAttribute("messages", messages);
				return "scheduler";
			} else {
				redirectUrl = request.getContextPath() + "/flow?job_id="
						+ jobName;
			}
		} else {
			for (String job : jobNames) {
				// Prepare broadcaster to send message concerning scheduled job
				Broadcaster broadcaster = BroadcasterFactory.getDefault()
						.lookup("/", true);
				if (hasParam(request, "schedule")) {
					DateTime day = null;
					if (scheduledDate == null
							|| scheduledDate.trim().length() == 0) {
						day = new LocalDateTime().toDateTime();
					} else {
						try {
							day = DateTimeFormat.forPattern("MM-dd-yyyy")
									.parseDateTime(scheduledDate);
						} catch (IllegalArgumentException e) {
							messages.add("Invalid date: '" + scheduledDate
									+ "'");
							modelMap.addAttribute("messages", messages);
							return "scheduler";
						}
					}

					ReadablePeriod thePeriod = null;
					if (hasParam(request, "is_recurring"))
						thePeriod = parsePeriod(period, periodUnits);

					boolean isPm = am_pm.equalsIgnoreCase("pm");
					if (isPm && hour < 12)
						hour += 12;
					hour %= 24;

					getInitScheduler(servletContext).getScheduleManager()
							.schedule(
									job,
									day.withHourOfDay(hour)
											.withMinuteOfHour(minutes)
											.withSecondOfMinute(0), thePeriod,
									false);
					modelMap.addAttribute("tab", "scheduled");
					messages.add(job + " scheduled.");
				} else if (hasParam(request, "run_now")) {
					boolean ignoreDeps = !hasParam(request, "include_deps");
					try {
						getInitScheduler(servletContext)
								.getJobExecutorManager().execute(job,
										ignoreDeps);
					} catch (JobExecutionException e) {
						messages.add(e.getMessage());
						redirectUrl = "";
					}
					broadcastLogViewer(job, request);
					messages.add("Running " + job);
				} else {
					messages.add("Neither run_now nor schedule param is set.");
				}
			}
			modelMap.addAttribute("messages", messages);
			return "scheduler";
		}

		if (messages.size() > 0)
			modelMap.addAttribute("messages", messages);
		if (!redirectUrl.isEmpty()) {
			return "redirect:/" + redirectUrl;
		} else
			return "scheduler";
	}

	// Used by schedule method
	private ReadablePeriod parsePeriod(int period, String periodUnits)
			throws ServletException {
		if ("d".equals(periodUnits))
			return Days.days(period);
		else if ("h".equals(periodUnits))
			return Hours.hours(period);
		else if ("m".equals(periodUnits))
			return Minutes.minutes(period);
		else if ("s".equals(periodUnits))
			return Seconds.seconds(period);
		else
			throw new ServletException("Unknown period unit: " + periodUnits);
	}

	/**
	 * Unschedule Job
	 */
	@RequestMapping(value = "/unscheduleJob", method = RequestMethod.POST)
	protected String unscheduleJob(@RequestParam("id") String jobId,
			HttpServletResponse response, HttpServletRequest request,
			ModelMap modelMap) throws ServletException, IOException {
		this.getInitScheduler(servletContext).getScheduleManager()
				.removeScheduledJob(jobId);
		modelMap.addAttribute("tab", "scheduled");
		List<String> messages = new ArrayList<String>();
		messages.add("Job " + jobId + " has been unscheduled");
		modelMap.addAttribute("messages", messages);
		return "scheduler";
	}

	/**
	 * Stop Job
	 */
	@RequestMapping(value = "/cancelJob", method = RequestMethod.POST)
	protected String cancelJob(@RequestParam("id") String jobId,
			HttpServletResponse response, HttpServletRequest request,
			ModelMap modelMap) throws Exception {
		this.getInitScheduler(servletContext).getJobExecutorManager()
				.cancel(jobId);

		List<String> messages = new ArrayList<String>();
		Collection<ExecutingJobAndInstance> executing = this
				.getInitScheduler(servletContext).getJobExecutorManager()
				.getExecutingJobs();
		for (ExecutingJobAndInstance curr : executing) {
			ExecutableFlow flow = curr.getExecutableFlow();
			final String flowId = flow.getId();
			if (flowId.equals(jobId)) {
				final String flowName = flow.getName();
				try {
					if (flow.cancel()) {
						messages.add("Cancelled " + flowName);
						logger.info("Job '" + flowName
								+ "' cancelled from gui.");
					} else {
						logger.info("Couldn't cancel flow '" + flowName
								+ "' for some reason.");
						messages.add("Failed to cancel flow " + flowName + ".");
					}
				} catch (Exception e) {
					logger.error("Exception while attempting to cancel flow '"
							+ flowName + "'.", e);
					messages.add("Failed to cancel flow " + flowName + ": "
							+ e.getMessage());
				}
			}
		}
		modelMap.addAttribute("tab", "scheduled");
		return "scheduler";
	}

	/**
	 * View flow from a job
	 */
	@RequestMapping(value = "/flow", method = RequestMethod.GET)
	protected String viewFlowFromJobList(@RequestParam("job_id") String jobId,
			ModelMap modelMap, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		final FlowManager allFlows = this.getInitScheduler(servletContext)
				.getAllFlows();
		ExecutableFlow flow = allFlows.createNewExecutableFlow(jobId);
		modelMap.addAttribute("id", "0");
		modelMap.addAttribute("name", jobId);

		if (flow == null) {
			// addError(req, "Job " + jobID + " not found.");
			return "";
		}

		org.bigloupe.web.scheduler.workflow.Flow displayFlow = new org.bigloupe.web.scheduler.workflow.Flow(
				flow.getName(), (Props) null);
		fillFlow(displayFlow, flow);
		displayFlow.validateFlow();

		String flowJSON = createJsonFlow(displayFlow);
		modelMap.addAttribute("jsonflow", flowJSON);
		modelMap.addAttribute("action", "run");
		modelMap.addAttribute("joblist", createJsonJobList(displayFlow));
		return "view-flow";
	}

	/**
	 * View flow from flow history
	 * 
	 */
	@RequestMapping(value = "/flowId", method = RequestMethod.GET)
	protected String viewFlowFromHistory(@RequestParam("flow_id") Long flowId,
			ModelMap modelMap, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		final FlowManager allFlows = this.getInitScheduler(servletContext)
				.getAllFlows();
		FlowExecutionHolder holder = allFlows.loadExecutableFlow(flowId);
		ExecutableFlow executableFlow = holder.getFlow();

		// This will be used the other
		org.bigloupe.web.scheduler.workflow.Flow displayFlow = new org.bigloupe.web.scheduler.workflow.Flow(
				executableFlow.getName(), (Props) null);
		fillFlow(displayFlow, executableFlow);
		displayFlow.validateFlow();

		String flowJSON = createJsonFlow(displayFlow);

		if (executableFlow.getStartTime() != null) {
			modelMap.addAttribute("startTime", executableFlow.getStartTime());
			if (executableFlow.getEndTime() != null) {
				modelMap.addAttribute("endTime", executableFlow.getEndTime());
				modelMap.addAttribute("period",
						new Duration(executableFlow.getStartTime(),
								executableFlow.getEndTime()).toPeriod());
			} else {
				modelMap.addAttribute("period",
						new Duration(executableFlow.getStartTime(),
								new DateTime()).toPeriod());
			}
		}

		modelMap.addAttribute("id", flowId);
		modelMap.addAttribute("showTimes", true);
		modelMap.addAttribute("name", executableFlow.getName());
		modelMap.addAttribute("jsonflow", flowJSON);
		modelMap.addAttribute("action", "restart");
		modelMap.addAttribute("joblist", createJsonJobList(displayFlow));
		return "view-flow";
	}

	/**
	 * View subflow
	 * 
	 * @param folder
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/subflow", method = RequestMethod.POST)
	protected String viewFlow(@RequestParam("id") Long id, ModelMap modelMap,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		final FlowManager allFlows = this.getInitScheduler(servletContext)
				.getAllFlows();
		FlowExecutionHolder holder = allFlows.loadExecutableFlow(id);
		ExecutableFlow executableFlow = holder.getFlow();

		// This will be used the other
		org.bigloupe.web.scheduler.workflow.Flow displayFlow = new org.bigloupe.web.scheduler.workflow.Flow(
				executableFlow.getName(), (Props) null);
		fillFlow(displayFlow, executableFlow);
		displayFlow.validateFlow();

		String flowJSON = createJsonFlow(displayFlow);

		modelMap.addAttribute("jsonflow", flowJSON);
		modelMap.addAttribute("id", id);

		if (executableFlow.getStartTime() != null) {
			modelMap.addAttribute("startTime", executableFlow.getStartTime());

			if (executableFlow.getEndTime() != null) {
				modelMap.addAttribute("endTime", executableFlow.getEndTime());
				modelMap.addAttribute("period",
						new Duration(executableFlow.getStartTime(),
								executableFlow.getEndTime()).toPeriod());
			} else {
				modelMap.addAttribute("period",
						new Duration(executableFlow.getStartTime(),
								new DateTime()).toPeriod());
			}
		}

		modelMap.addAttribute("showTimes", true);
		modelMap.addAttribute("name", executableFlow.getName());
		modelMap.addAttribute("action", "restart");
		modelMap.addAttribute("joblist", createJsonJobList(displayFlow));
		return "view-flow";

	}

	private void fillFlow(org.bigloupe.web.scheduler.workflow.Flow displayFlow,
			ExecutableFlow executableFlow) {
		List<String> dependencies = new ArrayList<String>();
		for (ExecutableFlow depFlow : executableFlow.getChildren()) {
			dependencies.add(depFlow.getName());
			fillFlow(displayFlow, depFlow);
		}

		displayFlow.addDependencies(executableFlow.getName(), dependencies);
		displayFlow.setStatus(executableFlow.getName(),
				getStringStatus(executableFlow.getStatus()));
	}

	private String getStringStatus(Status status) {
		switch (status) {
		case COMPLETED:
			return "completed";
		case FAILED:
			return "failed";
		case SUCCEEDED:
			return "succeeded";
		case RUNNING:
			return "running";
		case READY:
			return "ready";
		case IGNORED:
			return "disabled";
		}
		return "normal";
	}

	@SuppressWarnings("unchecked")
	private String createJsonJobList(
			org.bigloupe.web.scheduler.workflow.Flow flow) {
		JSONArray jsonArray = new JSONArray();
		for (FlowNode node : flow.getFlowNodes()) {
			jsonArray.add(node.getAlias());
		}

		return jsonArray.toJSONString();
	}

	@SuppressWarnings("unchecked")
	private String createJsonFlow(org.bigloupe.web.scheduler.workflow.Flow flow) {
		JSONObject jsonFlow = new JSONObject();
		jsonFlow.put("flow_id", flow.getId());

		if (!flow.isLayedOut()) {
			DagLayout layout = new SugiyamaLayout(flow);
			layout.setLayout();
		}

		JSONArray jsonNodes = new JSONArray();

		for (FlowNode node : flow.getFlowNodes()) {
			JSONObject jsonNode = new JSONObject();
			jsonNode.put("name", node.getAlias());
			jsonNode.put("x", node.getX());
			jsonNode.put("y", node.getY());
			jsonNode.put("status", node.getStatus());
			jsonNodes.add(jsonNode);
		}

		JSONArray jsonDependency = new JSONArray();

		for (Dependency dep : flow.getDependencies()) {
			JSONObject jsonDep = new JSONObject();
			jsonDep.put("dependency", dep.getDependency().getAlias());
			jsonDep.put("dependent", dep.getDependent().getAlias());
			jsonDependency.add(jsonDep);
		}

		jsonFlow.put("nodes", jsonNodes);
		jsonFlow.put("timestamp", flow.getLastModifiedTime());
		jsonFlow.put("layouttimestamp", flow.getLastLayoutModifiedTime());
		jsonFlow.put("dependencies", jsonDependency);

		return jsonFlow.toJSONString();
	}

	/**
	 * Run flow
	 * 
	 * @param folder
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/flow", method = RequestMethod.POST)
	@ResponseBody
	protected HashMap<String, Object> runFlow(@RequestParam("id") String id,
			@RequestParam("name") String name,
			@RequestParam("disabled") String value, ModelMap modelMap,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		final FlowManager allFlows = getInitScheduler(servletContext)
				.getAllFlows();
		String[] disabledValues = value.split(",");
		HashSet<String> disabledJobs = new HashSet<String>();
		for (String disabled : disabledValues) {
			if (!disabled.isEmpty()) {
				disabledJobs.add(disabled);
			}
		}

		ExecutableFlow flow = allFlows.createNewExecutableFlow(name);
		HashMap<String, Object> results = new HashMap<String, Object>();
		if (flow == null) {
			results.put("error", true);
			results.put("message", "Job " + name + " not found.");
		}
		traverseFlow(disabledJobs, flow);

		try {
			this.getInitScheduler(servletContext).getJobExecutorManager()
					.execute(flow);
			results.put("success", true);
			results.put("message", String.format("Executing Flow[%s].", name));
			results.put("id", flow.getId());

		} catch (Exception e) {
			results.put("error", true);
			results.put("message", String.format(
					"Error running Flow[%s]. " + e.getMessage(), name));
		}
		return results;
	}

	private void traverseFlow(HashSet<String> disabledJobs, ExecutableFlow flow) {
		String name = flow.getName();
		flow.reset();
		if (flow instanceof IndividualJobExecutableFlow
				&& disabledJobs.contains(name)) {
			IndividualJobExecutableFlow individualJob = (IndividualJobExecutableFlow) flow;
			individualJob.setStatus(Status.IGNORED);
			System.out.println("ignore " + name);
		} else {
			if (flow instanceof ComposedExecutableFlow) {
				ExecutableFlow innerFlow = ((ComposedExecutableFlow) flow)
						.getDepender();
				traverseFlow(disabledJobs, innerFlow);
			} else if (flow instanceof MultipleDependencyExecutableFlow) {
				traverseFlow(disabledJobs,
						((MultipleDependencyExecutableFlow) flow)
								.getActualFlow());
			} else if (flow instanceof WrappingExecutableFlow) {
				traverseFlow(disabledJobs,
						((WrappingExecutableFlow) flow).getDelegateFlow());
			}

			for (ExecutableFlow childFlow : flow.getChildren()) {
				traverseFlow(disabledJobs, childFlow);
			}
		}

	}

	/**
	 * Execution history
	 * 
	 * @param folder
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/history", method = RequestMethod.GET)
	protected String executionHistory(ModelMap modelMap,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		final FlowManager allFlows = this.getInitScheduler(servletContext)
				.getAllFlows();

		List<String> messages = new ArrayList<String>();

		if (hasParam(request, "action")) {
			if ("restart".equals(getParam(request, "action"))
					&& hasParam(request, "id")) {
				try {
					long id = Long.parseLong(getParam(request, "id"));

					final FlowExecutionHolder holder = allFlows
							.loadExecutableFlow(id);

					if (holder == null) {
						messages.add(String.format("Unknown flow with id[%s]",
								id));
					} else {
						Flows.resetFailedFlows(holder.getFlow());
						this.getInitScheduler(servletContext)
								.getJobExecutorManager().execute(holder);

						messages.add(String.format("Flow[%s] restarted.", id));
					}
				} catch (NumberFormatException e) {
					messages.add(String.format(
							"Apparently [%s] is not a valid long.",
							getParam(request, "id")));
				} catch (JobExecutionException e) {
					messages.add("Error restarting " + getParam(request, "id")
							+ ". " + e.getMessage());
				}
			}
		}

		long currMaxId = allFlows.getCurrMaxId();

		String beginParam = request.getParameter("begin");
		int begin = beginParam == null ? 0 : Integer.parseInt(beginParam);

		String sizeParam = request.getParameter("size");
		int size = sizeParam == null ? 20 : Integer.parseInt(sizeParam);

		List<ExecutableFlow> execs = new ArrayList<ExecutableFlow>(size);
		for (int i = begin; i < begin + size; i++) {
			final FlowExecutionHolder holder = allFlows
					.loadExecutableFlow(currMaxId - i);
			ExecutableFlow flow = null;
			if (holder != null)
				flow = holder.getFlow();

			if (flow != null)
				execs.add(flow);
		}
		if (messages.size() > 0)
			modelMap.addAttribute("messages", messages);
		modelMap.addAttribute("executions", execs);
		modelMap.addAttribute("begin", begin);
		modelMap.addAttribute("size", size);

		return "job-history";

	}

	public String getParam(HttpServletRequest request, String name)
			throws ServletException {
		String p = request.getParameter(name);
		if (p == null || p.equals(""))
			throw new ServletException("Missing required parameter '" + name
					+ "'.");
		else
			return p;
	}

	public boolean hasParam(HttpServletRequest request, String param) {
		return request.getParameter(param) != null;
	}

}
