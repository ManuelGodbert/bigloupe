package org.bigloupe.web.scheduler.workflow;

import org.bigloupe.web.util.Props;

/**
 * Basic unit of work.
 * 
 * @author Richard B Park
 */
public abstract class WorkUnit {
	private final String id;
	private Props prop;
	
	public WorkUnit(String id, WorkUnit toClone) {
		this.id = id;
		prop = Props.clone(toClone.getProps());
	}
	
	public WorkUnit(String id, Props prop) {
		this.id = id;
		this.prop = prop;
	}
	
	public String getId() {
		return id;
	}
	
	public Props getProps() {
		return prop;
	}
}
