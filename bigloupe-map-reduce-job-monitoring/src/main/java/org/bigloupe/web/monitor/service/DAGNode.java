package org.bigloupe.web.monitor.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Class that represents a Job node in the DAG. The job name must not be null.
 * At DAG creation time the jobID will probably be null. Ideally this will be
 * set on the node when the job is started, and the node will be sent as a
 * 
 * <pre>
 * WorkflowEvent.EVENT_TYPE.JOB_STARTED
 * </pre>
 * 
 * event.
 * 
 * This class can be converted to JSON as-is by doing something like this:
 * 
 * ObjectMapper om = new ObjectMapper();
 * om.getSerializationConfig().set(SerializationConfig.Feature.INDENT_OUTPUT,
 * true); String json = om.writeValueAsString(dagNode);
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DAGNode implements Serializable {
	
	private static final long serialVersionUID = 7619012272499454138L;
	
	private String name;
	private String[] aliases;
	private String[] features;
	private String jobId;
	private Collection<DAGNode> successors;
	private Collection<String> successorNames;
	private String runtime;
	private Integer dagLevel;
	private Double x, y;

	public DAGNode(String name, String[] aliases, String[] features,
			String runtime) {
		this.name = name;
		this.aliases = aliases;
		this.features = features;
		this.runtime = runtime;
	}

	@JsonCreator
	public DAGNode(@JsonProperty("name") String name,
			@JsonProperty("aliases") String[] aliases,
			@JsonProperty("features") String[] features,
			@JsonProperty("jobId") String jobId,
			@JsonProperty("successorNames") Collection<String> successorNames,
			@JsonProperty("runtime") String runtime) {
		this.name = name;
		this.aliases = aliases;
		this.features = features;
		this.jobId = jobId;
		this.successorNames = successorNames;
		this.runtime = runtime;
	}

	public String getName() {
		return name;
	}

	public String[] getAliases() {
		return aliases == null ? new String[0] : aliases;
	}

	public String[] getFeatures() {
		return features == null ? new String[0] : features;
	}

	public String getRuntime() {
		return runtime;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public Integer getDagLevel() {
		return dagLevel;
	}

	public void setDagLevel(Integer dagLevel) {
		this.dagLevel = dagLevel;
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	@JsonIgnore
	public synchronized Collection<DAGNode> getSuccessors() {
		return successors;
	}

	public synchronized void setSuccessors(Collection<DAGNode> successors) {
		Collection<String> successorNames = new HashSet<String>();
		if (successors != null) {
			for (DAGNode node : successors) {
				successorNames.add(node.getName());
			}
		}
		this.successors = successors;
		this.successorNames = successorNames;
	}

	public synchronized Collection<String> getSuccessorNames() {
		return successorNames;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((jobId == null) ? 0 : jobId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DAGNode other = (DAGNode) obj;
		if (jobId == null) {
			if (other.jobId != null)
				return false;
		} else if (!jobId.equals(other.jobId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
