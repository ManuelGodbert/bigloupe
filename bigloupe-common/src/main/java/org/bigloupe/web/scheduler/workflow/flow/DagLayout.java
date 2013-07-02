package org.bigloupe.web.scheduler.workflow.flow;

import org.bigloupe.web.scheduler.workflow.Flow;

public class DagLayout {
	public final Flow flow;
	
	public DagLayout(Flow flow) {
		this.flow = flow;
	}
	
	public void setLayout() {
		double x = 0;
		double y = 0;

		for(FlowNode node : flow.getFlowNodes()) {
			node.setPosition(x += 100, y += 100);
		}

	}

}