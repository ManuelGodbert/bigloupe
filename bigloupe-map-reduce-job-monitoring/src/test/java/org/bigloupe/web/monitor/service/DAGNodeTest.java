package org.bigloupe.web.monitor.service;

import java.io.IOException;
import java.util.List;

import org.bigloupe.web.monitor.service.impl.SugiyamaLayoutTransformer;
import org.bigloupe.web.monitor.util.JSONUtil;

import com.fasterxml.jackson.core.type.TypeReference;

public class DAGNodeTest {
	  @SuppressWarnings("unchecked")
	  public static void main(String[] args) throws IOException {
	    String sourceFile = "src/test/resources/data/map-reduce-statistics/large-dag.json";
	    String json = JSONUtil.readFile(sourceFile);
	    List<DAGNode> nodes =
	      (List<DAGNode>)JSONUtil.readJson(json, new TypeReference<List<DAGNode>>() { });
	    DAGTransformer dagTransformer = new SugiyamaLayoutTransformer(true);
	    dagTransformer.transform(nodes);

	    JSONUtil.writeJson(sourceFile + "2", nodes);
	  }
}
