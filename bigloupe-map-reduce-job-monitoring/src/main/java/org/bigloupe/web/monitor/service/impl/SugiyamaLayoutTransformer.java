package org.bigloupe.web.monitor.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.bigloupe.web.monitor.service.DAGNode;
import org.bigloupe.web.monitor.service.DAGTransformer;
import org.bigloupe.web.scheduler.workflow.Flow;
import org.bigloupe.web.scheduler.workflow.flow.DagLayout;
import org.bigloupe.web.scheduler.workflow.flow.FlowNode;
import org.bigloupe.web.scheduler.workflow.flow.SugiyamaLayout;
import org.bigloupe.web.util.Props;


/**
 * Transformer that wraps Azkaban's SugiyamaLayout to create level, X and Y values for the nodes in
 * the DAG.
 * @author billg
 */
public class SugiyamaLayoutTransformer implements DAGTransformer {

  private boolean landscape;

  /**
   * Create an instance of this class to generate top-down coordinates
   */
  public SugiyamaLayoutTransformer() {
    this(false);
  }

  /**
   * Create an instance of this class to generate coordinates. If <pre>landscape=true</pre>, then
   * the graph will layout from left to right. Otherwise it will layout from top to bottom.
   */
  public SugiyamaLayoutTransformer(boolean landscape) {
    this.landscape = landscape;
  }

  @Override
  public Collection<DAGNode> transform(Collection<DAGNode> nodes) {
    Flow flow = new Flow("sample flow", new Props());

    if(nodes.size() == 1) {
      flow.addDependencies(nodes.iterator().next().getName(), new ArrayList<String>());
    } else {
      for(DAGNode node : nodes) {
        for(String successor : node.getSuccessorNames()) {
          flow.addDependencies(successor, Arrays.asList(node.getName()));
        }
      }
    }

    flow.validateFlow(); // this sets levels
    flow.printFlow();
    if (!flow.isLayedOut()) {
      DagLayout layout = new SugiyamaLayout(flow);
      layout.setLayout();
    }

    for(DAGNode node : nodes) {
      FlowNode flowNode = flow.getFlowNode(node.getName());

      // invert X/Y if we're rendering in landscape
      if (landscape) {
        node.setX(flowNode.getY());
        node.setY(flowNode.getX());
      }
      else {
        node.setX(flowNode.getX());
        node.setY(flowNode.getY());
      }

      node.setDagLevel(flowNode.getLevel());
    }

    return nodes;
  }
}
