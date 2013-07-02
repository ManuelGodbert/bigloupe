package org.bigloupe.web.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.util.Version;
import org.bigloupe.jobclient.service.JobTrackerClientService;
import org.bigloupe.web.dto.PairValue;
import org.bigloupe.web.dto.ResponseStatus;
import org.bigloupe.web.dto.ResultSearch;
import org.bigloupe.web.service.hadoop.StatusesIndexService;
import org.bigloupe.web.service.hadoop.StatusesIndexService.FileSystemScanned;
import org.bigloupe.web.util.lucene.LowercaseAnalyzer;
import org.bigloupe.web.util.lucene.LuceneUtil;
import org.bigloupe.web.viewer.HdfsAvroFileViewer;
import org.bigloupe.web.viewer.HdfsFileViewer;
import org.bigloupe.web.viewer.JsonSequenceFileViewer;
import org.bigloupe.web.viewer.TextFileViewer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 * A servlet that shows the filesystem contents
 * 
 */
@Controller
public class HdfsBrowserController extends AbstractHdfsController implements
		InitializingBean, ServletContextAware {

	private static Logger logger = Logger
			.getLogger(HdfsBrowserController.class);

	@Autowired
	StatusesIndexService statusesIndexService;

	private static final String CURRENTPATH = "CURRENTPATH";
	private static final String DOWNLOADSIZE = "DOWNLOADSIZE";

	/**
	 * List of all viewers
	 */
	private ArrayList<HdfsFileViewer> viewers = new ArrayList<HdfsFileViewer>();

	/**
	 * To search path in HDFS directories
	 */
	QueryParser queryParser;

	/**
	 * Default viewer will be a text viewer
	 */
	private HdfsFileViewer defaultViewer = new TextFileViewer();

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();

		Analyzer analyzer = new LowercaseAnalyzer();
		queryParser = new QueryParser(Version.LUCENE_35, "highlightedpath",
				analyzer);

		viewers.add(new HdfsAvroFileViewer());
		viewers.add(new JsonSequenceFileViewer());
	}

	@ModelAttribute("downloadSize")
	private Integer getDownloadSize(HttpServletRequest request) {
		Integer downloadSize = (Integer) request.getSession().getAttribute(
				DOWNLOADSIZE);
		if (downloadSize == null) {
			setDownloadSize(50, request);
			downloadSize = 50;
		}
		return downloadSize;
	}

	private void setDownloadSize(Integer downloadSize,
			HttpServletRequest request) {
		request.getSession().setAttribute(DOWNLOADSIZE, downloadSize);
	}

	private Path getCurrentPath(HttpServletRequest request) {
		String path = (String) request.getSession().getAttribute(CURRENTPATH);
		if (path == null)
			return null;
		else
			return (Path) new Path(path);
	}

	private void setCurrentPath(Path currentPath, HttpServletRequest request) {
		request.getSession().setAttribute(CURRENTPATH, currentPath.toString());
	}

	@ModelAttribute("configurationHadoopKarmaCluster")
	public Map<String, String> populateHadoopKarmaCluster() {
		return configuration.getHadoopClusters();
	}

	/**
	 * Create HadoopCluster
	 * 
	 */
	public Configuration createHadoopCluster(String fsDefaultName,
			String serverName, HttpServletRequest request) throws Exception {
		configuration.setHadoopConfigurationCluster(serverName, request);
		// Create directory and xml files
		File newHadoopClusterDir = new File(
				configuration.getFullHadoopConfigurationCluster(request));
		File hadoopClusterLocalDir = new File(
				servletContext.getRealPath(configuration
						.getHadoopConfigurationCluster("local")));
		FileUtils.copyDirectory(hadoopClusterLocalDir, newHadoopClusterDir);
		saveHdfsConfiguration(fsDefaultName, null, request);
		return configureHadoopCluster(request);
	}

	/**
	 * Edit Hadoop cluster with {cluster} in request path
	 * 
	 * @param hadoopConfigurationCluster
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/editHdfsConfiguration/{cluster}", method = RequestMethod.GET)
	protected String editHDFSConfiguration(
			@PathVariable("cluster") String cluster, ModelMap model,
			HttpServletRequest req) throws Exception {
		configuration.setHadoopConfigurationCluster(cluster, req);
		Configuration configurationHadoop = null;
		configurationHadoop = configureHadoopCluster(req);
		model.addAttribute("configurationHadoop", configurationHadoop);
		model.addAttribute("configuration", configuration);
		return "hdfschangeconfiguration";
	}

	/**
	 * Change Hadoop cluster with {cluster} in request path
	 * 
	 * @param hadoopConfigurationCluster
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/changeHdfsConfiguration/{cluster}", method = RequestMethod.GET)
	protected String changeHDFSConfiguration(
			@PathVariable("cluster") String cluster, ModelMap model,
			HttpServletRequest req) throws Exception {
		// Save configuration
		configuration.setHadoopConfigurationCluster(cluster, req);
		// Put the cluster in the session
		req.getSession().setAttribute("clusterHadoop", cluster);
		Configuration configurationHadoop = null;
		try {
			configurationHadoop = configureHadoopCluster(req);
			FileSystem fs = FileSystem.get(configurationHadoop);
			setFileSystem(fs, req);
			setJobTrackerClient(new JobTrackerClientService(fs.getConf()), req);
		} catch (UnknownHostException e) {
			// Unknown host : propose to change hdfsconfiguration
			List<String> messages = new ArrayList<String>();
			messages.add(e.getMessage());
			model.addAttribute("configurationHadoop", configurationHadoop);
			model.addAttribute("configuration", configuration);
			model.addAttribute("messages", messages);
			return "hdfschangeconfiguration";
		} catch (ConnectException e) {
			List<String> messages = new ArrayList<String>();
			messages.add(e.getMessage());
			model.addAttribute("configurationHadoop", configurationHadoop);
			model.addAttribute("messages", messages);
			return "hdfschangeconfiguration";
		}
		model.addAttribute("configurationHadoop", configurationHadoop);
		return "redirect:/" + cluster + "/hdfsBrowser.html";
	}

	/**
	 * Increase/Decrease downloadSize
	 * 
	 * @param file
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/hdfsAction/downloadSize/{size}", method = RequestMethod.GET)
	@ResponseBody
	protected ResponseStatus downloadSize(@PathVariable("size") String size,
			HttpServletRequest req, HttpServletResponse resp) {
		ResponseStatus responseStatus = new ResponseStatus();
		Integer sizeInt = Integer.parseInt(size);
		if (sizeInt > 100) {
			setDownloadSize(100, req);
		} else if (sizeInt < 0) {
			setDownloadSize(0, req);
		} else
			setDownloadSize(sizeInt, req);

		responseStatus.setMessage("" + getDownloadSize(req));
		responseStatus.setSuccess(true);
		return responseStatus;
	}

	/**
	 * Display information on HDFS cluster
	 * 
	 * @param model
	 * @param hadoopConfigurationCluster
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/hdfsConfiguration", method = RequestMethod.POST)
	protected String displayHDFSConfiguration(ModelMap model,
			String hadoopConfigurationCluster, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		model.addAttribute("fileSystem", getFileSystem(req));
		return "forward:/WEB-INF/jsp/hdfs-browser/hdfs-configuration.jsp";
	}

	@RequestMapping(value = { "/hdfsBrowser/**", "/hdfsBrowser" }, method = RequestMethod.GET)
	protected String displayFileOrDir(ModelMap model,
			HttpServletRequest request, HttpServletResponse resp)
			throws ServletException, IOException {
		return displayFileOrDir("", model, request, resp);
	}

	/**
	 * Display file content or directory content
	 */
	@RequestMapping(value = { "/{cluster}/hdfsBrowser/**",
			"/{cluster}/hdfsBrowser" }, method = RequestMethod.GET)
	protected String displayFileOrDir(@PathVariable("cluster") String cluster,
			ModelMap model, HttpServletRequest request, HttpServletResponse resp)
			throws ServletException, IOException {

		String uri = request.getContextPath() + request.getServletPath();
		uri = uri.substring(0, uri.length() - ".html".length());
		int clusterLength = 0;
		if (!cluster.isEmpty())
			clusterLength = cluster.length() + 1;
		String fsPath = uri.substring(clusterLength + "/hdfsBrowser".length());
		if ((fsPath != null) && (fsPath.length() == 0))
			fsPath = "/";

		// Put the cluster in the session
		request.getSession().setAttribute("clusterHadoop", cluster);

		if (logger.isDebugEnabled())
			logger.debug("path=" + fsPath);

		// Use cluster hdfs in url if exist
		if (!cluster.isEmpty()) {
			// Compare old configuration with current configuration
			String currentHadoopCluster = configuration
					.getCurrentHadoopCluster(request);
			if (!currentHadoopCluster.equals(cluster)) {
				// Save configuration
				configuration.setHadoopConfigurationCluster(cluster, request);
				Configuration configurationHadoop = null;
				try {
					configurationHadoop = configureHadoopCluster(request);
					setFileSystem(FileSystem.get(configurationHadoop), request);
				} catch (Exception e) {
					List<String> messages = new ArrayList<String>();
					messages.add(e.getMessage());
					model.addAttribute("configurationHadoop",
							configurationHadoop);
					model.addAttribute("messages", messages);
					return "hdfschangeconfiguration";
				}
			}
		}

		Path path = new Path(fsPath);
		return displayFileOrDirForPath(model, request, resp, path);
	}

	/**
	 * Display dir or file content for a specific path Return content in model
	 * 
	 * @param model
	 * @param req
	 * @param resp
	 * @param path
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	protected String displayFileOrDirForPath(ModelMap model,
			HttpServletRequest req, HttpServletResponse resp, Path path)
			throws ServletException, IOException {

		// if fileSystem not well initialized, forward screen to change hdfs
		// configuration
		if (getFileSystem(req) == null) {
			try {
				Configuration configurationHadoop = configureHadoopCluster(req);
				model.addAttribute("configurationHadoop", configurationHadoop);
				return "hdfschangeconfiguration";
			} catch (Exception e) {
				e.printStackTrace();
				return "hdfschangeconfiguration";
			}
		}
		// Save current path
		setCurrentPath(path, req);
		if (!getFileSystem(req).exists(path))
			throw new IllegalArgumentException(path.toUri().getPath()
					+ " does not exist.");
		else if (getFileSystem(req).isFile(path))
			return displayFile(model, req, resp, path);
		else if (getFileSystem(req).getFileStatus(path).isDir())
			return displayDir(req, resp, model, path);
		else
			throw new IllegalStateException(
					"It exists, it is not a file, and it is not a directory, what is it precious?");

	}

	/**
	 * Display file content or directory content from HDFS URL
	 */
	@RequestMapping(value = { "/hdfsGotoBrowser/**", "/hdfsGotoBrowser" }, method = {
			RequestMethod.POST, RequestMethod.GET })
	protected String displayFileOrDirFromHDFSUrl(ModelMap model,
			HttpServletRequest req, HttpServletResponse resp,
			@RequestParam("gotoHDFSPath") String gotoHDFSPath)
			throws ServletException, IOException {

		if ((gotoHDFSPath != null) && (gotoHDFSPath.length() == 0))
			gotoHDFSPath = "/";

		if (logger.isDebugEnabled())
			logger.debug("path=" + gotoHDFSPath);

		Path path;
		// Extract hdfs serveur from url
		// gotoHDFSPath=hdfs://server:port/path/subpath ....
		if (gotoHDFSPath.startsWith("hdfs:/")) {
			gotoHDFSPath = gotoHDFSPath.substring("hdfs:/".length());
			// gotoHDFSPath=server:port/path/subpath or
			// /server:port/path/subpath....
			String hdfsServer = null;
			String hdfsPort = null;
			if (gotoHDFSPath.contains(":")) {
				if (gotoHDFSPath.startsWith("/"))
					gotoHDFSPath = gotoHDFSPath.substring(1);
				hdfsServer = gotoHDFSPath.substring(0,
						gotoHDFSPath.indexOf(":"));
				hdfsPort = gotoHDFSPath.substring(
						gotoHDFSPath.indexOf(":") + 1,
						gotoHDFSPath.indexOf("/"));
				gotoHDFSPath = gotoHDFSPath.substring(
						gotoHDFSPath.indexOf("/"), gotoHDFSPath.length());
			}

			// Search server in available configuration
			if (hdfsServer != null) {
				Map<String, String> hadoopKarmaClusterMap = configuration
						.getHadoopClusters();
				String hadoopKarmaCluster = hadoopKarmaClusterMap
						.get(hdfsServer);
				// Try to found configuration with hostname in DNS inside
				// configuration defined with hostname
				if ((hadoopKarmaCluster == null) && (hdfsServer.contains("."))) {
					hadoopKarmaCluster = hadoopKarmaClusterMap.get(hdfsServer
							.substring(0, hdfsServer.indexOf(".")));
				}

				// Try to found configuration with hostname inside configuration
				// defined with DNS name
				Set<String> hadoopKarmaClusterNames = hadoopKarmaClusterMap
						.keySet();
				for (String hadoopKarmaClusterName : hadoopKarmaClusterNames) {
					if (hadoopKarmaClusterName.startsWith(hdfsServer)) {
						hadoopKarmaCluster = hadoopKarmaClusterMap
								.get(hadoopKarmaClusterName);
						break;
					}
				}

				if (hadoopKarmaCluster != null) {
					// Set new Hadoop configuration cluster
					try {
						// if currentHadoopCluster : nothing to do
						if (!hadoopKarmaCluster.equals(configuration
								.getCurrentHadoopCluster(req))) {
							configuration.setHadoopConfigurationCluster(
									hadoopKarmaCluster, req);
							Configuration configurationHadoop = configureHadoopCluster(req);
							setFileSystem(FileSystem.get(configurationHadoop),
									req);
						}
					} catch (Exception e) {
						e.printStackTrace();
						return "hdfschangeconfiguration";
					}
				} else {
					// Declare new configuration
					try {
						Configuration configurationHadoop = createHadoopCluster(
								"hdfs://" + hdfsServer + ":" + hdfsPort,
								hdfsServer, req);
						model.addAttribute("configurationHadoop",
								configurationHadoop);
						setFileSystem(FileSystem.get(configurationHadoop), req);
					} catch (Exception e) {
						e.printStackTrace();
						return "hdfschangeconfiguration";
					}
				}
			}
		}

		// gotoHDFSPath=path/subpath or /path/subpath
		if (!gotoHDFSPath.startsWith("/")) {
			gotoHDFSPath = "/" + gotoHDFSPath;
		}

		// gotoHDFSPath=/path/subpath ....
		path = new Path(gotoHDFSPath);

		if ((getFileSystem(req) == null) || (!getFileSystem(req).exists(path)))
			if (getCurrentPath(req) != null) {
				List<String> messages = new ArrayList<String>();
				messages.add("Path '" + path + "' doesn't exist");
				model.addAttribute("messages", messages);
				return displayDir(req, resp, model, getCurrentPath(req));
			} else {
				List<String> messages = new ArrayList<String>();
				messages.add("Path '" + path + "' doesn't exist");
				model.addAttribute("messages", messages);
				return "hdfsbrowser";
			}
		else if (getFileSystem(req).isFile(path))
			return displayFile(model, req, resp, path);
		else if (getFileSystem(req).getFileStatus(path).isDir())
			return displayDir(req, resp, model, path);
		else
			throw new IllegalStateException(
					"It exists, it is not a file, and it is not a directory, what is it precious?");
	}

	/**
	 * Delete file in HDFS
	 * 
	 * @param file
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/hdfsAction/delete", method = RequestMethod.POST)
	@ResponseBody
	protected ResponseStatus deleteFile(String file, HttpServletRequest req,
			HttpServletResponse resp) {
		ResponseStatus responseStatus = new ResponseStatus();
		try {
			Path fileToDelete = new Path(file);
			getFileSystem(req).delete(fileToDelete, true);
		} catch (Exception e) {
			e.printStackTrace();
			responseStatus.setSuccess(false);
			responseStatus.setMessage(e.getMessage());
			return responseStatus;
		}
		responseStatus.setMessage("File " + file + " deleted");
		responseStatus.setSuccess(true);
		return responseStatus;
	}

	/**
	 * Copy file in an another HDFS
	 * 
	 * @param file
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/hdfsAction/copy", method = RequestMethod.POST)
	@ResponseBody
	protected ResponseStatus copyFileToPath(String file,
			HttpServletRequest req, HttpServletResponse resp) {
		ResponseStatus responseStatus = new ResponseStatus();
		try {
			Path fileToCopy = new Path(file);
			// TODO

		} catch (Exception e) {
			e.printStackTrace();
			responseStatus.setSuccess(false);
			responseStatus.setMessage(e.getMessage());
			return responseStatus;
		}
		responseStatus.setMessage("File " + file + " deleted");
		responseStatus.setSuccess(true);
		return responseStatus;
	}

	/**
	 * Find block in HDFS
	 * 
	 * @param file
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/hdfsAction/blockFinder", method = RequestMethod.POST)
	@ResponseBody
	protected ResponseStatus blockFinder(String file, HttpServletRequest req,
			HttpServletResponse resp) {
		ResponseStatus responseStatus = new ResponseStatus();
		StringBuilder b = new StringBuilder();
		try {
			ClientProtocol namenode = DFSClient.createNamenode(getFileSystem(
					req).getConf());
			FileStatus[] fileStatuses = getFileSystem(req).globStatus(
					new Path(file));
			for (FileStatus fileStatus : fileStatuses) {
				if (!fileStatus.isDir()) {
					b.append("<p>FILE: ")
							.append(fileStatus.getPath().toString())
							.append("</p>");

					String path = fileStatus.getPath().toUri().getPath();
					LocatedBlocks blocks = namenode.getBlockLocations(path, 0,
							fileStatus.getLen());

					for (LocatedBlock block : blocks.getLocatedBlocks()) {
						b.setLength(0);
						b.append(block.getBlock());
						b.append(" - ");

						List<String> nodes = new ArrayList<String>();
						for (DatanodeInfo datanodeInfo : block.getLocations()) {
							nodes.add(datanodeInfo.name);
						}
						for (String node : nodes) {
							b.append(node + ",");

						}

					}

				}
				b.append("<p></p>");
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseStatus.setSuccess(false);
			responseStatus.setMessage(e.getMessage());
			return responseStatus;
		}
		responseStatus.setMessage("Block finder for file " + file + "<p>"
				+ b.toString() + "</p>");
		responseStatus.setSuccess(true);
		return responseStatus;
	}

	/**
	 * Merge directory in HDFS
	 * 
	 * @param file
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/hdfsAction/merge/**", method = RequestMethod.POST)
	@ResponseBody
	protected ResponseStatus merge(String file, HttpServletRequest req,
			HttpServletResponse resp) {
		ResponseStatus responseStatus = new ResponseStatus();
		StringBuilder b = new StringBuilder();
		String uri = req.getContextPath() + req.getServletPath();
		uri = uri.substring(0, uri.length() - ".html".length());
		String directoryDestination = uri.substring("/hdfsAction/merge"
				.length());
		boolean isMerged = false;
		try {
			boolean deleteSource = false;
			FileSystem srcFS = getFileSystem(req);
			FileSystem dstFS = getFileSystem(req);
			Path srcDir = new Path(file);
			Path dstFile = new Path(directoryDestination);
			Configuration conf = srcFS.getConf();
			// Don't add new characters between files to merge
			String addString = null;

			isMerged = FileUtil.copyMerge(srcFS, srcDir, dstFS, dstFile,
					deleteSource, conf, addString);
		} catch (Exception e) {
			e.printStackTrace();
			responseStatus.setSuccess(false);
			responseStatus.setMessage(e.getMessage());
			return responseStatus;
		}
		if (isMerged) {
			responseStatus.setMessage("Merge directory " + file + " in file "
					+ directoryDestination + "<p>" + b.toString() + "</p>");
		} else {
			responseStatus.setMessage("Directory " + file + " not merged. "
					+ directoryDestination + " is a directory ?");
		}
		responseStatus.setSuccess(true);
		return responseStatus;
	}

	/**
	 * Download file in HDFS
	 * 
	 * @param file
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/hdfsAction/download/**", method = RequestMethod.GET)
	protected ModelAndView downloadFile(HttpServletRequest req,
			HttpServletResponse resp) {

		String uri = req.getContextPath() + req.getServletPath();
		uri = uri.substring(0, uri.length() - ".html".length());
		String file = uri.substring("/hdfsAction/download".length());

		OutputStream output;
		try {
			// use registered viewers to show the file content
			boolean outputed = false;
			output = resp.getOutputStream();

			Path fileToDownload = new Path(file);
			resp.setHeader("Content-Disposition", "attachment; filename=\""
					+ fileToDownload.getName() + "\"");
			for (HdfsFileViewer viewer : viewers) {
				if (viewer.canReadFile(getFileSystem(req), fileToDownload)) {
					viewer.downloadFile(getFileSystem(req), fileToDownload,
							output, getDownloadSize(req));
					outputed = true;
					break; // don't need to try other viewers
				}
			}

			// use default text viewer
			if (!outputed) {
				if (defaultViewer.canReadFile(getFileSystem(req),
						fileToDownload)) {
					defaultViewer.downloadFile(getFileSystem(req),
							fileToDownload, output, getDownloadSize(req));
				} else {
					output.write(("Sorry, no viewer available for this file. ")
							.getBytes("UTF-8"));
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Return directory content (files and subdirectories)
	 */
	private String displayDir(HttpServletRequest req, HttpServletResponse resp,
			ModelMap model, Path path) throws IOException {
		List<Path> paths = new ArrayList<Path>();
		List<String> segments = new ArrayList<String>();
		Path curr = path;
		while (curr.getParent() != null) {
			paths.add(curr);
			segments.add(curr.getName());
			curr = curr.getParent();
		}

		Collections.reverse(paths);
		Collections.reverse(segments);

		model.addAttribute("paths", paths);
		model.addAttribute("segments", segments);
		model.addAttribute("subdirs", getFileSystem(req).listStatus(path)); // ???
																			// line

		return "hdfsbrowser";
	}

	/**
	 * Display file content
	 * 
	 * @param req
	 * @param resp
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private String displayFile(ModelMap model, HttpServletRequest req,
			HttpServletResponse resp, Path path) throws IOException {

		String startRecord = req.getParameter("startRecord");
		String endRecord = req.getParameter("endRecord");
		int startRecordInt = 1;
		if (startRecord != null) {
			startRecordInt = Integer.parseInt(startRecord);
		}
		int endRecordInt = 100;
		if (endRecord != null) {
			endRecordInt = Integer.parseInt(endRecord);
		}

		// OutputStream output = resp.getOutputStream();
		OutputStream output = new ByteArrayOutputStream();
		for (HdfsFileViewer viewer : viewers) {
			if (viewer.canReadFile(getFileSystem(req), path)) {
				model.addAttribute("startRecord", startRecordInt);
				model.addAttribute("cluster",
						configuration.getCurrentHadoopCluster(req));
				model.addAttribute("path", path);
				model.addAttribute("output", output);
				return viewer.displayFile(getFileSystem(req), path, output,
						startRecordInt, endRecordInt);
			}
		}

		// use default text viewer if not found better viewer
		if (defaultViewer.canReadFile(getFileSystem(req), path)) {
			model.addAttribute("cluster",
					configuration.getCurrentHadoopCluster(req));
			model.addAttribute("path", path);
			model.addAttribute("output", output);
			return defaultViewer.displayFile(getFileSystem(req), path, output,
					startRecordInt, endRecordInt);
		} else {
			output.write(("Sorry, no viewer available for this file. ")
					.getBytes("UTF-8"));
		}

		return "hdfsbrowser";
	}

	/**
	 * Display Avro schema content
	 */
	@RequestMapping(value = { "/hdfsAvroSchema/**", "/hdfsAvroSchema" }, method = RequestMethod.GET)
	protected String displayAvroSchema(ModelMap model, HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {

		String uri = req.getContextPath() + req.getServletPath();
		uri = uri.substring(0, uri.length() - ".html".length());
		String fsPath = uri.substring("/hdfsAvroSchema".length());
		if ((fsPath != null) && (fsPath.length() == 0))
			fsPath = "/";

		Path path = new Path(fsPath);
		HdfsAvroFileViewer hdfsAvroFileViewer = new HdfsAvroFileViewer();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		hdfsAvroFileViewer.displaySchema(getFileSystem(req), path, output);
		model.addAttribute("output", output);
		return "forward:/WEB-INF/jsp/hdfs-browser/hdfs-avro-schema.jsp";
	}

	/**
	 * Read an avro schema and display its field names
	 */
	@RequestMapping(value = "/hdfsAvroFields/**", method = RequestMethod.GET)
	@ResponseBody
	public List<PairValue> displayAvroFields(HttpServletRequest req)
			throws ServletException, IOException {
		String uri = req.getContextPath() + req.getServletPath();
		uri = uri.substring(0, uri.length() - ".html".length());
		String fsPath = uri.substring("/hdfsAvroSchema".length());
		if ((fsPath != null) && (fsPath.length() == 0))
			fsPath = "/";

		Path path = new Path(fsPath);
		HdfsAvroFileViewer hdfsAvroFileViewer = new HdfsAvroFileViewer();
		return hdfsAvroFileViewer.displayFields(getFileSystem(req), path);
	}

	/**
	 * Compare Avro schema between two files
	 */
	@RequestMapping(value = { "/hdfsAvroSchemaDiff/**", "/hdfsAvroSchemaDiff" }, method = RequestMethod.GET)
	protected String displayAvroSchemaDiff(
			@RequestParam(value = "fsPath1", required = false) String fsPath1,
			@RequestParam(value = "fsPath2", required = false) String fsPath2,
			ModelMap model, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Path path1 = null;
		Path path2 = null;
		model.addAttribute("cluster",
				configuration.getCurrentHadoopCluster(req));
		try {
			boolean error = false;
			List<String> messages = new ArrayList<String>();
			if ((fsPath1 == null) || (fsPath2 == null)
					|| (fsPath1.equals("") || (fsPath2.equals("")))) {
				messages.add("Avro file 1 & 2 are required");
				model.addAttribute("messages", messages);
				return "avroschemacompare";

			}
			path1 = new Path(fsPath1);
			if (!getFileSystem(req).exists(path1)) {
				error = true;
				messages.add("Path '" + path1 + "' doesn't exist");
			}
			path2 = new Path(fsPath2);
			if (!getFileSystem(req).exists(path2)) {
				error = true;
				messages.add("<br>Path '" + path2 + "' doesn't exist</br>");
			}
			if (error) {
				model.addAttribute("messages", messages);
				return "avroschemacompare";
			}

		} catch (Exception fnfe) {
			fnfe.printStackTrace();
		}
		HdfsAvroFileViewer hdfsAvroFileViewer = new HdfsAvroFileViewer();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		hdfsAvroFileViewer.displaySchemaDiff(getFileSystem(req), path1, path2,
				output);
		model.addAttribute("output", output);
		return "avroschemacompare";
	}

	/**
	 * Display Avro codec
	 * 3 possibles values : no codec, deflate, snappy
	 */
	@RequestMapping(value = { "/hdfsAvroCodec/**", "/hdfsAvroCodec"}, method = RequestMethod.GET)
	@ResponseBody
	protected String displayAvroCodec(HttpServletRequest req) throws ServletException, IOException {
		
		String uri = req.getContextPath() + req.getServletPath();
		uri = uri.substring(0, uri.length() - ".html".length());
		String fsPath = uri.substring("/hdfsAvroCodec".length());
		if ((fsPath != null) && (fsPath.length() == 0))
			fsPath = "/";
		
		HdfsAvroFileViewer hdfsAvroFileViewer = new HdfsAvroFileViewer();
		Path path = new Path(fsPath);
		String codec = hdfsAvroFileViewer.displayCodec(getFileSystem(req), path);
		if (codec == null) codec = "No codec";
		return codec;
	}

	/**
	 * Upload file in HDFS
	 * 
	 * @param attachmentFile
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/hdfsAction/upload", method = {
			RequestMethod.POST, RequestMethod.GET })
	public String uploadFileInHdfs(
			@RequestParam("file") MultipartFile attachmentFile,
			@RequestParam("path") final String path, ModelMap model,
			HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if (getCurrentPath(req) == null) {
			setCurrentPath(new Path("/"), req);
			return displayDir(req, resp, model, getCurrentPath(req));
		} else {

			attachmentFile.transferTo(new File(
					configuration.getTempDirectory(), attachmentFile
							.getOriginalFilename()));
			final Path scrPath = new Path(configuration.getTempDirectory(),
					attachmentFile.getOriginalFilename());
			final Path destinationPath = new Path(getCurrentPath(req),
					attachmentFile.getOriginalFilename());
			try {
				getFileSystem(req).copyFromLocalFile(scrPath, destinationPath);
			} catch (Exception ace) {
				ace.printStackTrace();
				List<String> messages = new ArrayList<String>();
				messages.add(ace.getMessage());
				model.addAttribute("messages", messages);
			}

			return displayDir(req, resp, model, getCurrentPath(req));
		}
	}

	/**
	 * Save new HDFS configuration
	 * 
	 * @return
	 * @throws DocumentException
	 */
	@RequestMapping(value = "/saveHdfsConfiguration", method = RequestMethod.POST)
	public String saveHdfsConfiguration(
			@RequestParam("fs.default.name") final String fsDefaultName,
			@RequestParam("mapred.job.tracker") final String mapredJobTracker,
			@RequestParam("user") final String user,
			@RequestParam("pigTemp") final String pigTemp, ModelMap model,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Configuration configurationHadoop = new Configuration();
		try {
			configuration.saveUser(user, req);
			configuration.savePigTemp(pigTemp, req);
			configurationHadoop = configureHadoopCluster(req);
			configurationHadoop.set("fs.default.name", fsDefaultName);
			configurationHadoop.set("mapred.job.tracker", mapredJobTracker);
			configurationHadoop.reloadConfiguration();
			setFileSystem(FileSystem.get(configurationHadoop), req);
			// Save configuration in Hadoop XML files
			model.addAttribute("messages",
					saveHdfsConfiguration(fsDefaultName, mapredJobTracker, req));
		} catch (UnknownHostException e) {
			List<String> messages = new ArrayList<String>();
			messages.add(e.getMessage());
			model.addAttribute("configurationHadoop", configurationHadoop);
			model.addAttribute("messages", messages);
			return "hdfschangeconfiguration";
		} catch (ConnectException e) {
			List<String> messages = new ArrayList<String>();
			messages.add(e.getMessage());
			model.addAttribute("configurationHadoop", configurationHadoop);
			model.addAttribute("messages", messages);
			return "hdfschangeconfiguration";
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (getCurrentPath(req) != null) {
			List<String> messages = new ArrayList<String>();
			messages.add("Configuration " + fsDefaultName + " saved");
			model.addAttribute("messages", messages);
			return displayFileOrDirForPath(model, req, resp,
					getCurrentPath(req));
		} else
			return "hdfsbrowser";
	}

	/**
	 * Save HDFS configuration in XML file
	 * 
	 * @param fsDefaultName
	 * @param mapredJobTracker
	 * @return list of messages
	 * @throws DocumentException
	 */
	private List<String> saveHdfsConfiguration(final String fsDefaultName,
			final String mapredJobTracker, HttpServletRequest request)
			throws Exception {
		List<String> messages = new ArrayList<String>();
		File coreSiteXMLFile = new File(
				servletContext.getRealPath(configuration
						.getHadoopConfigurationCluster(request))
						+ "/core-site.xml");
		if (!coreSiteXMLFile.exists()) {
			messages.add(coreSiteXMLFile.getAbsolutePath() + " not found");
			return messages;
		}
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(coreSiteXMLFile);

		@SuppressWarnings("unchecked")
		List<Node> propertiesXML = document
				.selectNodes("//configuration/property");
		for (Node propertyXML : propertiesXML) {
			Node nameXML = propertyXML.selectSingleNode("name");
			if (nameXML.getStringValue().equals("fs.default.name")) {
				Node valueXML = propertyXML.selectSingleNode("value");
				valueXML.setText(fsDefaultName);

			}
		}

		// TODO jobtracker to change also

		XMLWriter output = new XMLWriter(new FileWriter(coreSiteXMLFile));
		output.write(document);
		output.close();
		return messages;
	}

	/**
	 * Save new HDFS configuration
	 * 
	 * @return
	 * @throws DocumentException
	 */
	@RequestMapping(value = "/suggestHdfsPath", method = RequestMethod.GET)
	@ResponseBody
	public List<ResultSearch> suggestHdfsPath(
			@RequestParam("obj") final String obj, ModelMap model,
			HttpServletRequest req) throws Exception {
		Map<String, FileSystemScanned> fileSystemScannedSet = statusesIndexService
				.getFileSystemScannedSet();
		List<ResultSearch> results = new ArrayList<ResultSearch>(10);

		// Search good fileSystem
		for (FileSystemScanned fileSystemScanned : fileSystemScannedSet
				.values()) {
			if (fileSystemScanned.getFileSystem().getCanonicalServiceName()
					.equals(getFileSystem(req).getCanonicalServiceName())) {

				try {
					Query q = queryParser.parse(obj);
					int hitsPerPage = 10;

					FastVectorHighlighter highlighter = LuceneUtil
							.makeHighlighter();
					FieldQuery fieldQuery = highlighter.getFieldQuery(q);

					IndexReader reader = IndexReader.open(fileSystemScanned
							.getIndex());
					IndexSearcher searcher = new IndexSearcher(reader);
					TopScoreDocCollector collector = TopScoreDocCollector
							.create(hitsPerPage, true);
					searcher.search(q, collector);
					ScoreDoc[] hits = collector.topDocs().scoreDocs;
					for (int i = 0; i < hits.length; ++i) {
						int docId = hits[i].doc;
						org.apache.lucene.document.Document d = searcher
								.doc(docId);
						// Result contains highlighted string and normal string
						results.add(new ResultSearch("" + i, highlighter
								.getBestFragment(fieldQuery, reader, docId,
										"highlightedpath", 10000), d
								.get("highlightedpath")));

					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}

			}
		}
		return results;
	}
}
