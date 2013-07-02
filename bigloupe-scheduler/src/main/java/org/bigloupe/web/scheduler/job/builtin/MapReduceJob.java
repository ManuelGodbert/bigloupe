package org.bigloupe.web.scheduler.job.builtin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.bigloupe.web.scheduler.JobDescriptor;
import org.bigloupe.web.util.UndefinedPropertyException;
import org.bigloupe.web.util.Utils;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

public class MapReduceJob extends JavaProcessJob {

	public static final String MAP_REDUCE_JAVA_CLASS = "org.apache.hadoop.util.RunJar";

	private List<String> libClassPath = new ArrayList<String>();

	public final JobDescriptor descriptor;

	boolean runJar = true;

	public MapReduceJob(JobDescriptor descriptor) {
		super(descriptor);
		this.descriptor = descriptor;
	}

	@Override
	protected String getJavaClass() {
		// For DistCp, ... return java.class and not generic RunJar runner
		String javaClass = descriptor.getProps().getString("java.class", null);
		if (javaClass != null) {
			runJar = false;
			return "";
		} else
			return MAP_REDUCE_JAVA_CLASS;
	}

	@Override
	protected String getJVMArguments() {
		String args = super.getJVMArguments();
		// Add hadoop home setting.
		String hadoopHome = descriptor.getProps().getString("hadoop.home");

		File hadoopHomeFile = new File(hadoopHome);
		args += " -DHADOOP_HOME=" + hadoopHomeFile.getPath();
		info("Using hadoop config found in " + hadoopHomeFile.getPath());

		Map<String, String> systemEnv = new HashMap<String, String>();
		systemEnv.put("HADOOP_HOME", hadoopHomeFile.getPath());
		Utils.setEnv(systemEnv);

		Map<String, String> env = getEnv();
		Set<String> keyProperties = env.keySet();
		for (String keyProperty : keyProperties) {
			args += " -D" + keyProperty + "=" + env.get(keyProperty);
		}

		return args;
	}

	@Override
	protected List<String> getClassPaths() {
		List<String> classPath = super.getClassPaths();

		// Add hadoop home setting.
		String hadoopHome = descriptor.getProps().getString("hadoop.home");
		classPath.add(new File(hadoopHome).getPath()
				+ System.getProperty("file.separator"));

		// Add libs in working directory
		if (getWorkingDirectory() != null
				&& (!getWorkingDirectory().equals(""))) {
			DirectoryScanner ds = new DirectoryScanner();
			ds.setBasedir(getWorkingDirectory());
			ds.setIncludes(new String[] { "*.jar" });
			ds.scan();
			for (String jar : ds.getIncludedFiles()) {
				classPath.add(getWorkingDirectory()
						+ System.getProperty("file.separator") + jar);
			}
		}

		// Add extra classpath necessary for map reduce job
		List<String> classPathsExtra = descriptor.getProps().getStringList(
				"classpath.ext", getDefaultLibPath(), ",");

		boolean unixSeparator = (System.getProperty("file.separator")
				.equals('/')) ? true : false;
		for (String classPathExtra : classPathsExtra) {
			classPathExtra = FilenameUtils.normalize(classPathExtra,
					unixSeparator);

			if (classPathExtra != null && (!classPathExtra.equals(""))) {
				DirectoryScanner ds = new DirectoryScanner();
				ds.setBasedir(getWorkingDirectory()
						+ System.getProperty("file.separator") + classPathExtra);
				ds.setIncludes(new String[] { "*.jar" });
				ds.scan();
				for (String jar : ds.getIncludedFiles()) {
					classPath.add(classPathExtra
							+ System.getProperty("file.separator") + jar);
					// Add also for TaskTracker
					getLibClassPath()
							.add(FilenameUtils.normalize(classPathExtra
									+ System.getProperty("file.separator")
									+ jar, unixSeparator));

				}
			}
		}

		return classPath;
	}

	/**
	 * Add default library path (/lib) if exist,
	 * 
	 * @return
	 */
	private List<String> getDefaultLibPath() {
		ArrayList<String> defaultLibPath = new ArrayList<String>(1);
		// Test if lib directories
		File libDir = new File(getWorkingDirectory()
				+ System.getProperty("file.separator") + "lib");
		if (libDir.exists()) {
			defaultLibPath.add("lib");
		}
		return defaultLibPath;
	}

	@Override
	protected List<String> getMainArguments() {
		ArrayList<String> list = new ArrayList<String>();

		String jarName = descriptor.getProps().getString("jar", null);
		if (jarName != null)
			list.add(jarName);

		String javaClass = descriptor.getProps().getString("java.class", null);
		if (javaClass != null)
			list.add(javaClass);

		if (list.isEmpty())
			throw new UndefinedPropertyException(
					"Missing required property jar or java.class");

		if (runJar) {
			// Add libjars
			// Copies the specified JAR files from the local
			// filesystem (or any filesystem if a scheme is
			// specified) to the shared filesystem used by the jobtracker
			// (usually
			// HDFS), and adds them
			// to the MapReduce task’s classpath. This option is a useful way of
			// shipping JAR files that
			// a job is dependent on.
			int i = 0;

			StringBuffer libjars = new StringBuffer();
			if (!libClassPath.isEmpty())
				libjars.append("-libjars ");
			for (String jar : libClassPath) {
				libjars.append(jar.replace('\\', '/'));
				i++;
				if (i < libClassPath.size())
					libjars.append(",");
			}
			list.add(libjars.toString());
		}

		List<String> args = getProps().getStringList("jar.args", null, ",");
		if (args != null)
			list.addAll(args);

		return list;
	}

	public List<String> getLibClassPath() {
		return libClassPath;
	}
}
