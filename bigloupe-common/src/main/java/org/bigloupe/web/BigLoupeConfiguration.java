package org.bigloupe.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.ProtectionDomain;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;

/**
 * Configuration initialized by spring container
 * 
 * @author bigloupe
 * 
 */
public class BigLoupeConfiguration implements InitializingBean,
		ServletContextAware, Serializable {

	private static final long serialVersionUID = 7232674488221790013L;

	private static Logger logger = LoggerFactory
			.getLogger(BigLoupeConfiguration.class);

	public static final String DEFAULT_LOG_URL_PREFIX = "predefined_log_url_prefix";
	public static final String LOG_URL_PREFIX = "log_url_prefix";

	// Constants to identify objects in session
	public static final String FILESYSTEM = "FILESYSTEM";
	public static final String JOBTRACKER = "JOBTRACKER";
	public static final String CURRENT_HADOOP_CONFIGURATION = "CURRENT_HADOOP_CONFIGURATION";

	/**
	 * ElasticSearch Web URL (eg : http://localhost:9200) Initialized in
	 * bigloupe.properties
	 */
	private static String configurationWebElasticSearchServer;

	/**
	 * ElasticSearch Web server URL for node to node communication (eg :
	 * http://localhost:9300) URL where BigLoupe has been launched
	 */
	private static String configurationElasticSearchNodeUrl;

	/**
	 * ElasticSearch directory installation Initialized in bigloupe.properties
	 */
	private static String configurationElasticSearchDirectory;

	/**
	 * Web server URL (eg : http://localhost:9090) URL where BigLoupe has been
	 * launched
	 */
	private static String configurationWebBigLoupeServer;

	/**
	 * Hadoop configuration cluster Configuration directory for Hadoop
	 * "conf_cluster" directory including core-site.xml, hdfs-site.xml,
	 */
	private final String ROOT_HADOOP_CONFIGURATION_CLUSTER = "hdfs/cluster";

	private static String defaultHadoopConfigurationCluster = "hdfs/cluster/conf_filesystem";

	/**
	 * Data directory (file in json or csv available for web application) Could
	 * be in the future HDFS file directory
	 */
	private final String dataDirectory = "data";

	/**
	 * Scheduler directory
	 */
	private final static String schedulerDirectory = "scheduler";

	/**
	 * Pig directory (librairies & scripts)
	 */
	private final String pigDirectory = "pig";

	/**
	 * Pig libs directory (librairies)
	 */
	private final static String pigLibDirectory = "pig/lib";

	/**
	 * Sqoop libs directory
	 */
	private final static String sqoopLibDirectory = "sqoop/lib";

	/**
	 * Pig scripts directory (scripts)
	 */
	private final static String pigScriptDirectory = "pig/scripts";

	/**
	 * Temp directory for upload files
	 */
	private final static String tempDirectory = "tmp";

	/**
	 * Index Lucene directory
	 */
	private final static String indexDirectory = "indexes";

	private static String baseDir;

	// For testing if running in servlet context
	private ServletContext servletContext;

	/**
	 * If yes : Ambrose graph are available in BigLoupe Initialized in
	 * bigloupe.properties
	 */
	private static boolean useAmbroseInBigLoupe;

	private boolean pigLogDirOrSchedulerLogDir;

	/**
	 * Check properties
	 * 
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {

		if (servletContext == null) {
			if (baseDir == null) {
				ProtectionDomain protectionDomain = BigLoupeConfiguration.class
						.getProtectionDomain();
				baseDir = new File(protectionDomain.getCodeSource()
						.getLocation().getPath()).getParent();
			}
		} else
			baseDir = servletContext.getRealPath("");

		logger.info("BigLoupe launched in  : " + baseDir);
		readServerUrlConfiguration();

		logger.info(String.format(">>>>>>>>>>>>>>>> Using server Url : %s",
				configurationWebBigLoupeServer));

		logger.info(String.format(
				">>>>>>>>>>>>>>>> Using elastic search server Url : %s",
				configurationWebElasticSearchServer));

		// Create temporary dir if not exists
		File tempDirectoryFile = new File(baseDir, tempDirectory);
		if (!tempDirectoryFile.exists())
			tempDirectoryFile.mkdir();

	}

	public static void setConfigurationWebBigLoupeServer(
			String configurationWebBigLoupeServer) {
		BigLoupeConfiguration.configurationWebBigLoupeServer = configurationWebBigLoupeServer;
	}

	public static String getConfigurationWebBigLoupeServer() {
		return configurationWebBigLoupeServer;
	}

	/**
	 * ElasticSearch URL (eg : http://localhost:9200/)
	 */
	public static String getConfigurationWebElasticSearchServer() {
		return configurationWebElasticSearchServer;
	}

	public static void setConfigurationWebElasticSearchServer(
			String configurationWebElasticSearchServer) {
		BigLoupeConfiguration.configurationWebElasticSearchServer = configurationWebElasticSearchServer;
	}

	public static String getConfigurationElasticSearchDirectory() {
		return configurationElasticSearchDirectory;
	}

	public static void setConfigurationElasticSearchDirectory(
			String configurationElasticSearchDirectory) {
		BigLoupeConfiguration.configurationElasticSearchDirectory = configurationElasticSearchDirectory;
	}

	public static String getConfigurationElasticSearchNodeUrl() {
		return configurationElasticSearchNodeUrl;
	}

	public static void setConfigurationElasticSearchNodeUrl(
			String configurationElasticSearchNodeUrl) {
		BigLoupeConfiguration.configurationElasticSearchNodeUrl = configurationElasticSearchNodeUrl;
	}

	/**
	 * File Manager URL (eg : http://localhost:8080/filemanager/view/)
	 */
	public static String getFileManagerUrl() {
		return configurationWebBigLoupeServer + "/filemanager/view/";
	}

	public static String getViewJobLogUrl() {
		return configurationWebBigLoupeServer
				+ "/scheduler/viewJobLog.html?file=";
	}

	/**
	 * List of all Hadoop Cluster
	 * 
	 * @return map of hadoop clusters
	 */
	@Cacheable(value = "configurationHadoopKarmaCluster")
	public Map<String, String> getHadoopClusters() {
		Map<String, String> configurationHadoopKarmaCluster = new HashMap<String, String>();
		// Add configuration
		File configurationHadoopDirectory = new File(getBaseDir(),
				ROOT_HADOOP_CONFIGURATION_CLUSTER);

		if (configurationHadoopDirectory.exists()) {
			String[] configurationHadoopSubDirectories = configurationHadoopDirectory
					.list();
			for (String configurationHadoopSubDirectory : configurationHadoopSubDirectories) {
				configurationHadoopSubDirectory = configurationHadoopSubDirectory
						.substring(
								configurationHadoopSubDirectory
										.indexOf("conf_") + "conf_".length(),
								configurationHadoopSubDirectory.length());
				configurationHadoopKarmaCluster.put(
						configurationHadoopSubDirectory,
						configurationHadoopSubDirectory);
			}
		} else
			logger.info("HADOOP clusters configuration directory doen't exist. Check your configuration :'"
					+ configurationHadoopDirectory.getAbsolutePath());

		// Check if configuration doesn't exist with full dns name (e.g :
		// server.domain.com)
		// Remove short dns entry

		Map<String, String> configurationHadoopKarmaClusterClone = new HashMap<String, String>(
				configurationHadoopKarmaCluster);
		Set<String> keys = configurationHadoopKarmaClusterClone.keySet();
		for (String key : keys) {
			if (key.contains(".")) {
				String serverKey = key.substring(0, key.indexOf("."));
				if (configurationHadoopKarmaCluster.get(serverKey) != null) {
					configurationHadoopKarmaCluster.remove(serverKey);
				}
			}
		}
		configurationHadoopKarmaCluster.put("filesystem", "filesystem");

		return configurationHadoopKarmaCluster;
	}

	/**
	 * HDFS configuration filesystem (eg :
	 * "hdfs/cluster/conf_qvipkar6.france.airfrance.fr")
	 * 
	 * @return
	 */
	public String getHadoopConfigurationCluster(HttpServletRequest request) {
		String hadoopConfigurationCluster = (String) request.getSession()
				.getAttribute(CURRENT_HADOOP_CONFIGURATION);
		if (hadoopConfigurationCluster == null)
			return defaultHadoopConfigurationCluster;

		return hadoopConfigurationCluster;
	}

	/**
	 * For scheduler (no HTTP Session available) - Can work only on qvipkar5
	 * 
	 * @return
	 */
	public static String getDefaultHadoopConfigurationCluster() {
		return defaultHadoopConfigurationCluster;
	}

	/**
	 * HDFS configuration filesystem for a cluster
	 * 
	 * @param cluster
	 * @return
	 */
	public String getHadoopConfigurationCluster(String cluster) {
		return ROOT_HADOOP_CONFIGURATION_CLUSTER + "/conf_" + cluster;
	}

	/**
	 * HDFS configuration with full absolute path
	 * 
	 * @return
	 */
	public String getFullHadoopConfigurationCluster(HttpServletRequest request) {
		String hadoopConfigurationCluster = getHadoopConfigurationCluster(request);
		return getBaseDir() + System.getProperty("file.separator")
				+ hadoopConfigurationCluster;
	}

	public void setHadoopConfigurationCluster(String cluster,
			HttpServletRequest request) {
		String hadoopConfigurationCluster = ROOT_HADOOP_CONFIGURATION_CLUSTER
				+ "/conf_" + cluster;
		request.getSession().setAttribute(CURRENT_HADOOP_CONFIGURATION,
				hadoopConfigurationCluster);
	}

	public String getCurrentHadoopCluster(HttpServletRequest request) {
		String hadoopConfigurationCluster = getHadoopConfigurationCluster(request);
		return hadoopConfigurationCluster.substring(
				hadoopConfigurationCluster.indexOf('_') + 1,
				hadoopConfigurationCluster.length());
	}

	/**
	 * Save user in user.txt in the correct folder
	 * 
	 * @param user
	 * @return
	 */
	public void saveUser(String user, HttpServletRequest request) {
		try {
			File userFile = new File(getBaseDir()
					+ System.getProperty("file.separator")
					+ getHadoopConfigurationCluster(request), "user.txt");
			if (!userFile.exists())
				userFile.createNewFile();

			if (user == null)
				user = System.getProperty("user.name");
			FileUtils.write(userFile, user);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get User Information is stored in Hadoop cluster configuration directory
	 * 
	 * @return
	 */
	public static String getUser(String hadoopHome) {
		File userFile;
		if (hadoopHome == null) {
			userFile = new File(getBaseDir()
					+ System.getProperty("file.separator")
					+ getDefaultHadoopConfigurationCluster(), "user.txt");
		} else {
			userFile = new File(hadoopHome, "user.txt");
		}
		if (userFile.exists())
			try {
				return FileUtils.readFileToString(userFile);
			} catch (IOException e) {
				e.printStackTrace();
				return System.getProperty("user.name");
			}
		else
			return System.getProperty("user.name");
	}

	/**
	 * Save pig temporary directory in configuration.txt in the correct folder
	 * 
	 * @param pigTemp
	 *            : temporary directory in HDFS
	 * @return
	 */
	public void savePigTemp(String pigTemp, HttpServletRequest request) {
		setProperty("pig.tmp.dir", pigTemp, request);
	}

	/**
	 * <p>
	 * Get temp directory used by Apache Pig. This configuration depends of HDFS
	 * configuration and is stored in Hadoop cluster configuration directory
	 * </p>
	 * 
	 * @return
	 */
	public static String getPigTemp(String hadoopHome) {
		return getProperty(hadoopHome, "pig.tmp.dir", "pig/tmp");
	}

	private void setProperty(String property, String value,
			HttpServletRequest request) {
		try {
			File configurationFile = new File(getBaseDir()
					+ System.getProperty("file.separator")
					+ getHadoopConfigurationCluster(request),
					"configuration.txt");
			if (!configurationFile.exists())
				configurationFile.createNewFile();

			Properties properties = new Properties();
			properties.setProperty(property, value);
			properties.store(new FileOutputStream(configurationFile),
					"Saved by " + request.getRemoteUser());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getProperty(String hadoopHome, String property,
			String defaultValue) {
		File configurationFile;
		if (hadoopHome == null) {
			configurationFile = new File(getBaseDir()
					+ System.getProperty("file.separator")
					+ getDefaultHadoopConfigurationCluster(),
					"configuration.txt");
		} else {
			configurationFile = new File(hadoopHome, "configuration.txt");
		}
		if (configurationFile.exists())
			try {
				Properties properties = new Properties();
				properties.load(new FileInputStream(configurationFile));
				if (properties.getProperty(property) != null)
					return properties.getProperty(property);
				else
					return defaultValue;
			} catch (IOException e) {
				e.printStackTrace();
				return defaultValue;
			}
		else
			return defaultValue;
	}

	/**
	 * Data directory (/data) containing JSON files (results of scheduled jobs)
	 * 
	 * @return
	 */
	public String getFullDataDirectory() {
		return getBaseDir() + System.getProperty("file.separator")
				+ dataDirectory;
	}

	/**
	 * Pig directory (script and librairies)
	 * 
	 * @return
	 */
	public String getPigDirectory() {
		return pigDirectory;
	}

	/**
	 * Pig lib directory (librairies)
	 * 
	 * @return
	 */
	public String getPigLibDirectory() {
		return pigLibDirectory;
	}

	/**
	 * Pig scripts directory (scripts)
	 * 
	 * @return
	 */
	public String getPigScriptDirectory() {
		return pigScriptDirectory;
	}

	/**
	 * Full path for pig lib directory (librairies)
	 * 
	 * @return
	 */
	public static String getFullPigLibDirectory() {
		return getBaseDir() + System.getProperty("file.separator")
				+ pigLibDirectory;
	}

	/**
	 * Full path for sqoop lib directory (librairies)
	 * 
	 * @return
	 */
	public static String getFullSqoopLibDirectory() {
		return getBaseDir() + System.getProperty("file.separator")
				+ sqoopLibDirectory;
	}

	/**
	 * Scheduler directory (definition of jobs and logs)
	 * 
	 * @return
	 */
	public static String getSchedulerDirectory() {
		return schedulerDirectory;
	}

	/**
	 * Temporary directory (for uploaded files) - full path
	 * 
	 * @return
	 */
	public static String getTempDirectory() {
		String directory = getBaseDir() + System.getProperty("file.separator")
				+ tempDirectory;
		File directoryFile = new File(directory);
		if (!directoryFile.exists())
			directoryFile.mkdirs();
		return directory;
	}

	public static String getIndexDirectory() {
		String directory = getBaseDir() + System.getProperty("file.separator")
				+ indexDirectory;
		File directoryFile = new File(directory);
		if (!directoryFile.exists())
			directoryFile.mkdirs();
		return directory;
	}

	public static boolean isUseAmbroseInBigLoupe() {
		return useAmbroseInBigLoupe;
	}

	public void setUseAmbroseInBigLoupe(boolean useAmbroseInBigLoupe) {
		BigLoupeConfiguration.useAmbroseInBigLoupe = useAmbroseInBigLoupe;
	}

	public boolean isPigLogDirOrSchedulerLogDir() {
		return pigLogDirOrSchedulerLogDir;
	}

	public void setPigLogDirOrSchedulerLogDir(boolean pigLogDirOrSchedulerLogDir) {
		this.pigLogDirOrSchedulerLogDir = pigLogDirOrSchedulerLogDir;
	}

	/**
	 * Save configuration after init
	 */
	public static void saveServerUrlConfiguration(String serverUrl) {
		String classesDir = getBaseDir();
		try {
			Properties properties = new Properties();

			properties.setProperty("server.url", serverUrl);
			BigLoupeConfiguration.setConfigurationWebBigLoupeServer(serverUrl);
			File bigLoupeServerUrlFile = new File(classesDir,
					"bigloupe-server-url.properties");
			if (!bigLoupeServerUrlFile.exists())
				bigLoupeServerUrlFile.createNewFile();
			FileOutputStream outputStream = new FileOutputStream(
					bigLoupeServerUrlFile);
			properties.store(outputStream,
					" " + (new SimpleDateFormat().format(new Date())));
		} catch (Exception e) {
			logger.error("Can't save bigloupe-server-url.properties in '"
					+ classesDir + "'");
			e.printStackTrace();
		}

	}

	/**
	 * Save configuration after init
	 */
	public static String readServerUrlConfiguration() {
		String classesDir = getBaseDir();
		try {
			Properties properties = new Properties();
			FileInputStream inputStream = new FileInputStream(new File(
					classesDir, "bigloupe-server-url.properties"));
			properties.load(inputStream);

			BigLoupeConfiguration.setConfigurationWebBigLoupeServer(properties
					.getProperty("server.url"));
			return properties.getProperty("bigloupe.server.url");
		} catch (Exception e) {
			logger.error("Can't load bigloupe-server-url.properties in '"
					+ classesDir + "'");
			return null;
		}

	}

	public static String getBaseDir() {
		return baseDir;
	}

	public static void setBaseDir(String baseDir) {
		BigLoupeConfiguration.baseDir = baseDir;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}
