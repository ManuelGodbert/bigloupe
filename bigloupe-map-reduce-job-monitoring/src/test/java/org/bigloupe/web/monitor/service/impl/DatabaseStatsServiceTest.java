package org.bigloupe.web.monitor.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bigloupe.web.monitor.pig.AmbrosePigProgressNotificationListener.WorkflowProgressField;
import org.bigloupe.web.monitor.service.DAGNode;
import org.bigloupe.web.monitor.service.WorkflowEvent;
import org.bigloupe.web.monitor.service.WorkflowEvent.EVENT_TYPE;
import org.bigloupe.web.monitor.util.JSONUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.type.TypeReference;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/applicationContext-database-test.xml"})
public class DatabaseStatsServiceTest {
	@Autowired
	private DatabaseStatsService databaseStatsService;

	@Test
	public void testDatabaseStatsEventService() throws Exception {
		Map<WorkflowProgressField, String> eventData = new HashMap<WorkflowProgressField, String>();
		eventData.put(WorkflowProgressField.workflowProgress, Integer.toString(1));
		WorkflowEvent event = new WorkflowEvent(EVENT_TYPE.JOB_PROGRESS, eventData, null);
		
		// Push an event workflowId = 1 
		databaseStatsService.pushEvent("1", event);
		
		// Retrieve event from database workflowId = 1
		Collection<WorkflowEvent> events = databaseStatsService.getEventsSinceId("1", 0);
		Assert.assertEquals(1, events.size());
		System.out.println("Events : #" + events.size());
		for (WorkflowEvent workflowEvent : events) {
			System.out.println(workflowEvent.toString());
		}
		
	}
	
	@Test
	public void testDatabaseStatsDagService() throws Exception {
		String workflowId = "1";
		
		Map<String, DAGNode> dagNodeNameMap = new HashMap<String, DAGNode>();
		String json = JSONUtil
				.readFile("src/test/resources/data/map-reduce-statistics/large-dag.json");
		List<DAGNode> dagNodeList = (List<DAGNode>) JSONUtil.readJson(
				json, new TypeReference<List<DAGNode>>() {
				});
		for (DAGNode dagNode : dagNodeList) {
			dagNodeNameMap.put(dagNode.getJobId(), dagNode );	
		}
		
		databaseStatsService.sendDagNodeNameMap(workflowId, dagNodeNameMap);
		
		Map<String, DAGNode> dagNodeNameMapFromDatabase = databaseStatsService.getDagNodeNameMap(workflowId);
		compareDagNodeName(dagNodeNameMap, dagNodeNameMapFromDatabase);		
	}

	private void compareDagNodeName(Map<String, DAGNode> dagNodeNameMap1,
			Map<String, DAGNode> dagNodeNameMap2) {
		Set<Entry<String, DAGNode>> dagNodeNameSet1 = dagNodeNameMap1.entrySet();
		for (Entry<String, DAGNode> entrydagNodeNameSet1 : dagNodeNameSet1) {
			String key1 = entrydagNodeNameSet1.getKey();
			DAGNode dagNodeFromMap2 = dagNodeNameMap2.get(key1);
			Assert.assertEquals(entrydagNodeNameSet1.getValue(), dagNodeFromMap2);
		}
		
		
	}
}
