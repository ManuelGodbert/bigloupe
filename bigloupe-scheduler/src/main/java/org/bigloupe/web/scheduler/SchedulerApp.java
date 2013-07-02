package org.bigloupe.web.scheduler;

import java.io.File;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.log4j.Logger;
import org.bigloupe.web.scheduler.job.SchedulerCommandLine;


public class SchedulerApp {
	
	  private static final Logger logger = Logger.getLogger(SchedulerApp.class);

	/**
	 * @param args
	 */
	public static void main(String[] arguments) throws Exception {
	     OptionParser parser = new OptionParser();
	      String devModeOpt = "dev-mode";
	      parser.accepts(devModeOpt, "Enable developer friendly options.");

	      SchedulerCommandLine cl = new SchedulerCommandLine(parser, arguments);
	      OptionSet options = cl.getOptions();
	      
	      if(cl.hasHelp())
	          cl.printHelpAndExit("USAGE: java -jar bigloupe.war", System.out);
	      if(cl.getJobDirs().size() == 0)
	          cl.printHelpAndExit("No job directory given.", System.out);

	      logger.info("Job log directory set to " + cl.getLogDir().getAbsolutePath());
	      logger.info("Job directories set to " + cl.getJobDirs());

	      InitializeScheduler initScheduler = new InitializeScheduler(cl.getJobDirs(), cl.getLogDir(), new File("temp"), null, null, options.has(devModeOpt));

	}

}
