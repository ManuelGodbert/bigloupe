package org.bigloupe.web.controller;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import javax.servlet.http.HttpServletRequest;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.bigloupe.jobclient.service.JobTrackerClientService;
import org.bigloupe.web.BigLoupeConfiguration;
import org.bigloupe.web.hdfs.HDFSFileSystemManager;
import org.bigloupe.web.hdfs.JobTrackerClientManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractHdfsController extends AbstractConfigurationController
		implements InitializingBean {

	@Autowired
	private HDFSFileSystemManager fileSystemManager;

	@Autowired
	private JobTrackerClientManager jobTrackerClientManager;

	private ClassLoader threadClassLoader;

	@Override
	public void afterPropertiesSet() throws Exception {
		// Save classLoader
		threadClassLoader = Thread.currentThread().getContextClassLoader();
	}

	/**
	 * Change Hadoop cluster with classloader manipulation
	 * 
	 * @return
	 * @throws Exception
	 */
	protected Configuration configureHadoopCluster(HttpServletRequest request)
			throws Exception {

		Configuration conf = new Configuration();

		String hadoopConfigurationPath = servletContext.getRealPath(configuration
				.getHadoopConfigurationCluster(request));

		File hadoopConfigurationFile = new File(hadoopConfigurationPath);

		// Add the conf dir to the classpath
		// Chain the current thread classloader
		URLClassLoader urlClassLoader = new URLClassLoader(
				new URL[] { hadoopConfigurationFile.toURL() },
				threadClassLoader);
		// Replace the thread classloader - assumes
		// you have permissions to do so
		Thread.currentThread().setContextClassLoader(urlClassLoader);

		// Switch between HadoopKarmaCluster
		conf.setClassLoader(urlClassLoader);

		conf.reloadConfiguration();

		return conf;

	}

	/**
	 * Get filesystem from filesystem manager Filesystem manager avoid to
	 * BigLoupe to store HDFS connection in HTTP Session.
	 * 
	 * @param request
	 * @return
	 */
	protected FileSystem getFileSystem(HttpServletRequest request) {
		String fsCanonicalServiceName = (String) request.getSession()
				.getAttribute(BigLoupeConfiguration.FILESYSTEM);
		FileSystem fs = fileSystemManager.getFileSystem(fsCanonicalServiceName);
		if (fs != null)
			return fs;
		else {
			try {
				// Save fileSystem
				fs = FileSystem.get(configureHadoopCluster(request));
				setFileSystem(fs, request);

				return fs;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

	}
	
	/**
	 * Store filesystem in filesystem manager
	 * 
	 * @param fileSystem
	 * @param request
	 */
	protected void setFileSystem(FileSystem fileSystem,
			HttpServletRequest request) {
		fileSystemManager.addFileSystem(fileSystem);
		request.getSession().setAttribute(BigLoupeConfiguration.FILESYSTEM,
				fileSystem.getCanonicalServiceName());
	}
	
	protected JobTrackerClientService getJobTrackerClient(HttpServletRequest request) {
		String jobTrackerServiceName = (String) request.getSession()
				.getAttribute(BigLoupeConfiguration.JOBTRACKER);
		JobTrackerClientService jobTrackerClientService = jobTrackerClientManager.getJobTrackerClient(jobTrackerServiceName);
		if (jobTrackerClientService != null)
			return jobTrackerClientService;
		else {
			try {
				// Save fileSystem
				FileSystem fs = getFileSystem(request);
				jobTrackerClientService = new JobTrackerClientService(
						fs.getConf());
				setJobTrackerClient(jobTrackerClientService, request);
				return jobTrackerClientService;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

	}
	
	/**
	 * Store filesystem in filesystem manager
	 * 
	 * @param fileSystem
	 * @param request
	 */
	protected void setJobTrackerClient(JobTrackerClientService jobTrackerClientService,
			HttpServletRequest request) {
		jobTrackerClientManager.addJobTrackerClientService(jobTrackerClientService);
		request.getSession().setAttribute(BigLoupeConfiguration.JOBTRACKER,
				jobTrackerClientService.getJobTrackerServiceName());
	}


}
