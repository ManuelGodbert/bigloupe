package org.bigloupe.web.scheduler.workflow;

import org.bigloupe.web.util.Props;

/**
 * A discrete job to be run.
 *  
 * @author Richard B Park
 */
public class Job extends WorkUnit {
	public static final String REFERENCE_KEY = "reference";
	public static final String TYPE_KEY = "type";
	public static final String DEPENDENCY_KEY = "dependencies";
	private long timestamp;
	
	public Job(String id, Job toClone) {
		super(id, toClone);
		this.timestamp = toClone.timestamp;
	}
	
	public Job(String id, Props prop) {
		super(id, prop);
	}

	public String getType() {
		return getProps().get(TYPE_KEY);
	}
	
	public String getReference() {
		return getProps().get(REFERENCE_KEY);
	}
	
	public void setLastUpdateTime(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public long getLastUpdateTime() {
		return timestamp;
	}
}
