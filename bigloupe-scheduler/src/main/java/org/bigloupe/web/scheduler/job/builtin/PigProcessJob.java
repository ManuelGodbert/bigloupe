package org.bigloupe.web.scheduler.job.builtin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.bigloupe.web.BigLoupeConfiguration;
import org.bigloupe.web.scheduler.JobDescriptor;
import org.bigloupe.web.util.StringUtils;
import org.bigloupe.web.util.Utils;


/**
 * Use MapReduceJob to use generic classpath 
 * @author m329997
 *
 */
public class PigProcessJob extends MapReduceJob {

	public static final String PIG_SCRIPT = "pig.script";
	public static final String UDF_IMPORT = "udf.import.list";
	public static final String PIG_PARAM_PREFIX = "param.";
	public static final String PIG_PARAM_FILES = "paramfile";
	public static final String HADOOP_UGI = "hadoop.job.ugi";
	public static final String DEBUG = "debug";
	// Commands to execute (within quotes)
	public static final String PIG_COMMAND = "command";

	public static final String PIG_JAVA_CLASS = "org.apache.pig.Main";

	public final JobDescriptor descriptor;

	public PigProcessJob(JobDescriptor descriptor) {
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

		List<String> udfImport = getUDFImportList();
		if (udfImport != null) {
			args += " -Dudf.import.list="
					+ super.createArguments(udfImport, ":");
		}

		String hadoopUGI = getHadoopUGI();
		if (hadoopUGI != null) {
			args += " -Dhadoop.job.ugi=" + hadoopUGI;
		}
		
		// Use Ambrose to have chord diagram
		if (BigLoupeConfiguration.isUseAmbroseInBigLoupe()) {
			args += " -Dpig.notification.listener=org.bigloupe.web.monitor.pig.BigLoupePigProgressNotificationListener";
			args += " -Dbigloupe.http.server=" + BigLoupeConfiguration.getConfigurationWebBigLoupeServer();
		}
		
		// Use specific directory for Pig temporary directory
		// Add hadoop home setting.
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
		
		Map<String, String> map = getPigParams();
		if (map != null) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				char quote = StringUtils.SINGLE_QUOTE;
				if (System.getProperty("os.name").startsWith("Windows")) {
					quote = StringUtils.DOUBLE_QUOTE;
			        // includes: Windows 2000,  Windows 95, Windows 98, Windows NT, Windows Vista, Windows XP
			    } 
				list.add("-param "
						+ StringUtils.shellQuote(
								entry.getKey() + "=" + entry.getValue(),
								quote));
			}
		}

		List<String> paramFiles = getPigParamFiles();
		if (paramFiles != null) {
			for (String paramFile : paramFiles) {
				list.add("-param_file " + paramFile);
			}
		}

		if (getDebug()) {
			list.add("-debug");
		}

		if (getExecute() != null) {
			list.add("-e");
			list.add("\"" + getExecute() + "\"");
		}	
		else
			list.add(getScript());

		return list;
	}

	protected boolean getDebug() {
		return getProps().getBoolean(DEBUG, false);
	}

	protected String getExecute() {
		return getProps().getString(PIG_COMMAND, null);
	}
	
	protected String getScript() {
		return getProps().getString(PIG_SCRIPT, getJobName() + ".pig");
	}

	protected List<String> getUDFImportList() {
		return getProps().getStringList(UDF_IMPORT, null, ",");
	}

	protected String getHadoopUGI() {
		return getProps().getString(HADOOP_UGI, null);
	}

	protected Map<String, String> getPigParams() {
		return getProps().getMapByPrefix(PIG_PARAM_PREFIX);
	}

	protected List<String> getPigParamFiles() {
		return getProps().getStringList(PIG_PARAM_FILES, null, ",");
	}
}
