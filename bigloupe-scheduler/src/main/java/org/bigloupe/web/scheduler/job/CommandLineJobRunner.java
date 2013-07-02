package org.bigloupe.web.scheduler.job;

import static java.util.Arrays.asList;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import org.bigloupe.web.scheduler.JobManager;
import org.bigloupe.web.scheduler.JobWrappingFactory;
import org.bigloupe.web.scheduler.flow.ExecutableFlow;
import org.bigloupe.web.scheduler.flow.FlowCallback;
import org.bigloupe.web.scheduler.flow.FlowManager;
import org.bigloupe.web.scheduler.flow.RefreshableFlowManager;
import org.bigloupe.web.scheduler.job.builtin.IndexFileProcessJob;
import org.bigloupe.web.scheduler.job.builtin.JavaJob;
import org.bigloupe.web.scheduler.job.builtin.JavaProcessJob;
import org.bigloupe.web.scheduler.job.builtin.MapReduceJob;
import org.bigloupe.web.scheduler.job.builtin.PigProcessJob;
import org.bigloupe.web.scheduler.job.builtin.ProcessJob;
import org.bigloupe.web.scheduler.job.builtin.SqoopProcessJob;
import org.bigloupe.web.scheduler.jobcontrol.impl.jobs.locks.NamedPermitManager;
import org.bigloupe.web.scheduler.jobcontrol.impl.jobs.locks.ReadWriteLockManager;
import org.bigloupe.web.scheduler.serializer.DefaultExecutableFlowDeserializer;
import org.bigloupe.web.scheduler.serializer.DefaultExecutableFlowSerializer;
import org.bigloupe.web.scheduler.serializer.ExecutableFlowDeserializer;
import org.bigloupe.web.scheduler.serializer.ExecutableFlowSerializer;
import org.bigloupe.web.scheduler.serializer.FlowExecutionDeserializer;
import org.bigloupe.web.scheduler.serializer.FlowExecutionSerializer;
import org.bigloupe.web.util.Props;
import org.bigloupe.web.util.Utils;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import com.google.common.collect.ImmutableMap;

/**
 * Runs a job from the command line
 * 
 * The usage is
 * 
 * java org.bigloupe.web.scheduler.job.CommandLineJobRunner props-file
 * prop_key=prop_val
 * 
 * Any argument that contains an '=' is assumed to be a property, all others are
 * assumed to be properties files for the job
 * 
 * The order of the properties files matters--in the case where both define a
 * property it will be read from the last file given.
 * 
 * @author jkreps
 * 
 */
public class CommandLineJobRunner {

	public static void main(String[] args) throws Exception {
		OptionParser parser = new OptionParser();
		OptionSpec<String> overrideOpt = parser
				.acceptsAll(asList("o", "override"),
						"An override property to be used instead of what is in the job")
				.withRequiredArg().describedAs("key=val");
		String ignoreDepsOpt = "ignore-deps";
		parser.accepts(ignoreDepsOpt,
				"Run only the specified job, ignoring dependencies");
		SchedulerCommandLine cl = new SchedulerCommandLine(parser, args);

		String helpMessage = "USAGE: run-job.sh [options] job_name...";
		OptionSet options = cl.getOptions();
		if (cl.hasHelp())
			cl.printHelpAndExit(helpMessage, System.out);

		List<String> jobNames = options.nonOptionArguments();
		if (jobNames.size() < 1)
			cl.printHelpAndExit(helpMessage, System.err);

		// parse override properties
		boolean ignoreDeps = options.has(ignoreDepsOpt);
		Props overrideProps = addGenericProperties();
		for (String override : options.valuesOf(overrideOpt)) {
			String[] pieces = override.split("=");
			if (pieces.length != 2)
				Utils.croak("Invalid property override: '" + override
						+ "', properties must be in the form key=value", 1);
			overrideProps.put(pieces[0], pieces[1]);
		}

		NamedPermitManager permitManager = new NamedPermitManager();
		permitManager.createNamedPermit("default", cl.getNumWorkPermits());

		Map<String, Class<? extends Job>> map = new HashMap<String, Class<? extends Job>>();
		map.put("java", JavaJob.class);
		map.put("command", ProcessJob.class);
		map.put("javaprocess", JavaProcessJob.class);
		map.put("pig", PigProcessJob.class);
		map.put("map-reduce", MapReduceJob.class);
		map.put("sqoop", SqoopProcessJob.class);
		map.put("indexfile", IndexFileProcessJob.class);

		JobWrappingFactory factory = new JobWrappingFactory(permitManager,
				new ReadWriteLockManager(), cl.getLogDir().getAbsolutePath(),
				"java", map);

		JobManager jobManager = new JobManager(factory, cl.getLogDir()
				.getAbsolutePath(), cl.getDefaultProps(), cl.getJobDirs(),
				cl.getClassloader(), null);

		File executionsStorageFile = new File("executions");
		if (!executionsStorageFile.exists()) {
			executionsStorageFile.mkdirs();
		}

		long lastId = 0;
		for (File file : executionsStorageFile.listFiles()) {
			final String filename = file.getName();
			if (filename.endsWith(".json")) {
				try {
					lastId = Math.max(
							lastId,
							Long.parseLong(filename.substring(0,
									filename.length() - 5)));
				} catch (NumberFormatException e) {
				}
			}
		}

		final ExecutableFlowSerializer flowSerializer = new DefaultExecutableFlowSerializer();
		final ExecutableFlowDeserializer flowDeserializer = new DefaultExecutableFlowDeserializer(
				jobManager, factory);
		FlowExecutionSerializer flowExecutionSerializer = new FlowExecutionSerializer(
				flowSerializer);
		FlowExecutionDeserializer flowExecutionDeserializer = new FlowExecutionDeserializer(
				flowDeserializer);

		FlowManager allFlows = new RefreshableFlowManager(jobManager,
				flowExecutionSerializer, flowExecutionDeserializer,
				executionsStorageFile, lastId);
		jobManager.setFlowManager(allFlows);

		final CountDownLatch countDown = new CountDownLatch(jobNames.size());

		for (String jobName : jobNames) {
			try {
				final ExecutableFlow flowToRun = allFlows
						.createNewExecutableFlow(jobName);

				if (flowToRun == null) {
					System.out.printf("Job[%s] is unknown.  Not running.%n",
							jobName);

					countDown.countDown();
					continue;
				} else {
					System.out.println("Running " + jobName);
				}

				if (ignoreDeps) {
					for (ExecutableFlow flow : flowToRun.getChildren()) {
						flow.markCompleted();
					}
				}
				

				flowToRun.execute(overrideProps, new FlowCallback() {
					@Override
					public void progressMade() {
					}

					@Override
					public void completed(Status status) {
						if (status == Status.FAILED) {
							System.out.printf("Job failed.%n");
							final Map<String, Throwable> exceptions = flowToRun
									.getExceptions();

							if (exceptions == null || exceptions.isEmpty()) {
								System.out
										.println("flowToRun.getExceptions() was null/empty when it should not have been.  Please notify the Azkaban developers.");
							}

							int errorNum = 1;
							for (Entry<String, Throwable> entry : exceptions
									.entrySet()) {
								String name = entry.getKey();
								System.err.println(errorNum + ".  " + name
										+ "\n");
								entry.getValue().printStackTrace(System.err);
								System.err.println("\n\n");
								errorNum++;
							}

						}

						countDown.countDown();
					}
				});
			} catch (Exception e) {
				System.out.println("Failed to run job '" + jobName + "':");
				e.printStackTrace();
			}
		}

		countDown.await();
	}
	
	/**
	 * Add generic properties like date ....
	 * @return
	 */
	private static Props addGenericProperties() {
		Props overrides = new Props();
		SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyyMMdd");
		overrides.put("now", simpleDateformat.format(new Date()));
		return overrides;
	}
}
