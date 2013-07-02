package org.bigloupe.web.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.bigloupe.web.BigLoupeConfiguration;
import org.bigloupe.web.dto.PairValue;
import org.bigloupe.web.dto.PigOption;
import org.bigloupe.web.dto.ResponseStatus;
import org.bigloupe.web.scheduler.JobDescriptor;
import org.bigloupe.web.scheduler.JobManager;
import org.bigloupe.web.scheduler.job.builtin.PigProcessJob;
import org.bigloupe.web.util.Props;
import org.bigloupe.web.util.VariableParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PigController extends AbstractSchedulerController {
	private static Logger logger = LoggerFactory.getLogger(PigController.class);

	@ModelAttribute("scriptPigList")
	public List<String> populateScriptPig() {
		List<String> scriptPig = new ArrayList<String>();
		try {
			String scriptPigPath = servletContext.getRealPath("pig/scripts");
			DirectoryScanner scanner = new DirectoryScanner();
			scanner.setIncludes(new String[] { "**/*.pig" });
			scanner.setBasedir(scriptPigPath);
			scanner.scan();
			String[] includedFiles = scanner.getIncludedFiles();
			for (String includedFile : includedFiles) {
				scriptPig.add(includedFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return scriptPig;
	}

	/**
	 * Return page to launch pig script
	 * 
	 * @param pigOption
	 * @param request
	 * @param model
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/pig", method = RequestMethod.GET)
	protected String pigConfiguration(
			@ModelAttribute("pigOption") PigOption pigOption,
			HttpServletRequest request, ModelMap model)
			throws ServletException, IOException {
		return "pig";
	}

	/**
	 * Launcher pig script
	 */
	@RequestMapping(value = "/pig", method = RequestMethod.POST)
	protected String launchAntPig(
			@ModelAttribute("pigOption") PigOption pigOption,
			HttpServletRequest request, HttpServletResponse response,
			BindingResult bindingResult, ModelMap model)
			throws ServletException, IOException {

		try {

			Project project = new Project();
			project.setBaseDir(new File(request.getSession()
					.getServletContext()
					.getRealPath(configuration.getPigDirectory())));
			project.setName("Pig launcher");
			project.setDefault("launch-pig");
			project.init();

			DefaultLogger consoleLogger = new DefaultLogger();
			OutputStream outputStream = new ByteArrayOutputStream();
			consoleLogger.setErrorPrintStream(new PrintStream(outputStream));
			consoleLogger.setOutputPrintStream(new PrintStream(outputStream));
			consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
			project.addBuildListener(consoleLogger);

			project.fireBuildStarted();

			Java javaTask = createJavaPigTask(request, pigOption, project);
			project.log("Launch Pig script");
			javaTask.init();
			int returnCode = javaTask.executeJava();
			Throwable caught = null;
			project.fireBuildFinished(caught);
			outputStream.flush();
			outputStream.close();
			model.addAttribute("output", outputStream);

			return "forward:/WEB-INF/jsp/pig/pig-result.jsp";
		} catch (Exception ex) {
			ex.printStackTrace();
			model.addAttribute("pigException", ex);
			return "forward:/WEB-INF/jsp/pig/pig-result.jsp";
		}
	}

	/**
	 * Create Java pig task
	 */
	private Java createJavaPigTask(HttpServletRequest request,
			PigOption pigOption, Project project) {
		Target target = new Target();
		target.setName("launch-pig");
		Java javaTask = new Java();
		javaTask.setTaskName("Pig");
		javaTask.setFork(true);
		javaTask.setFailonerror(true);
		javaTask.setClassname("org.apache.pig.Main");

		Argument argument = javaTask.createArg();
		Path classpath = preparePigArgument(pigOption, project, argument,
				request);

		// List of all PIG jars
		FileSet fileSet = new FileSet();
		fileSet.setDir(new File(request.getSession().getServletContext()
				.getRealPath(configuration.getPigLibDirectory())));
		fileSet.setIncludes("**/*.jar");

		classpath.addFileset(fileSet);

		javaTask.setClasspath(classpath);
		javaTask.setProject(project);
		target.addTask(javaTask);
		project.addTarget(target);
		return javaTask;
	}

	/**
	 * Prepare Pig argument
	 * 
	 * 
	 * @param pigOption
	 * @param project
	 * @param argument
	 * @return
	 */
	private Path preparePigArgument(PigOption pigOption, Project project,
			Argument argument, HttpServletRequest request) {
		String argumentLine = "";
		if (configuration.getCurrentHadoopCluster(request).equals("filesystem")) 
			argumentLine += " -x local";
		List<PairValue> scriptPigVariables = pigOption.getScriptPigVariables();
		for (PairValue pairValue : scriptPigVariables) {
			argumentLine += "-param " + pairValue.getName().substring(1) + "="
					+ pairValue.getValue() + " ";
		}

		argumentLine += " " + pigOption.getScriptPig();
		argument.setLine(argumentLine);
		Path classpath = new Path(project);

		// Add pathelement with cluster Hadoop configuration dir
		// Configuration directory for Hadoop /conf including core-site.xml,
		// hdfs-site.xml,

		if (!configuration.getCurrentHadoopCluster(request).equals("filesystem")) {
			Path hadoopConfigurationPath = new Path(project,
					configuration.getFullHadoopConfigurationCluster(request)
							+ System.getProperty("file.separator"));
			classpath.add(hadoopConfigurationPath);
		} 
		

		return classpath;
	}

	/**
	 * Return list of variables
	 * 
	 * @return
	 */
	@RequestMapping(value = "/pigVariables", method = RequestMethod.POST)
	public String scriptPigVariables(String scriptPig,
			HttpServletRequest request, ModelMap model)
			throws ServletException, IOException {
		List<PairValue> scriptPigVariables = new ArrayList<PairValue>();
		try {
			String scriptPigFile = servletContext.getRealPath("pig/scripts/"
					+ scriptPig);
			File pigFile = new File(scriptPigFile);
			if (pigFile.exists()) {
				scriptPigVariables = VariableParser.getListVariable(pigFile);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		PigOption pigOption = new PigOption();
		pigOption.setScriptPigVariables(scriptPigVariables);
		model.addAttribute("pigOption", pigOption);

		return "forward:/WEB-INF/jsp/pig/pig-variables.jsp";
	}

	/**
	 * Upload file in HDFS
	 * 
	 * @param attachmentFile
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/uploadPig", method = { RequestMethod.POST })
	public String uploadScriptlet(
			@RequestParam("file") MultipartFile attachmentFile, ModelMap model,
			HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		attachmentFile.transferTo(new File(servletContext
				.getRealPath(configuration.getPigScriptDirectory()),
				attachmentFile.getOriginalFilename()));
		model.addAttribute("pigOption", new PigOption());
		return "pig";

	}

	/**
	 * Edit pig file in web application
	 * 
	 * @param attachmentFile
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/editPig/{pigFileName}", method = { RequestMethod.GET })
	public String editScriptlet(@PathVariable String pigFileName,
			ModelMap model, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		File pigFile = new File(servletContext.getRealPath(configuration
				.getPigScriptDirectory()), pigFileName);
		return getPigContent(pigFile, model,
				configuration.getPigScriptDirectory(), pigFileName);

	}

	/**
	 * Save pig file
	 * 
	 * @param attachmentFile
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/savePig/{pigFileName}", method = { RequestMethod.POST })
	public String saveScriptlet(@PathVariable String pigFileName,
			String pigPath, String content, ModelMap model,
			HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		File pigFile = new File(servletContext.getRealPath(pigPath),
				pigFileName);
		return savePigContent(pigFile, content, model);
	}

	/**
	 * Edit pig file used in a Job in web application
	 * 
	 * @param attachmentFile
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/editPigInJob/{pigFileName}", method = { RequestMethod.GET })
	public String editScriptletUsedInJob(@PathVariable String pigFileName,
			@RequestParam(value = "path", required = true) String path,
			ModelMap model, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		path = path.replace(File.separator, "/");
		if (path.startsWith("/"))
			path = path.substring(1, path.length());
		String scriptletPath = BigLoupeConfiguration.getSchedulerDirectory()
				+ "/jobs/" + path;
		File pigFile = new File(servletContext.getRealPath(scriptletPath),
				pigFileName);
		return getPigContent(pigFile, model, scriptletPath, pigFileName);

	}

	/**
	 * Read pig script file to edit
	 * 
	 * @param pigFile
	 * @param model
	 * @param pigPath
	 * @param pigFileName
	 * @return
	 * @throws IOException
	 */
	private String getPigContent(File pigFile, ModelMap model, String pigPath,
			String pigFileName) throws IOException {
		String pigContent;
		if (!pigFile.exists()) {
			pigContent = "File not found in '" + pigFile.getName()
					+ "' not found in '" + pigFile.getAbsolutePath() + "'";
		} else
			pigContent = FileUtils.readFileToString(pigFile,
					Charset.defaultCharset());
		model.addAttribute("pigContent", pigContent);
		model.addAttribute("pigPath", pigPath);
		model.addAttribute("pigFileName", pigFileName);
		return "pig-edit";
	}

	/**
	 * Save pig file
	 * 
	 * @param pigFile
	 * @param content
	 * @param model
	 * @return
	 * @throws IOException
	 */
	private String savePigContent(File pigFile, String content, ModelMap model)
			throws IOException {
		try {
			FileUtils.writeStringToFile(pigFile, content,
					Charset.defaultCharset());
		} catch (IOException ioe) {
			content = "File '" + pigFile.getName() + "' can't be saved : "
					+ ioe.getMessage();
		}
		model.addAttribute("pigContent", content);
		return "pig-edit";
	}

	/**
	 * Explain pig file used in a Job in web application
	 * 
	 * @param attachmentFile
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/explainPigInJob", method = { RequestMethod.GET })
	@ResponseBody
	public ResponseStatus explainScriptletUsedInJob(
			@RequestParam("id") String jobId, ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ResponseStatus responseStatus = new ResponseStatus();

		JobManager jobManager = getInitScheduler(servletContext)
				.getJobManager();
		Map<String, JobDescriptor> descriptors = jobManager
				.loadJobDescriptors();
		JobDescriptor jobDescriptorPigToExplain = descriptors.get(jobId);
		Props propsPigToExplain = jobDescriptorPigToExplain.getProps();
		Map<String, String> paramPropertiesPigToExplain = propsPigToExplain
				.getMapByPrefix(PigProcessJob.PIG_PARAM_PREFIX);
		Set<Entry<String, String>> paramPropertiesPigToExplainEntries = paramPropertiesPigToExplain
				.entrySet();

		// Property of Pig Explain job
		Map<String, String> propsExplainJob = new HashMap<String, String>();
		propsExplainJob.put("type", "pig");
		// Local mode
		boolean unixSeparator = (System.getProperty("file.separator").equals(
				"/") ? true : false);
		String fullPath = jobDescriptorPigToExplain.getFullPath();
		String hadoopCluster = FilenameUtils.normalize(
				jobDescriptorPigToExplain.getProps().get("hadoop.home"),
				unixSeparator);
		hadoopCluster = hadoopCluster.substring(hadoopCluster.indexOf("hdfs"));
		propsExplainJob.put("hadoop.home", hadoopCluster);

		String jobPath = "explain_pig";
		String jobName = "explain_pig_" + request.getLocalAddr();
		String command = "explain -script "
				+ FilenameUtils.getFullPath(jobDescriptorPigToExplain
						.getFullPath())
				+ propsPigToExplain.getString(PigProcessJob.PIG_SCRIPT) + " ";
		int i = 0;
		for (Entry<String, String> entry : paramPropertiesPigToExplainEntries) {
			command += "-param " + entry.getKey() + "=" + entry.getValue();
			i++;
			if (i < paramPropertiesPigToExplainEntries.size())
				command += " ";
		}

		propsExplainJob.put(PigProcessJob.PIG_COMMAND, command + " -out "
				+ propsPigToExplain.getString(PigProcessJob.PIG_SCRIPT)
				+ "_explained.txt");
		JobDescriptor jobDescriptorExplainJob;
		try {
			// Deploy Job with a new parameter (specific job by ip user)
			jobManager.deployJob(jobName, jobPath, new Props(null,
					propsExplainJob));
			jobDescriptorExplainJob = jobManager.getJobDescriptor(jobName);
			getInitScheduler(servletContext).getJobExecutorManager().execute(
					jobDescriptorExplainJob.getId(), false);

			messages.add(jobName + " has been deployed to " + jobPath);
		} catch (Exception e) {
			logger.error(
					"Failed to run job '" + jobName + " to explain pig file '"
							+ jobDescriptorPigToExplain.getFullPath() + "'", e);
			responseStatus.setSuccess(false);
			responseStatus.setMessage("Failed to run job '" + jobName
					+ " to explain pig file '"
					+ jobDescriptorPigToExplain.getFullPath() + "' : "
					+ e.getMessage());
			return responseStatus;
		}
		return responseStatus;
	}

}
