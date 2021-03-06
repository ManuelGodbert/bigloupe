package org.bigloupe.web.scheduler.workflow.flow;

import org.bigloupe.web.util.Props;

public class ExecutingFlowNode extends FlowNode {
	private Props referenceProps;
	private Props resolvedProps;
	
	public ExecutingFlowNode(FlowNode source) {
		super(source);
	}
	
	public Props getReferenceProps() {
		return referenceProps;
	}
	
	public void setReferenceProps(Props reference) {
		this.referenceProps = reference;
	}

	public void setResolvedProps(Props resolvedProps) {
		this.resolvedProps = resolvedProps;
	}

	public Props getResolvedProps() {
		return resolvedProps;
	}
}