package org.bigloupe.web.scheduler.workflow.flow;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import org.bigloupe.web.util.Props;



public class FlowNode {
	public static final String NORMAL = "normal";
	public static final String DISABLED = "disabled";
	public static final String RUNNING = "running";
	public static final String FAILED = "failed";
	public static final String SUCCEEDED = "succeeded";
	
	private String alias;
	private Set<String> dependencies = new HashSet<String>();
	private Set<String> dependents = new HashSet<String>();
	private Props prop = new Props();
	private int level = 0;
	private String status = NORMAL;
	private Point2D position = new Point2D.Double();
	
	public FlowNode(String alias) {
		this.alias = alias;
	}
	
	public FlowNode(FlowNode other) {
		this.alias = other.alias;
		this.dependencies.addAll(other.dependencies);
		this.dependents.addAll(other.dependents);
		this.prop = Props.clone(other.getProps());
		this.level = other.level;
		this.status = other.status;
		this.position = other.position;
	}
	
	public void setProps(Props prop) {
		this.prop = prop;
	}
	
	public Props getProps() {
		return this.prop;
	}
	
	public void setDependencies(Set<String> dependencies) {
		this.dependencies = dependencies;
	}
	
	public Set<String> getDependencies() {
		return dependencies;
	}
	
	public Set<String> getDependents() {
		return dependents;
	}
	
	public void addDependent(String dependent) {
		dependents.add(dependent);
	}
	
	public String getAlias() {
		return alias;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public void setPosition(double x, double y) {
		position = new Point2D.Double(x, y);
	}
	
	public double getX() {
		return position.getX();
	}
	
	public double getY() {
		return position.getY();
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
}