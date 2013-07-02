package org.bigloupe.web.monitor.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bigloupe.web.monitor.service.DAGNode;
import org.bigloupe.web.monitor.service.DAGTransformer;
import org.bigloupe.web.monitor.util.JSONUtil;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

public class RemoteStatsWriteServiceTest {

	public static void main(String[] args) throws Exception {
		RemoteStatsWriteService remoteStatsWriteService = new RemoteStatsWriteService(
				"http://localhost:9090");
		String workflowId = "444";

		String sourceFile = "src/test/resources/data/map-reduce-statistics/large-dag.json";
		String json = JSONUtil.readFile(sourceFile);

		List<DAGNode> nodes = (List<DAGNode>) JSONUtil.readJson(json,
				new TypeReference<List<DAGNode>>() {
				});
		DAGTransformer dagTransformer = new SugiyamaLayoutTransformer(true);
		dagTransformer.transform(nodes);

		Map<String, DAGNode> dagNodeNameMap = new HashMap<String, DAGNode>();
		dagNodeNameMap.put(workflowId, nodes.get(0));
		
		remoteStatsWriteService.sendDagNodeNameMap(workflowId, dagNodeNameMap);
	}

}
