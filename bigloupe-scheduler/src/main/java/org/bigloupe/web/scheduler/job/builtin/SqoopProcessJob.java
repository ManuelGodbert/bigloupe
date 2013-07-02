package org.bigloupe.web.scheduler.job.builtin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.tools.ant.DirectoryScanner;
import org.bigloupe.web.BigLoupeConfiguration;
import org.bigloupe.web.scheduler.JobDescriptor;
import org.bigloupe.web.util.Utils;

/**
 * To launch SQOOP Process
 * Use default classpath in MapReduceJob
 * 
 * @author bigloupe
 *
 */
public class SqoopProcessJob extends MapReduceJob {

	/**
	 * Import or export
	 */
	public static final String SQOOP_USAGE = "usage";
	public static final String SQOOP_JDBCCONNECTION = "jdbcConnection";
	public static final String SQOOP_JDBCUSER = "jdbcUser";
	public static final String SQOOP_JDBCPASSWORD = "jdbcPassword";
	public static final String SQOOP_JDBCTABLE = "jdbcTable";
	public static final String SQOOP_JDBCWHERECLAUSE = "jdbcWhereClause";
	public static final String SQOOP_CONNECTION_FACTORIES = "sqoopConnectionFactories";
	
	
	// Option for import only
	public static final String SQOOP_AVRODATAFILE = "avroDataFile";
	public static final String SQOOP_NUMBERMAPPERS = "numberMappers";
	public static final String SQOOP_AVSCJARFILE = "avscJarFile";
	public static final String SQOOP_HDFSTARGETDIR = "hdfsTargetDir";
	
	
	// Option for export only
	public static final String SQOOP_EXPORTDIR = "exportDir";
	
	// Option outside SQOOP
	// Delete previous Java file generated
	public static final String SQOOP_DELETE_JAVA_MAPPING_FILE = "deleteJavaMappingFile";
	// Delete previous output HDFS directory
	public static final String SQOOP_DELETE_HDFS_TARGET_DIRECTORY = "deleteHdfsTargetDir";
	
	public static final String SQOOP_VERBOSE = "verbose";

	public static final String PIG_JAVA_CLASS = "com.cloudera.sqoop.Sqoop";

	// Possible JDBC Connection
	public static final Map<String, String> JDBC_CONNECTION = new HashMap<String, String>();
	static {
		JDBC_CONNECTION
				.put("KARMAQAL",
						"jdbc:oracle:thin:@lh-karmaqaq11-db.france.airfrance.fr:1530:KARMAQAL");
	}

	public final JobDescriptor descriptor;

	public SqoopProcessJob(JobDescriptor descriptor) {
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
		// Add hadoop home setting.
		String hadoopHome = descriptor.getProps().getString("hadoop.home");

		args += " -DSQOOP_HOME="
				+ BigLoupeConfiguration.getFullSqoopLibDirectory().substring(
						0,
						BigLoupeConfiguration.getFullSqoopLibDirectory()
								.length() - "\\lib".length());
		info("Using SQOOP config found in "
				+ BigLoupeConfiguration.getFullSqoopLibDirectory());

		return args;
	}

	@Override
	protected List<String> getMainArguments() {
		ArrayList<String> list = new ArrayList<String>();

		if (getUsage() != null) {
			list.add(getUsage());
		}

		// Add other SQOOP connection factories (eg : ORAOOP)
		if (getConnectionFactories() != null)
			list.add("-Dsqoop.connection.factories=" + getConnectionFactories());
		
		
		// Add libjars 
		// Copies the specified JAR files from the local
		// filesystem (or any filesystem if a scheme is
		// specified) to the shared filesystem used by the jobtracker (usually
		// HDFS), and adds them
		// to the MapReduce task’s classpath. This option is a useful way of
		// shipping JAR files that
		// a job is dependent on.
		int i=0;
		
		StringBuffer libjars = new StringBuffer();
		if (!getLibClassPath().isEmpty())
			libjars.append("-libjars ");
		for (String jar : getLibClassPath()) {
			libjars.append(jar.replace('\\', '/'));
			i++;
			if (i < getLibClassPath().size())
				libjars.append(",");
		}
		list.add(libjars.toString());

		if (getJdbcConnection() != null) {
			if (getJdbcConnection().startsWith("jdbc:")) {
				list.add("--connect " + getJdbcConnection());
			} else
				// Try to retrieve JDBC connection in a list
				list.add("--connect "
						+ JDBC_CONNECTION.get(getJdbcConnection()));
		}
		if (getJdbcUser() != null) {
			list.add("--username " + getJdbcUser());
		}
		if (getJdbcPassword() != null) {
			list.add("--password " + getJdbcPassword());
		}
		if (getJdbcTable() != null) {
			list.add("--table " + getJdbcTable());
		}

		// Delete previous java mapping file
		if (getDeleteJavaMappingFile()) {
			String workingDirectory = getWorkingDirectory();
			File javaFile = new File(workingDirectory, getJdbcTable() + ".java");
			if (javaFile.exists()) {
				javaFile.delete();
			}
		}

		// Parameter for import only
		if (getUsage().equals("import")) {
			if (getNumberMappers() != null) {
				list.add("--num-mappers " + getNumberMappers());
			}

			if (getJdbcWhereClause() != null) {
				list.add("--where " + getJdbcWhereClause());
			}
			if (getHdfsTargetDir() != null) {
				list.add("--target-dir " + getHdfsTargetDir());

				// Delete targetDir before launch
				if (getDeleteHdfsTargetDir()) {
					Configuration conf = new Configuration();

					String hadoopConfiguratationCluster = BigLoupeConfiguration
							.getDefaultHadoopConfigurationCluster();
					String workingDirectory = getWorkingDirectory();
					File fullHadoopConfigurationCluster = new File(
							workingDirectory.substring(0, workingDirectory
									.indexOf(BigLoupeConfiguration
											.getSchedulerDirectory()))
									+ hadoopConfiguratationCluster);

					// Add the conf dir to the classpath
					// Chain the current thread classloader
					URLClassLoader urlClassLoader;
					try {
						urlClassLoader = new URLClassLoader(
								new URL[] { fullHadoopConfigurationCluster
										.toURL() },
								Thread.currentThread().getContextClassLoader());
						// Replace the thread classloader - assumes
						// you have permissions to do so
						Thread.currentThread().setContextClassLoader(
								urlClassLoader);

						// Switch between HadoopKarmaCluster
						conf.setClassLoader(urlClassLoader);

						conf.reloadConfiguration();

						FileSystem fileSystem = FileSystem.get(conf);
						Path fileToDelete = new Path(getHdfsTargetDir());
						fileSystem.delete(fileToDelete, true);

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
			if (getAvroDataFile()) {
				list.add("--as-avrodatafile");
			}
			if ((getAvscJarFile() != null) && (!getAvscJarFile().equals(""))) {
				list.add("--avsc-jar-file " + descriptor.getId()
						+ System.getProperty("file.separator")
						+ getAvscJarFile());
				// Retrieve jar from tmp directory
				try {
					FileUtils.copyFile(
							new File(BigLoupeConfiguration.getTempDirectory()
									+ System.getProperty("file.separator")
									+ getAvscJarFile()),
							new File(descriptor.getFullPath().substring(
									0,
									descriptor.getFullPath().length()
											- ".job".length())
									+ System.getProperty("file.separator")
									+ getAvscJarFile()));
				} catch (IOException e) {
					e.printStackTrace();
					throw new IllegalStateException("Can't copy jar from "
							+ BigLoupeConfiguration.getTempDirectory()
							+ System.getProperty("file.separator")
							+ getAvscJarFile() + " to  "
							+ descriptor.getFullPath()
							+ System.getProperty("file.separator")
							+ getAvscJarFile() + " - " + e.getMessage());
				}
			}
		} // end paramameters import only

		
		if (getUsage().equals("export")) {
			if (getExportDir() != null) {
				list.add("--export-dir " + getExportDir());
			}

		}
	
		
		if (getVerbose()) {
			list.add("--verbose");
		}

		return list;
	}

	public Boolean getVerbose() {
		return getProps().getBoolean(SQOOP_VERBOSE, false);
	}

	/**
	 * Import (Database to HDFS by default)
	 * 
	 * @return
	 */
	public String getUsage() {
		return getProps().getString(SQOOP_USAGE, "import");
	}

	
	/**
	 * Import (Database to HDFS by default)
	 * 
	 * @return
	 */
	public String getConnectionFactories() {
		return getProps().getString(SQOOP_CONNECTION_FACTORIES, null);
	}
	
	/**
	 * JDBC Connection
	 * 
	 * @return
	 */
	public String getJdbcConnection() {
		return getProps().getString(SQOOP_JDBCCONNECTION, null);
	}

	/**
	 * JDBC User
	 * 
	 * @return
	 */
	public String getJdbcUser() {
		return getProps().getString(SQOOP_JDBCUSER, null);
	}

	/**
	 * JDBC Password
	 * 
	 * @return
	 */
	public String getJdbcPassword() {
		return getProps().getString(SQOOP_JDBCPASSWORD, null);
	}

	/**
	 * JDBC Table
	 * 
	 * @return
	 */
	public String getJdbcTable() {
		return getProps().getString(SQOOP_JDBCTABLE, null);
	}

	/**
	 * JDBC Where clause
	 * 
	 * @return
	 */
	public String getJdbcWhereClause() {
		return getProps().getString(SQOOP_JDBCWHERECLAUSE, null);
	}

	/**
	 * Option IMPORT only
	 * 
	 */

	/**
	 * HDFS Target dir
	 * 
	 * @return
	 */
	public String getHdfsTargetDir() {
		return getProps().getString(SQOOP_HDFSTARGETDIR, null);
	}

	/**
	 * Create file during import in HDFS in Avro (only for import)
	 * 
	 * @return
	 */
	public Boolean getAvroDataFile() {
		return getProps().getBoolean(SQOOP_AVRODATAFILE, false);
	}

	/**
	 * Number of mappers to import (only for import)
	 * 
	 * @return
	 */
	public Integer getNumberMappers() {
		return getProps().getInt(SQOOP_NUMBERMAPPERS, 1);
	}

	/**
	 * Avro Schema library to use during import
	 * 
	 * @return
	 */
	public String getAvscJarFile() {
		return getProps().getString(SQOOP_AVSCJARFILE, null);
	}

	/**
	 * Extra option for export
	 */
	/**
	 * Number of mappers to import (only for import)
	 * 
	 * @return
	 */
	public String getExportDir() {
		return getProps().getString(SQOOP_EXPORTDIR, null);
	}	
	
	/**
	 * Extra option for an easy use of SQOOP
	 */

	/**
	 * Delete previous Java file generated
	 */
	public Boolean getDeleteJavaMappingFile() {
		return getProps().getBoolean(SQOOP_DELETE_JAVA_MAPPING_FILE, false);
	}

	/**
	 * Delete previous target HDFS directory
	 */
	public Boolean getDeleteHdfsTargetDir() {
		return getProps().getBoolean(SQOOP_DELETE_HDFS_TARGET_DIRECTORY, false);
	}

}
