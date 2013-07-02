package org.bigloupe.web.hdfs;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;
import org.bigloupe.jobclient.service.JobTrackerClientService;
import org.springframework.stereotype.Component;

/**
 * Manage an array list of FileSystem ({@link FileSystem}
 * Singleton
 * 
 * @author bigloupe
 * 
 */
@Component
public class JobTrackerClientManager {
	
	private static Map<String, JobTrackerClientService> jobTrackerClientMap = new HashMap<String, JobTrackerClientService>();
	
	public void addJobTrackerClientService(JobTrackerClientService jobTrackerClient) {
		jobTrackerClientMap.put(jobTrackerClient.getJobTrackerServiceName(), jobTrackerClient);
	}
	
	public static JobTrackerClientService getJobTrackerClient(String jobTrackerServiceName) {
		return jobTrackerClientMap.get(jobTrackerServiceName);
	}

	
}
