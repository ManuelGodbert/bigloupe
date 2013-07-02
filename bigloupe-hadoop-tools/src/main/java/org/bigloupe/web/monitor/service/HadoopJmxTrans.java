package org.bigloupe.web.monitor.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.hadoop.conf.Configuration;
import org.bigloupe.web.BigLoupeConfiguration;
import org.jmxtrans.embedded.EmbeddedJmxTrans;
import org.jmxtrans.embedded.EmbeddedJmxTransException;
import org.jmxtrans.embedded.config.ConfigurationParser;
import org.jmxtrans.embedded.spring.SpringEmbeddedJmxTrans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

/**
 * Build JSON configuration file for JMXTrans
 * Use JMXTrans (support remote servers)
 * 
 * @author bigloupe
 * 
 */
@Service
public class HadoopJmxTrans implements InitializingBean, ResourceLoaderAware {

	private static final String TEMPLATE_CONFIGURATION_JMXTRANS_DIR = "jmxtrans";

	@Autowired
	BigLoupeConfiguration bigLoupeConfiguration;

	ResourceLoader resourceLoader;

	private ClassLoader threadClassLoader;

	private static Logger logger = LoggerFactory.getLogger(HadoopJmxTrans.class);

	/**
	 * Build JMX trans configuration files in tmp directory
	 * 
	 * @return list of JSON configuration files
	 * @throws Exception
	 */
	public List<File> configurationBuilder(String templateConfigurationDirectory) throws Exception {

		// Retrieve all Hadoop configurations
		Map<String, String> hadoopClusters = bigLoupeConfiguration
				.getHadoopClusters();
		Set<String> clusters = hadoopClusters.keySet();

		// Retrieve all jmxtrans template files
		Resource jmxtransTemplateConfigurationDir = resourceLoader
				.getResource(templateConfigurationDirectory);

		String baseDir = BigLoupeConfiguration.getBaseDir();

		List<File> configurationFiles = new ArrayList<File>();

		if (jmxtransTemplateConfigurationDir.exists()) {
			// For each clusters
			for (String cluster : clusters) {
				if (!cluster.equals("filesystem")) {

					String hadoopConfigurationPath = baseDir
							+ "/"
							+ bigLoupeConfiguration
									.getHadoopConfigurationCluster(cluster);
					Configuration hadoopConfiguration = configureHadoopCluster(hadoopConfigurationPath);
					Map<String, String> valuesMap = new HashMap<String, String>();

					// String fsDefaultName = hadoopConfiguration
					// .get("fs.default.name");
					// fsDefaultName =
					// fsDefaultName.substring("hdfs://".length(),
					// fsDefaultName.lastIndexOf(":"));
					valuesMap.put("namenode", cluster);

					File[] jmxtransTemplateConfigurationFiles = jmxtransTemplateConfigurationDir
							.getFile().listFiles();
					for (File jmxtransTemplateConfigurationFile : jmxtransTemplateConfigurationFiles) {
						String jmxtransConfigurationFileName = jmxtransTemplateConfigurationFile
								.getName();
						jmxtransConfigurationFileName = jmxtransConfigurationFileName
								.substring(0,
										jmxtransConfigurationFileName.length()
												- ".json".length());
						File jmxtransConfigurationFile = new File(
								hadoopConfigurationPath,
								jmxtransConfigurationFileName + "-" + cluster
										+ ".json");
						jmxtransConfigurationFile.createNewFile();
						replaceHadoopParametersInFile(
								jmxtransTemplateConfigurationFile,
								jmxtransConfigurationFile, valuesMap);
						// Add new file
						configurationFiles.add(jmxtransConfigurationFile);
					}
				}

			}
		} else
			logger.info("No JMXTrans configuration directory (" + jmxtransTemplateConfigurationDir + ")");
		if (configurationFiles.size() == 0) 
			logger.info("No JMXTrans configuration files found in " + jmxtransTemplateConfigurationDir.getFile().getAbsolutePath());
		return configurationFiles;
	}

	/**
	 * Replace variables ${param}
	 * 
	 * @param in
	 *            file
	 * @param out
	 *            file
	 * @throws Exception
	 */
	private void replaceHadoopParametersInFile(File in, File out,
			Map<String, String> valuesMap) throws Exception {
		StrSubstitutor sub = new StrSubstitutor(valuesMap);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(in)));
		PrintWriter writer = new PrintWriter(new FileWriter(out));
		String line;
		while ((line = reader.readLine()) != null) {
			String resolvedString = sub.replace(line);
			writer.println(resolvedString);
		}
		reader.close();
		writer.close();
	}

	/**
	 * Change Hadoop cluster with classloader manipulation
	 * 
	 * @return
	 * @throws Exception
	 */
	protected Configuration configureHadoopCluster(
			String hadoopConfigurationPath) throws Exception {

		Configuration conf = new Configuration();

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

	public void loadConfiguration() throws Exception {
		EmbeddedJmxTrans embeddedJmxTrans = new EmbeddedJmxTrans();
		ConfigurationParser parser = new ConfigurationParser();
		List<File> configurationFiles = configurationBuilder("classpath:/"
				+ TEMPLATE_CONFIGURATION_JMXTRANS_DIR);
		for (File configurationFile : configurationFiles) {
			try {

				logger.debug("Load configuration {}", configurationFile);

				parser.mergeEmbeddedJmxTransConfiguration(new FileInputStream(
						configurationFile), embeddedJmxTrans);
			} catch (Exception e) {
				throw new EmbeddedJmxTransException(
						"Exception loading configuration " + configurationFile,
						e);
			}
		}
		logger.info("Created EmbeddedJmxTrans with configuration {})", Arrays.asList(configurationFiles));
		
        embeddedJmxTrans.start();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// Save classLoader
		threadClassLoader = Thread.currentThread().getContextClassLoader();
		loadConfiguration();
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	
}
