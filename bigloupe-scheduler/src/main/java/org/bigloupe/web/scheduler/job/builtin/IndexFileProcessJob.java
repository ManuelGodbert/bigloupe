package org.bigloupe.web.scheduler.job.builtin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.tools.ant.DirectoryScanner;
import org.bigloupe.web.BigLoupeConfiguration;
import org.bigloupe.web.dto.PairValue;
import org.bigloupe.web.scheduler.JobDescriptor;
import org.bigloupe.web.util.StringUtils;
import org.bigloupe.web.util.Utils;


/**
 * 
 * Job to index a file in Elastic Search
 * 
 * @author bigloupe
 * 
 */
public class IndexFileProcessJob extends MapReduceJob {

	public static final String PIG_JAVA_CLASS = "org.apache.pig.Main";

	/**
	 * File to index in ElasticSearch
	 */
	public static final String FILE_TO_INDEX = "fileToIndex";
	
	/**
	 * Index name in ElastciSearch
	 */
	public static final String INDEX = "index";

	public static final String ELASTICSEARCH_DIRECTORY = "elasticSearchDirectory";

	public final JobDescriptor descriptor;

	public IndexFileProcessJob(JobDescriptor descriptor) {
		super(descriptor);
		this.descriptor = descriptor;
	}

	@Override
	protected String getJavaClass() {
		return PIG_JAVA_CLASS;
	}

	@Override
	protected String getJVMArguments() {
		String args = super.getJVMArguments();
		
		// Use Ambrose to have chord diagram
		if (BigLoupeConfiguration.isUseAmbroseInBigLoupe()) {
			args += " -Dpig.notification.listener=org.bigloupe.web.monitor.pig.BigLoupePigProgressNotificationListener";
			args += " -Dbigloupe.http.server=" + BigLoupeConfiguration.getConfigurationWebBigLoupeServer();
		}
		
		// Use specific directory for Pig temporary directory
		String hadoopHome = descriptor.getProps().getString("hadoop.home");
		args += " -Dpig.temp.dir=" + BigLoupeConfiguration.getPigTemp(hadoopHome);

		return args;
	}

	@Override
	protected List<String> getMainArguments() {
		ArrayList<String> list = new ArrayList<String>();

		// Pig local mode if filesystem and not hadoop cluster configuration
		String hadoopHome = descriptor.getProps().getString("hadoop.home");
		if (hadoopHome.endsWith("filesystem")) {
			list.add("-x local");
		}
		
		if (getFileToIndex() != null) {
			list.add("-param fileToIndex=" + getFileToIndex());
			
			// Add Object
			int length = (getFileToIndex().endsWith(".avro")) ? getFileToIndex()
					.length() - ".avro".length()
					: getFileToIndex().length();
			String object = getFileToIndex().substring(
					getFileToIndex().lastIndexOf('/') + 1, length);
			
			// Verify if filename to index is not a part of a big file (starts by part-m)
			// Use directory name
			if (object.startsWith("part-m")) {
				String fullPathNoEndSeparator = FilenameUtils.getFullPathNoEndSeparator(getFileToIndex());
				object = FilenameUtils.getBaseName(fullPathNoEndSeparator);
			}
			list.add("-param object=" + object.toLowerCase());
			
			
			// Index
			if (getIndexName() != null)
				list.add("-param index=" + getIndexName());
			else
				list.add("-param index=" + object.toLowerCase());
		}

		if (getElasticSearchDirectory() != null) {
			list.add("-param elasticSearchDirectory="
					+ getElasticSearchDirectory());
		}

		// Script
		list.add("index_generic_avro_file_with_elasticsearch.pig");

		return list;
	}

	/**
	 * File to Index
	 * 
	 * @return
	 */
	public String getFileToIndex() {
		return getProps().getString(FILE_TO_INDEX);
	}

	/**
	 * Index name in elastcisearch
	 * 
	 * @return
	 */
	public String getIndexName() {
		return getProps().getString(INDEX, null);
	}
	
	/**
	 * ElasticSearch Directory installation
	 * 
	 * @return
	 */
	public String getElasticSearchDirectory() {
		return getProps().getString(ELASTICSEARCH_DIRECTORY,
				"/app/KARMA/travail/karma-search");
	}

}
