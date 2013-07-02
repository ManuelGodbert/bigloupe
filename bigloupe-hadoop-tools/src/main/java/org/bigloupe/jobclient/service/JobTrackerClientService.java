package org.bigloupe.jobclient.service;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobID;
import org.apache.hadoop.mapred.JobStatus;
import org.apache.hadoop.mapred.RunningJob;
import org.bigloupe.web.BigLoupeConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class JobTrackerClientService {

	JobClient jobClient;
	Configuration hadoopConfiguration;
	
	String jobTrackerServiceName;
	
	@Autowired
	BigLoupeConfiguration configuration;

	public String getJobTrackerServiceName() {
		return jobTrackerServiceName;
	}

	public String getJobTrackerWebPort() {
		String port = hadoopConfiguration.get("mapred.job.tracker.http.address");
		String jobTrackerAddress = hadoopConfiguration.get("mapred.job.tracker");
		String jobTrackerLocation;
		if ((jobTrackerAddress != null) && (port != null))
			jobTrackerLocation = jobTrackerAddress.substring(0,jobTrackerAddress.indexOf(":")) 
                + port.substring(port.indexOf(":"));
		else
			jobTrackerLocation = "#";
		return jobTrackerLocation;
	}
	
	public JobTrackerClientService(Configuration hadoopConfiguration) throws IOException {
		this.hadoopConfiguration = hadoopConfiguration;
		String jobTracker = hadoopConfiguration.get("mapred.job.tracker");
		// Found host and port
		if ((jobTracker == null) || (!jobTracker.contains(":")))
			throw new IllegalArgumentException(
					"Error : JobTracker information is mandatory in mapred-site.xml");
		else {
			String hostJobTracker = jobTracker.substring(0,
					jobTracker.indexOf(':'));
			int portJobTracker = Integer.parseInt(jobTracker.substring(
					jobTracker.indexOf(':')+1, jobTracker.length()));

			jobClient = new JobClient(new InetSocketAddress(hostJobTracker,
					portJobTracker), hadoopConfiguration);
			this.jobTrackerServiceName = jobTracker;
		}
	}

	public JobStatus[] listJob() throws IOException {
		return jobClient.getAllJobs();
	}
	
	// NOT USED
	public void getInformationJob(String jobId) throws IllegalArgumentException, IOException {
		RunningJob theJob = jobClient.getJob(JobID.forName(jobId));
		float mapProgress = theJob.mapProgress(); // similar for reduceProgress
	}
	
	public void getRunningJob() throws IOException {
		 JobStatus[] runningJobs = jobClient.getAllJobs();
	}	


}
