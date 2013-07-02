package org.bigloupe.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.bigloupe.web.BigLoupeConfiguration;
import org.bigloupe.web.scheduler.InitializeScheduler;
import org.bigloupe.web.service.common.MailerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;


public abstract class AbstractSchedulerController extends AbstractConfigurationController {

	public static final String SCHEDULER = "SCHEDULER";
	
	private InitializeScheduler initScheduler;
	
	@Autowired
	MailerService mailerService;

	public InitializeScheduler getInitScheduler(ServletContext servletContext) {
		initScheduler = (InitializeScheduler)servletContext.getAttribute(SCHEDULER);
		if (initScheduler == null) {
			String schedulerDirectory = servletContext
					.getRealPath(configuration.getSchedulerDirectory());
			List<File> jobDirs = new ArrayList<File>();
			jobDirs.add(new File(schedulerDirectory + "/jobs"));
			File logDir = new File(schedulerDirectory + "/logs");
			File tempDir = new File(schedulerDirectory + "/temp");
			try {
				initScheduler = new InitializeScheduler(jobDirs, logDir, tempDir,
						mailerService, configuration, true);
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalStateException("Scheduler not initialized");
			}
			servletContext.setAttribute(SCHEDULER, initScheduler);
		}
		return initScheduler;
	}
	
	public void setInitScheduler(InitializeScheduler initScheduler) {
		this.initScheduler = initScheduler;
	}
	
	
}
